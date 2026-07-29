package com.nexus.porsuk.di

import com.nexus.porsuk.core.network.ApiKeyProvider
import com.nexus.porsuk.core.network.ConfigProvider
import com.nexus.porsuk.core.network.ConfigProviderImpl
import com.nexus.porsuk.core.network.DynamicApiKeyInterceptor
import com.nexus.porsuk.core.network.ErrorHandler
import com.nexus.porsuk.core.network.RateLimitInterceptor
import com.nexus.porsuk.core.network.RetryInterceptor
import com.nexus.porsuk.core.network.createService
import com.nexus.porsuk.data.remote.PorsukApiKeyProvider
import com.nexus.porsuk.data.remote.api.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideConfigProvider(impl: ConfigProviderImpl): ConfigProvider = impl

    @Provides
    @Singleton
    fun provideApiKeyProvider(impl: PorsukApiKeyProvider): ApiKeyProvider = impl

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        rateLimitInterceptor: RateLimitInterceptor,
        apiKeyInterceptor: DynamicApiKeyInterceptor,
        retryInterceptor: RetryInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(retryInterceptor)
            .addInterceptor(rateLimitInterceptor)
            .addInterceptor(apiKeyInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.finnhub.io/api/v1/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideTefasApi(retrofit: Retrofit): TefasApi = retrofit.create(TefasApi::class.java)

    @Provides
    @Singleton
    fun provideFinnhubApi(retrofit: Retrofit): FinnhubApi = retrofit.create(FinnhubApi::class.java)

    @Provides
    @Singleton
    fun provideFmpApi(retrofit: Retrofit): FmpApi = retrofit.createService("https://financialmodelingprep.com/api/")

    @Provides
    @Singleton
    fun provideNewsApi(retrofit: Retrofit): NewsApi = retrofit.createService("https://newsapi.org/")

    @Provides
    @Singleton
    fun provideFredApi(retrofit: Retrofit): FredApi = retrofit.createService("https://api.stlouisfed.org/")

    @Provides
    @Singleton
    fun provideExchangeRateApi(retrofit: Retrofit): ExchangeRateApi = retrofit.createService("https://api.exchangerate.host/")
}
