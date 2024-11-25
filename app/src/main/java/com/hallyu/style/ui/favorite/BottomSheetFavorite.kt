package com.hallyu.style.ui.favorite

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.hallyu.style.R
import com.hallyu.style.adapters.ListColorAdapter
import com.hallyu.style.adapters.ListSizeAdapter
import com.hallyu.style.data.Product
import com.hallyu.style.databinding.BottomLayoutSelectSizeBinding
import com.hallyu.style.core.BaseBottomSheetDialog
import com.hallyu.style.utilities.BUNDLE_KEY_IS_FAVORITE
import com.hallyu.style.utilities.REQUEST_KEY
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BottomSheetFavorite(
    private val product: Product,
    private val selectSizeInt: Int? = null,
    private var color: String? = null,
) : BaseBottomSheetDialog() {
    private val viewModel: FavoriteViewModel by viewModels()
    private var selectSize: String? = null

    private lateinit var binding: BottomLayoutSelectSizeBinding
    private lateinit var adapterSize: ListSizeAdapter
    private lateinit var adapterColor: ListColorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val listSize = product.getAllSize()
        val listColor = product.getAllColor()
        adapterSize = ListSizeAdapter {
            selectSize = it
        }
        adapterSize.submitList(listSize)
        adapterColor = ListColorAdapter {
            color = if (color == it) {
                null
            } else {
                it
            }
        }
        adapterColor.submitList(listColor)
        if (selectSizeInt != null) {
            adapterSize.positionCurrent = selectSizeInt
            selectSize = listSize[selectSizeInt]
        }
        if (!color.isNullOrBlank()) {
            adapterColor.positionCurrent = listColor.indexOf(color)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomLayoutSelectSizeBinding.inflate(inflater, container, false)

        observeSetup()
        bind()
        return binding.root
    }

    private fun observeSetup() {
        viewModel.apply {
            toastMessage.observe(viewLifecycleOwner) { str ->
                toastMessage(str)
                
            }
        }
    }

    fun bind() {
        binding.apply {
            recyclerViewSize.layoutManager = GridLayoutManager(context, GRIDVIEW_SPAN_COUNT)
            recyclerViewSize.adapter = adapterSize

            recyclerViewColor.layoutManager = GridLayoutManager(context, GRIDVIEW_SPAN_COUNT)
            recyclerViewColor.adapter = adapterColor

            btnAddToCart.setOnClickListener {
                if (selectSize.isNullOrBlank()) {
                    viewModel.toastMessage.postValue(getString(R.string.warning_select_size))
                } else if (color.isNullOrBlank()) {
                    viewModel.toastMessage.postValue(getString(R.string.warning_select_color))
                } else {
                    viewModel.insertFavorite(product)
                        .observe(viewLifecycleOwner) {
                            dismiss()
                            sendData()
                        }
                }
            }
        }
    }

    private fun sendData() {
        setFragmentResult(
            REQUEST_KEY,
            bundleOf(BUNDLE_KEY_IS_FAVORITE to true)
        )
    }

    companion object {
        const val GRIDVIEW_SPAN_COUNT = 3
        const val TAG = "BOTTOM_SHEET_SIZE"
    }
}