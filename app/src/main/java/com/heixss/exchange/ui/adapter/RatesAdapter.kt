package com.heixss.exchange.ui.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.heixss.exchange.R
import com.heixss.exchange.model.local.Rate
import com.heixss.exchange.ui.adapter.diffutil.RatesDiffUtil
import io.reactivex.subjects.PublishSubject
import java.text.NumberFormat

class RatesAdapter(
    private val rates: ArrayList<Rate>,
    private val rowSelectSubject: PublishSubject<Rate>,
    private val multiplierChangeSubject: PublishSubject<Double>
) : RecyclerView.Adapter<RateViewHolder>() {

    private val nf = NumberFormat.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RateViewHolder {
        return RateViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.rate_layout, parent, false),
            rowSelectSubject, multiplierChangeSubject, nf
        )
    }

    override fun getItemCount(): Int {
        return rates.size
    }

    override fun onBindViewHolder(holder: RateViewHolder, position: Int) {
        holder.setup(rates[position])
    }

    fun updateList(newRates: List<Rate>) {
        val diffResult = DiffUtil.calculateDiff(RatesDiffUtil(this.rates, newRates))
        this.rates.clear()
        this.rates.addAll(newRates)
        diffResult.dispatchUpdatesTo(this)
    }
}

class RateViewHolder(
    view: View,
    private val rowSelectSubject: PublishSubject<Rate>,
    private val multiplierChangeSubject: PublishSubject<Double>,
    private val numberFormat: NumberFormat
) :
    RecyclerView.ViewHolder(view) {

    private lateinit var rate: Rate
    private val tvCurrency = view.findViewById<TextView>(R.id.tvCurrency)
    private val edValue = view.findViewById<TextView>(R.id.edValue)
    private val ivCurrency = view.findViewById<ImageView>(R.id.ivCurrency)

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(
            s: CharSequence?,
            start: Int,
            count: Int,
            after: Int
        ) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

        override fun afterTextChanged(s: Editable?) {
            if (edValue.hasFocus()) {
                if (s.toString().isEmpty()) {
                    multiplierChangeSubject.onNext(1.0)
                    return
                }
                try {
                    numberFormat.parse(s.toString())?.let {
                        multiplierChangeSubject.onNext(it.toDouble())
                    }
                } catch (e: Exception) {
                }
            }

        }
    }

    private val focusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
        if (hasFocus) rowSelectSubject.onNext(rate)
    }

    init {
        view.setOnClickListener {
            rowSelectSubject.onNext(rate)
        }
        edValue.onFocusChangeListener = focusChangeListener
        edValue.addTextChangedListener(textWatcher)
    }

    fun setup(rate: Rate) {
        this.rate = rate
        tvCurrency.text = rate.currency

        if (!edValue.hasFocus()) {
            edValue.text = rate.value
        }
        val imageResource: Int = when (rate.currency) {
            "EUR" -> R.drawable.eur
            "RON" -> R.drawable.ron
            "DKK" -> R.drawable.dkk
            "USD" -> R.drawable.usd
            "GBP" -> R.drawable.gbp
            else -> R.drawable.ic_launcher_foreground
        }
        ivCurrency.setImageDrawable(ContextCompat.getDrawable(itemView.context, imageResource))
    }
}