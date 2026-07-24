package com.nexus.porsuk.di

import com.nexus.porsuk.data.local.PorsukDatabase
import com.nexus.porsuk.data.local.dao.SecurityAuditDao
import com.nexus.porsuk.data.security.*
import com.nexus.porsuk.data.repository.*
import com.nexus.porsuk.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SecurityAuditDaoModule {

    @Provides
    fun provideSecurityAuditDao(db: PorsukDatabase): SecurityAuditDao {
        return db.securityAuditDao()
    }

    @Provides
    @Singleton
    fun provideKeystoreEncryptionManager(impl: KeystoreEncryptionManagerImpl): KeystoreEncryptionManager = impl
}

@Module
@InstallIn(SingletonComponent::class)
object SecurityRepositoriesModule {

    @Provides
    @Singleton
    fun provideSecurityRepository(impl: SecurityRepositoryImpl): SecurityRepository = impl

    @Provides
    @Singleton
    fun providePrivacyRepository(impl: PrivacyRepositoryImpl): PrivacyRepository = impl

    @Provides
    @Singleton
    fun provideAuthenticationRepository(impl: AuthenticationRepositoryImpl): AuthenticationRepository = impl

    @Provides
    @Singleton
    fun provideSessionRepository(impl: SessionRepositoryImpl): SessionRepository = impl

    @Provides
    @Singleton
    fun provideAuditRepository(impl: AuditRepositoryImpl): AuditRepository = impl
}
