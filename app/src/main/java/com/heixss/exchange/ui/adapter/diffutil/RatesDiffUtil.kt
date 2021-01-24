package com.heixss.exchange.ui.adapter.diffutil

import androidx.recyclerview.widget.DiffUtil
import com.heixss.exchange.model.local.Rate

class RatesDiffUtil constructor(
    private val oldList: List<Rate>,
    private val newList: List<Rate>
) : DiffUtil.Callback() {
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return (oldList[oldItemPosition].currency == newList[newItemPosition].currency)
    }

    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return (oldList[oldItemPosition].value == newList[newItemPosition].value)
    }
}