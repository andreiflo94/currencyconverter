package com.heixss.exchange

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.heixss.exchange.model.local.Rate
import com.heixss.exchange.model.local.Resource
import com.heixss.exchange.model.repositories.RatesRepository
import com.heixss.exchange.viewmodels.HomeViewModel
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
open class HomeViewModelTest {
    @Mock
    lateinit var ratesRepository: RatesRepository
    lateinit var homeViewModel: HomeViewModel

    @Mock
    private lateinit var observer: Observer<Resource<List<Rate>>>

    @Captor
    private lateinit var argumentCaptor: ArgumentCaptor<Resource<List<Rate>>>

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setup() {
        homeViewModel = HomeViewModel(ratesRepository)
        homeViewModel.ratesLD.observeForever(observer)
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
    }

    @Test
    fun testLoadRates() {
        val ratesList = ArrayList<Rate>()
        ratesList.add(Rate("EUR", "032"))
        `when`(ratesRepository.loadRates("EUR", 1.0)).thenReturn(
            Single.just(
                ratesList
            )
        )

        homeViewModel.loadRates()

        verify(observer, times(2))
            .onChanged(argumentCaptor.capture())

        val values = argumentCaptor.allValues
        Assert.assertEquals(Resource.Status.LOADING, values[0].status)
        Assert.assertEquals(Resource.Status.SUCCESS, values[1].status)
    }
}
