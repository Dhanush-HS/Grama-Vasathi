package com.example.gramavasathi.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.gramavasathi.navigation.AppFlow
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.userFlowDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "user_flow"
)

interface UserFlowPreferences {
    val appFlow: Flow<AppFlow>
    suspend fun setAppFlow(flow: AppFlow)
}

@Singleton
class UserFlowPreferencesImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : UserFlowPreferences {

    private val keyFlow = stringPreferencesKey("app_flow")

    override val appFlow: Flow<AppFlow> = context.userFlowDataStore.data.map { prefs ->
        when (prefs[keyFlow]) {
            "host" -> AppFlow.Host
            else -> AppFlow.Guest
        }
    }

    override suspend fun setAppFlow(flow: AppFlow) {
        context.userFlowDataStore.edit { prefs ->
            prefs[keyFlow] = when (flow) {
                AppFlow.Host -> "host"
                AppFlow.Guest -> "guest"
            }
        }
    }
}
