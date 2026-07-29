package com.nexus.porsuk.core.network

import retrofit2.Retrofit

inline fun <reified T> Retrofit.createService(baseUrl: String): T {
    return this.newBuilder()
        .baseUrl(baseUrl)
        .build()
        .create(T::class.java)
}
