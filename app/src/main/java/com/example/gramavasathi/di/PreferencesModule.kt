package com.example.gramavasathi.di

import com.example.gramavasathi.data.preferences.UserFlowPreferences
import com.example.gramavasathi.data.preferences.UserFlowPreferencesImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PreferencesModule {

    @Binds
    @Singleton
    abstract fun bindUserFlowPreferences(
        impl: UserFlowPreferencesImpl
    ): UserFlowPreferences
}
