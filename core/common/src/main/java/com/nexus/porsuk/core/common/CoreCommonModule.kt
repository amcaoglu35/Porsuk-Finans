package com.nexus.porsuk.core.common

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object CoreCommonModule {
    // Empty module. Infrastructure classes use @Inject constructor() and @Singleton directly.
    // This module ensures the :core:common classes are part of the Hilt component tree if needed.
}
