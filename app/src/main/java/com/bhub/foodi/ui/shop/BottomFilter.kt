package com.bhub.foodi.ui.shop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.bhub.foodi.databinding.BottomLayoutFilterBinding
import com.bhub.foodi.utilities.BUNDLE_KEY_MAX
import com.bhub.foodi.utilities.BUNDLE_KEY_MIN
import com.bhub.foodi.utilities.REQUEST_KEY
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.bhub.foodi.R
import java.text.NumberFormat
import java.util.*

class BottomFilter(
    private var min: Float = 0F,
    private var max: Float = 100F
) : BottomSheetDialogFragment() {
    private lateinit var binding: BottomLayoutFilterBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = BottomLayoutFilterBinding.inflate(inflater, container, false)

        bind()
        return binding.root
    }

    private fun bind() {
        binding.apply {
            rangeSlider.setLabelFormatter { value: Float ->
                val format = NumberFormat.getCurrencyInstance()
                format.maximumFractionDigits = 0
                format.currency = Currency.getInstance(getString(R.string.currency))
                format.format(value.toDouble())
            }
            rangeSlider.values = listOf(min, max)

            rangeSlider.valueFrom = min
            rangeSlider.valueTo = max
            rangeSlider.addOnChangeListener { rangeSlider, _, _ ->
                min = rangeSlider.values[0]
                max = rangeSlider.values[1]
            }

            btnReset.setOnClickListener {
                min = 0F
                max = 2500F
                sendData()
                dismiss()
            }

            btnConfirm.setOnClickListener {
                sendData()
                dismiss()
            }
        }
    }

    private fun sendData() {
        setFragmentResult(
            REQUEST_KEY, bundleOf(BUNDLE_KEY_MIN to min, BUNDLE_KEY_MAX to max)
        )
    }

    companion object {
        const val TAG = "BOTTOM_FILTER"
    }
}