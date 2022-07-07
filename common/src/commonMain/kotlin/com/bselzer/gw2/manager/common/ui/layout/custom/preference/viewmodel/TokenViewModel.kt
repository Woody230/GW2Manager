package com.bselzer.gw2.manager.common.ui.layout.custom.preference.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import com.bselzer.gw2.manager.common.AppResources
import com.bselzer.gw2.manager.common.ui.base.AppComponentContext
import com.bselzer.gw2.manager.common.ui.base.ViewModel
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.token.TokenLogic
import com.bselzer.gw2.manager.common.ui.layout.custom.preference.model.token.TokenResources
import com.bselzer.gw2.v2.client.model.Token
import com.bselzer.gw2.v2.model.account.token.TokenInfo
import com.bselzer.gw2.v2.resource.Gw2Resources
import com.bselzer.gw2.v2.scope.core.Permission
import com.bselzer.ktx.db.transaction.transaction
import com.bselzer.ktx.intent.browser.Browser
import com.bselzer.ktx.logging.Logger
import com.bselzer.ktx.resource.KtxResources
import com.bselzer.ktx.settings.nullState
import com.bselzer.ktx.settings.setting.Setting
import dev.icerock.moko.resources.desc.Raw
import dev.icerock.moko.resources.desc.StringDesc
import dev.icerock.moko.resources.desc.desc
import org.kodein.db.asModelSequence
import org.kodein.db.find

class TokenViewModel(
    context: AppComponentContext,
) : ViewModel(context) {
    private val setting: Setting<String> = preferences.common.token
    private val intermediary = mutableStateOf<Token?>(null)

    val resources
        @Composable
        get() = TokenResources(
            image = Gw2Resources.images.black_lion_key,
            title = KtxResources.strings.token.desc(),
            subtitle = subtitle(),
            dialogSubtitle = AppResources.strings.token_description.desc(),
            dialogInput = StringDesc.Raw(intermediary.value?.toString() ?: ""),
            hyperlink = AppResources.strings.token_hyperlink.desc(),
            failure = AppResources.strings.token_failure.desc(),
        )

    @Composable
    private fun subtitle(): StringDesc {
        val token = setting.nullState().value
        return when {
            token.isNullOrBlank() -> KtxResources.strings.not_set.desc()
            else -> {
                // The token info id is only the first GUID so a startsWith is required.
                val tokenInfo = database.find<TokenInfo<*>>().all().asModelSequence().firstOrNull { info -> token.startsWith(info.id.value) }

                // Try to use the name first as the most user friendly.
                // Since the id is not the full token, try to use that as a default otherwise.
                StringDesc.Raw(tokenInfo?.name ?: tokenInfo?.id?.value ?: "")
            }
        }
    }

    val logic = TokenLogic(
        updateInput = { value -> intermediary.value = value?.let { Token(it.trim()) } },
        clearInput = { intermediary.value = null },
        onReset = { setting.remove() },
        onClickHyperlink = { Browser.open(it) },
        onSave = ::onSave
    )

    private suspend fun onSave(): Boolean {
        // Validate the new token before committing it.
        val newValue = intermediary.value?.toString()?.trim()
        if (newValue.isNullOrBlank()) {
            intermediary.value = null
            return false
        }

        val token = Token(newValue)
        val tokenInfo = clients.gw2.token.information(token = token)
        if (tokenInfo.id.isDefault) {
            Logger.d { "Rejecting default token information." }
            return false
        }

        return initializePreferences(token, tokenInfo)
    }

    /**
     * Set up other preferences based on the [tokenInfo].
     */
    private suspend fun initializePreferences(token: Token, tokenInfo: TokenInfo<*>): Boolean {
        val permissions = tokenInfo.permissions
        Logger.d { "Token $token permissions: $permissions" }

        // TODO scope processor to automatically verify permissions
        return when {
            permissions.contains(Permission.ACCOUNT) -> initializeAccountPreferences(token, tokenInfo)
            else -> false
        }
    }

    private suspend fun initializeAccountPreferences(
        token: Token,
        tokenInfo: TokenInfo<*>
    ): Boolean {
        val account = clients.gw2.account.account(token)
        if (account.id.isDefault) {
            return false
        }

        // Ensure the token info exists before updating the token so that it will be available for recomposition.
        database.transaction().use {
            put(tokenInfo)
        }

        setting.set(token.value)
        Logger.i { "Set client token to $token" }

        preferences.wvw.selectedWorld.initialize(account.world)
        return true
    }
}