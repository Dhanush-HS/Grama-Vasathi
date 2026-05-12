package com.example.gramavasathi.di

import com.example.gramavasathi.data.repository.BookingRepositoryImpl
import com.example.gramavasathi.data.repository.FarmstayRepositoryImpl
import com.example.gramavasathi.data.repository.HostWizardRepositoryImpl
import com.example.gramavasathi.data.repository.ProfileRepositoryImpl
import com.example.gramavasathi.data.repository.AuthRepositoryImpl
import com.example.gramavasathi.domain.repository.AuthRepository
import com.example.gramavasathi.domain.repository.BookingRepository
import com.example.gramavasathi.domain.repository.FarmstayRepository
import com.example.gramavasathi.domain.repository.HostWizardRepository
import com.example.gramavasathi.domain.repository.ProfileRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindFarmstayRepository(
        farmstayRepositoryImpl: FarmstayRepositoryImpl
    ): FarmstayRepository

    @Binds
    @Singleton
    abstract fun bindHostWizardRepository(
        hostWizardRepositoryImpl: HostWizardRepositoryImpl
    ): HostWizardRepository

    @Binds
    @Singleton
    abstract fun bindBookingRepository(
        bookingRepositoryImpl: BookingRepositoryImpl
    ): BookingRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(
        profileRepositoryImpl: ProfileRepositoryImpl
    ): ProfileRepository
}
