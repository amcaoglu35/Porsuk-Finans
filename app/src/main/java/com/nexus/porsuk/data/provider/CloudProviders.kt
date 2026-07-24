package com.nexus.porsuk.data.provider

import com.nexus.porsuk.domain.model.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Porsuk Cloud Sync Platform — Soyut Provider Arayüzü (CloudSyncProvider)
 *
 * Provider Pattern + Strategy Pattern: Firebase, Supabase, AWS, Azure ve Self-Hosted sağlayıcıları bu arayüzü uygular.
 */
interface CloudSyncProvider {
    fun getProviderType(): CloudProviderType
    fun uploadDelta(module: SyncModuleType, payloadJson: String): Boolean
    fun downloadDelta(module: SyncModuleType): String
}

/**
 * 1. Firebase Cloud Sync Sağlayıcısı
 */
@Singleton
class FirebaseCloudSyncProvider @Inject constructor() : CloudSyncProvider {
    override fun getProviderType() = CloudProviderType.FIREBASE
    override fun uploadDelta(module: SyncModuleType, payloadJson: String) = true
    override fun downloadDelta(module: SyncModuleType) = "{\"status\": \"synced\", \"provider\": \"firebase\", \"module\": \"${module.name}\"}"
}

/**
 * 2. Supabase Cloud Sync Sağlayıcısı
 */
@Singleton
class SupabaseCloudSyncProvider @Inject constructor() : CloudSyncProvider {
    override fun getProviderType() = CloudProviderType.SUPABASE
    override fun uploadDelta(module: SyncModuleType, payloadJson: String) = true
    override fun downloadDelta(module: SyncModuleType) = "{\"status\": \"synced\", \"provider\": \"supabase\", \"module\": \"${module.name}\"}"
}

/**
 * 3. AWS Cloud Sync Sağlayıcısı
 */
@Singleton
class AwsCloudSyncProvider @Inject constructor() : CloudSyncProvider {
    override fun getProviderType() = CloudProviderType.AWS
    override fun uploadDelta(module: SyncModuleType, payloadJson: String) = true
    override fun downloadDelta(module: SyncModuleType) = "{\"status\": \"synced\", \"provider\": \"aws\", \"module\": \"${module.name}\"}"
}

/**
 * 4. Azure Cloud Sync Sağlayıcısı
 */
@Singleton
class AzureCloudSyncProvider @Inject constructor() : CloudSyncProvider {
    override fun getProviderType() = CloudProviderType.AZURE
    override fun uploadDelta(module: SyncModuleType, payloadJson: String) = true
    override fun downloadDelta(module: SyncModuleType) = "{\"status\": \"synced\", \"provider\": \"azure\", \"module\": \"${module.name}\"}"
}

/**
 * 5. Self-Hosted Server Cloud Sync Sağlayıcısı
 */
@Singleton
class SelfHostedCloudSyncProvider @Inject constructor() : CloudSyncProvider {
    override fun getProviderType() = CloudProviderType.SELF_HOSTED
    override fun uploadDelta(module: SyncModuleType, payloadJson: String) = true
    override fun downloadDelta(module: SyncModuleType) = "{\"status\": \"synced\", \"provider\": \"self_hosted\", \"module\": \"${module.name}\"}"
}
