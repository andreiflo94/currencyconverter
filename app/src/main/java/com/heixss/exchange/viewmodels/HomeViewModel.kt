package com.heixss.exchange.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.heixss.exchange.model.local.Rate
import com.heixss.exchange.model.local.Resource
import com.heixss.exchange.model.repositories.RatesRepository
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class HomeViewModel @ViewModelInject constructor(
    private val repository: RatesRepository
) : ViewModel() {

    companion object {
        private const val DEFAULT_BASE_CURRENCY = "EUR"
    }

    private var ratesDisposable: Disposable? = null
    var baseCurrencyValue = 1.0
    var baseCurrency: String

    private val ratesMLD = MutableLiveData<Resource<List<Rate>>>()
    var ratesLD: LiveData<Resource<List<Rate>>> = ratesMLD

    init {
        baseCurrency = DEFAULT_BASE_CURRENCY
    }

    /**
     * Method that starts loading the rates and then refreshes the rates
     * until stopLoading() is calledc
     */
    fun loadRates() {
        stopLoading()
        ratesDisposable = Observable.interval(
            1,
            TimeUnit.SECONDS
        )
            .startWith(0)
            .flatMap {
                return@flatMap repository.loadRates(baseCurrency, baseCurrencyValue)
                    .toObservable()
            }
            .doOnSubscribe { ratesMLD.value = Resource.loading() }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                ratesMLD.value = Resource.success(it)
            }, {
                ratesMLD.value = Resource.error(it)
            })
    }

    /**
     * Method that will dispose the ratesDisposable so that the refresh loading will work
     * until stopLoading is called
     */
    fun stopLoading() {
        ratesDisposable?.dispose()
    }
}
