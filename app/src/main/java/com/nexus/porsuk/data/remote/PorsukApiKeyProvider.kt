package com.nexus.porsuk.data.remote

import com.nexus.porsuk.core.network.ApiKeyProvider
import com.nexus.porsuk.core.network.ConfigProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PorsukApiKeyProvider @Inject constructor(
    private val configProvider: ConfigProvider
) : ApiKeyProvider {
    
    override fun getFinnhubKey(): String = configProvider.getFinnhubKey()
    override fun getFmpKey(): String = configProvider.getFmpKey()
    override fun getNewsApiKey(): String = configProvider.getNewsApiKey()
    override fun getFredKey(): String = configProvider.getFredKey()
    override fun getExchangeRateKey(): String = configProvider.getExchangeRateKey()
}
