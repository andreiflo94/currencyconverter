package com.heixss.exchange.ui.fragments

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import io.reactivex.disposables.CompositeDisposable

abstract class BaseFragment : Fragment {

    protected val disposables = CompositeDisposable()

    constructor() : super()
    constructor(@LayoutRes layoutResId: Int) : super(layoutResId)

    override fun onPause() {
        disposables.clear()
        super.onPause()
    }
}