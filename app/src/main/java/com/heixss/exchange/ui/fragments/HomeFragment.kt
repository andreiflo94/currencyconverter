package com.heixss.exchange.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.heixss.exchange.R
import com.heixss.exchange.model.local.Rate
import com.heixss.exchange.model.local.Resource
import com.heixss.exchange.ui.adapter.RatesAdapter
import com.heixss.exchange.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.home_screen_fragment.*

@AndroidEntryPoint
class HomeFragment : BaseFragment(R.layout.home_screen_fragment) {
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var ratesAdapter: RatesAdapter
    private var multiplierSubject = PublishSubject.create<Double>()
    private var selectedCurrencySubject = PublishSubject.create<Rate>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configUi()
        observeRatesLD()
    }

    override fun onResume() {
        super.onResume()
        observeSelectedCurrencySubject()
        observeMultiplierSubject()
        viewModel.loadRates()
    }

    override fun onPause() {
        viewModel.stopLoading()
        super.onPause()
    }

    private fun configUi() {
        ratesAdapter = RatesAdapter(arrayListOf(), selectedCurrencySubject, multiplierSubject)
        (rvRates.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
        rvRates.adapter = ratesAdapter

        ratesAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                if (fromPosition == toPosition)
                    return
                rvRates.scrollToPosition(0)
            }
        })
    }

    private fun observeRatesLD() {
        viewModel.ratesLD.observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Resource.Status.SUCCESS -> {
                    progressBar.visibility = View.GONE
                    it.data?.let { data ->
                        ratesAdapter.updateList(data)
                    } ?: run {
                        showErrorToast()
                    }
                }
                Resource.Status.ERROR -> {
                    progressBar.visibility = View.GONE
                    showErrorToast()
                }
                Resource.Status.LOADING -> {
                    progressBar.visibility = View.VISIBLE
                }
            }
        })
    }

    private fun observeMultiplierSubject() {
        disposables.add(multiplierSubject.subscribe {
            viewModel.baseCurrencyValue = it
        })
    }

    private fun observeSelectedCurrencySubject() {
        disposables.add(selectedCurrencySubject.subscribe {
            viewModel.baseCurrency = it.currency
            viewModel.loadRates()
        })
    }

    private fun showErrorToast() {
        context?.let { ctx ->
            Toast.makeText(
                ctx,
                getString(R.string.err_msj),
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
