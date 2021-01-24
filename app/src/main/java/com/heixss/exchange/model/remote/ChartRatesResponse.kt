package com.heixss.exchange.model.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ChartRatesResponse(
    @Json(name = "end_at")
    val endAt: String = "",
    @Json(name = "rates")
    val rates: Map<String, Map<String, String>>,
    @Json(name = "start_at")
    val startAt: String = "",
    @Json(name = "base")
    val base: String = ""
)