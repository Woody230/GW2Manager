package com.bselzer.gw2.manager.android.common

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.bselzer.gw2.manager.common.expect.AndroidApp
import org.kodein.di.DI
import org.kodein.di.DIAware

/**
 * The application used for initialization.
 */
class AppInitializer : Application(), DIAware {
    private companion object {
        /**
         * The default preferences datastore.
         */
        val Context.DATASTORE: DataStore<Preferences> by preferencesDataStore("default")
    }

    override val di: DI = AndroidApp(this) { DATASTORE }.di
}