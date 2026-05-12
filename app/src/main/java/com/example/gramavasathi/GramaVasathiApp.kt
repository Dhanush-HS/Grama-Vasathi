package com.example.gramavasathi

import android.app.Application
import com.example.gramavasathi.data.DataSeeder
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class GramaVasathiApp : Application() {

    @Inject
    lateinit var seeder: DataSeeder

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()
        GlobalScope.launch {
            try {
                seeder.seedIfEmpty()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
