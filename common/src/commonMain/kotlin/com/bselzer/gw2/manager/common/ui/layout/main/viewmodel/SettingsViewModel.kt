package com.bselzer.gw2.manager.common.ui.layout.main.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import com.bselzer.gw2.manager.common.AppResources
import com.bselzer.gw2.manager.common.dependency.LocalTheme
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.WvwResources
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.interval.WvwIntervalLogic
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.interval.WvwIntervalResources
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.language.LanguageLogic
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.language.LanguageResources
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.map.MapLabelLogic
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.map.MapLabelResources
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.map.ZoomLogic
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.map.ZoomResources
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.theme.ThemeLogic
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.theme.ThemeResources
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.token.TokenLogic
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.token.TokenResources
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.viewmodel.ColorViewModel
import com.bselzer.gw2.manager.common.ui.theme.Theme
import com.bselzer.gw2.v2.client.model.Token
import com.bselzer.gw2.v2.model.account.token.TokenInfo
import com.bselzer.gw2.v2.resource.Gw2Resources
import com.bselzer.gw2.v2.scope.core.Permission
import com.bselzer.ktx.datetime.format.DurationBound
import com.bselzer.ktx.intent.browser.Browser
import com.bselzer.ktx.intl.Locale
import com.bselzer.ktx.kodein.db.transaction.transaction
import com.bselzer.ktx.logging.Logger
import com.bselzer.ktx.resource.KtxResources
import com.bselzer.ktx.resource.strings.stringResource
import com.bselzer.ktx.resource.strings.stringResourceOrNull
import com.bselzer.ktx.settings.compose.defaultState
import com.bselzer.ktx.settings.compose.nullState
import com.bselzer.ktx.settings.compose.safeState
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
    override val title: StringDesc = KtxResources.strings.settings.desc()

    val themeResources
        @Composable
        get() = ThemeResources(
            image = when (LocalTheme.current) {
                Theme.LIGHT -> Gw2Resources.images.sunrise
                Theme.DARK -> Gw2Resources.images.twilight
            },
            title = KtxResources.strings.theme.desc(),
            subtitle = when (LocalTheme.current) {
                Theme.LIGHT -> KtxResources.strings.light
                Theme.DARK -> KtxResources.strings.dark
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
            image = Gw2Resources.images.black_lion_key,
            title = KtxResources.strings.token.desc(),
            subtitle = preferences.common.token.nullState().value.let { token ->
                if (token.isNullOrBlank()) {
                    KtxResources.strings.not_set.desc()
                } else {
                    // The token info id is only the first GUID so a startsWith is required.
                    val tokenInfo = database.find<TokenInfo<*>>().all().asModelSequence().firstOrNull { info -> token.startsWith(info.id.value) }

                    // Try to use the name first as the most user friendly.
                    // Since the id is not the full token, try to use that as a default otherwise.
                    StringDesc.Raw(tokenInfo?.name ?: tokenInfo?.id?.value ?: "")
                }
            },
            dialogSubtitle = AppResources.strings.token_description.desc(),
            dialogInput = StringDesc.Raw(token.value?.toString() ?: ""),
            hyperlink = AppResources.strings.token_hyperlink.desc(),
            failure = AppResources.strings.token_failure.desc(),
        )

    val tokenLogic = TokenLogic(
        updateInput = { value -> token.value = value?.let { Token(it.trim()) } },
        clearInput = { token.value = null },
        onReset = { preferences.common.token.remove() },
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
    private suspend fun initializePreferences(token: Token, tokenInfo: TokenInfo<*>): Boolean {
        val permissions = tokenInfo.permissions
        Logger.d { "Token $token permissions: $permissions" }

        // TODO scope processor to automatically verify permissions
        if (permissions.contains(Permission.ACCOUNT)) {
            val account = clients.gw2.account.account(token)
            if (!account.id.isDefault) {
                // Ensure the token info exists before updating the token so that it will be available for recomposition.
                database.transaction().use {
                    put(tokenInfo)
                }

                preferences.common.token.set(token.value)
                Logger.i { "Set client token to $token" }

                preferences.wvw.selectedWorld.initialize(account.world)
                return true
            }
        }

        return false
    }

    val wvwResources
        @Composable
        get() = run {
            val interval = preferences.wvw.refreshInterval.defaultState().value
            WvwResources(
                image = Gw2Resources.images.rank_dolyak,
                title = Gw2Resources.strings.wvw.desc(),
                interval = WvwIntervalResources(
                    image = Gw2Resources.images.concentration,
                    title = KtxResources.strings.refresh_interval.desc(),

                    // The notation given should be acceptable for all of the supported localizations.
                    subtitle = interval.toString().desc(),
                    label = { unit -> unit.stringResource().desc() }
                )
            )
        }

    // TODO default to the actual preference instead of initial?
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
            onReset = { preferences.wvw.refreshInterval.remove() },
            clearInput = {
                intervalAmount.value = preferences.wvw.refreshInterval.initialAmount
                intervalUnit.value = preferences.wvw.refreshInterval.initialUnit
            }
        )

    private val language: MutableState<Locale?> = mutableStateOf(null)
    val languageResources
        @Composable
        get() = LanguageResources(
            image = KtxResources.images.ic_language,
            title = KtxResources.strings.language.desc(),
            subtitle = (preferences.common.locale.safeState().value.stringResourceOrNull() ?: KtxResources.strings.locale_en).desc(),
            getLabel = { locale -> locale.stringResourceOrNull()?.desc() ?: "".desc() }
        )

    val languageLogic
        get() = LanguageLogic(
            values = languages,
            selected = { language.value ?: preferences.common.locale.safeState().value },
            onSave = {
                language.value?.let { locale -> updateLocale(locale) }
            },
            onReset = { resetLocale() },
            updateSelection = { language.value = it },
            resetSelection = { language.value = null }
        )

    private val zoom: MutableState<Int?> = mutableStateOf(null)
    val zoomResources
        @Composable
        get() = ZoomResources(
            image = Gw2Resources.images.gift_of_exploration,
            title = AppResources.strings.default_zoom_level.desc(),
            subtitle = preferences.wvw.zoom.safeState().value.toString().desc(),
        )

    val zoomLogic: ZoomLogic
        get() {
            val range = repositories.selectedWorld.zoomRange
            return ZoomLogic(
                // TODO default to the actual preference instead of initial?
                amount = zoom.value ?: preferences.wvw.zoom.defaultValue,
                amountRange = range,
                onValueChange = { zoom.value = it.coerceIn(range) },
                onSave = {
                    zoom.value?.let { updateZoom(it) }
                },
                onReset = { updateZoom(preferences.wvw.zoom.defaultValue) },
                clearInput = { zoom.value = null }
            )
        }

    private suspend fun updateZoom(zoom: Int) {
        val bounded = zoom.coerceIn(repositories.selectedWorld.zoomRange)
        preferences.wvw.zoom.set(bounded)
        repositories.selectedWorld.updateZoom(bounded)
    }

    val mapLabelResources
        @Composable
        get() = MapLabelResources(
            image = Gw2Resources.images.gift_of_exploration,
            title = AppResources.strings.team_label.desc(),
            subtitle = preferences.wvw.showMapLabel.safeState().value.stringResource().desc()
        )

    val mapLabelLogic
        @Composable
        get() = run {
            val scope = rememberCoroutineScope()
            MapLabelLogic(
                checked = preferences.wvw.showMapLabel.safeState().value,
                onCheckedChange = { checked ->
                    scope.launch { preferences.wvw.showMapLabel.set(checked) }
                }
            )
        }

    val colors: List<ColorViewModel>
        get() = mapTypes.map { mapType ->
            ColorViewModel(
                context = this,
                mapType = mapType
            )
        }
}