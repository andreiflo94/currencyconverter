package com.heixss.exchange.network

import com.heixss.exchange.model.remote.RatesResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface ExchangeApi {

    @GET("api/android/latest")
    fun getRates(
        @Query("base")
        baseCurrency: String
    ): Single<RatesResponse>
}