package com.heixss.exchange.model.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RatesResponse(
    @Json(name = "baseCurrency")
    val baseCurrency: String = "",
    @Json(name = "rates")
    val rates: Map<String, Double>
)