package com.bselzer.gw2.manager.common.ui.layout.main.viewmodel

import androidx.compose.runtime.*
import com.bselzer.gw2.manager.common.Gw2Resources
import com.bselzer.gw2.manager.common.dependency.LocalTheme
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.layout.main.model.settings.*
import com.bselzer.gw2.manager.common.ui.theme.Theme
import com.bselzer.gw2.v2.client.model.Token
import com.bselzer.gw2.v2.model.account.token.TokenInfo
import com.bselzer.gw2.v2.scope.core.Permission
import com.bselzer.ktx.datetime.format.DurationBound
import com.bselzer.ktx.intent.browser.Browser
import com.bselzer.ktx.logging.Logger
import com.bselzer.ktx.resource.Resources
import com.bselzer.ktx.resource.strings.stringResource
import com.bselzer.ktx.settings.compose.defaultState
import com.bselzer.ktx.settings.compose.nullState
import dev.icerock.moko.resources.desc.Raw
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import kotlinx.coroutines.launch
import org.kodein.db.asModelSequence
import org.kodein.db.find
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class SettingsViewModel(context: AppComponentContext) : MainViewModel(context) {
    override val title: StringDesc = Resources.strings.settings.desc()

    val themeResources
        @Composable
        get() = ThemeResources(
            image = when (LocalTheme.current) {
                Theme.LIGHT -> Gw2Resources.images.gw2_sunrise
                Theme.DARK -> Gw2Resources.images.gw2_twilight
            },
            title = Resources.strings.theme.desc(),
            subtitle = when (LocalTheme.current) {
                Theme.LIGHT -> Resources.strings.light
                Theme.DARK -> Resources.strings.dark
            }.desc()
        )

    val themeLogic
        @Composable
        get() = run {
            val scope = rememberCoroutineScope()
            ThemeLogic(
                checked = LocalTheme.current != Theme.LIGHT,
                onCheckedChange = { checked ->
                    scope.launch {
                        val theme = if (checked) Theme.DARK else Theme.LIGHT
                        preferences.common.theme.set(theme)
                    }
                }
            )
        }

    private val token = mutableStateOf<Token?>(null)
    val tokenResources
        @Composable
        get() = TokenResources(
            image = Gw2Resources.images.gw2_black_lion_key,
            title = Resources.strings.token.desc(),
            subtitle = preferences.common.token.nullState().value.let { token ->
                if (token.isNullOrBlank()) {
                    Resources.strings.not_set.desc()
                } else {
                    // The token info id is only the first GUID so a startsWith is required.
                    val tokenInfo = caches.database.find<TokenInfo>().all().asModelSequence().firstOrNull { info -> token.startsWith(info.id.value) }

                    // Try to use the name first as the most user friendly.
                    // Since the id is not the full token, try to use that as a default otherwise.
                    StringDesc.Raw(tokenInfo?.name ?: tokenInfo?.id?.value ?: "")
                }
            },
            dialogSubtitle = Gw2Resources.strings.token_description.desc(),
            dialogInput = StringDesc.Raw(token.value?.toString() ?: ""),
            hyperlink = Gw2Resources.strings.token_hyperlink.desc(),
            failure = Gw2Resources.strings.token_failure.desc(),
        )

    val tokenLogic = TokenLogic(
        updateInput = { value -> token.value = value?.let { Token(it.trim()) } },
        clearInput = { token.value = null },
        onReset = {
            token.value = null
            preferences.common.token.remove()
        },
        onClickHyperlink = { Browser.open(it) },
        onSave = {
            // Validate the new token before committing it.
            val newValue = token.value?.toString()?.trim()
            if (newValue.isNullOrBlank()) {
                token.value = null
                return@TokenLogic false
            }

            val token = Token(newValue)
            val tokenInfo = clients.gw2.token.information(token = token)
            if (tokenInfo.id.isDefault) {
                Logger.d { "Rejecting default token information." }
                return@TokenLogic false
            }

            initializePreferences(token, tokenInfo)
        },
    )

    /**
     * Set up other preferences based on the [tokenInfo].
     */
    private suspend fun initializePreferences(token: Token, tokenInfo: TokenInfo): Boolean {
        val permissions = tokenInfo.permissions
        Logger.d { "Token $token permissions: $permissions" }

        // TODO scope processor to automatically verify permissions
        if (permissions.contains(Permission.ACCOUNT)) {
            val account = clients.gw2.account.account(token)
            if (!account.id.isDefault) {
                // Ensure the token info exists before updating the token so that it will be available for recomposition.
                transaction {
                    caches.database.put(tokenInfo)
                }

                preferences.common.token.set(token.value)
                Logger.d { "Set client token to $token" }

                preferences.wvw.selectedWorld.initialize(account.world)
                return true
            }
        }

        return false
    }

    val wvwResources
        @Composable
        get() = run {
            val interval by preferences.wvw.refreshInterval.defaultState()
            WvwResources(
                image = Gw2Resources.images.gw2_rank_dolyak,
                title = Gw2Resources.strings.wvw.desc(),
                interval = WvwIntervalResources(
                    image = Gw2Resources.images.gw2_concentration,
                    title = Resources.strings.refresh_interval.desc(),

                    // The notation given should be acceptable for all of the supported localizations.
                    subtitle = StringDesc.Raw(interval.toString()),
                    label = { unit -> unit.stringResource().desc() }
                )
            )
        }

    private val intervalAmount: MutableState<Int> = mutableStateOf(preferences.wvw.refreshInterval.initialAmount)
    private val intervalUnit: MutableState<DurationUnit> = mutableStateOf(preferences.wvw.refreshInterval.initialUnit)
    private val intervalBound = DurationBound(min = 30.seconds)
    val intervalLogic
        get() = WvwIntervalLogic(
            amount = intervalBound.minBind(intervalAmount.value, intervalUnit.value),
            unit = intervalUnit.value,
            amountRange = intervalBound.minRange(intervalUnit.value),
            units = listOf(DurationUnit.SECONDS, DurationUnit.MINUTES, DurationUnit.HOURS, DurationUnit.DAYS),
            onValueChange = { amount, unit ->
                intervalAmount.value = intervalBound.minBind(amount, unit)
                intervalUnit.value = unit
            },
            onSave = {
                val unit = intervalUnit.value
                val amount = intervalBound.minBind(intervalAmount.value, unit)
                preferences.wvw.refreshInterval.set(amount.toDuration(unit))
            },
            onReset = { preferences.wvw.refreshInterval.remove() }
        )
}