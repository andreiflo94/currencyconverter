package com.heixss.exchange.model.repositories

import com.heixss.exchange.model.local.Rate
import com.heixss.exchange.network.ExchangeApi
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

class RatesRepository @Inject constructor(
    private val api: ExchangeApi
) {

    /**
     * Method that fetches the response from the api, then the response is parsed into
     * a list of Rate objects.
     */
    fun loadRates(
        baseCurrency: String,
        multiplier: Double
    ): Single<List<Rate>> {
        return api.getRates(baseCurrency).subscribeOn(Schedulers.io())
            .map { response ->
                val rates = LinkedList<Rate>()

                response.rates.forEach { map ->
                    val calculatedRate = multiplier * map.value
                    val number2digits = String.format("%.2f", calculatedRate)

                    rates.add(
                        Rate(
                            map.key,
                            number2digits
                        )
                    )
                }

                rates.addFirst(
                    Rate(
                        baseCurrency,
                        multiplier.toString()
                    )
                )
                return@map rates
            }
    }
}