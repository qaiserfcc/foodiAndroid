package com.hallyu.style.ui.bag

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.hallyu.style.R
import com.hallyu.style.adapters.ListColorAdapter
import com.hallyu.style.adapters.ListSizeAdapter
import com.hallyu.style.core.BaseBottomSheetDialog
import com.hallyu.style.data.Product
import com.hallyu.style.databinding.BottomLayoutSelectSizeBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BottomSheetCart(
    private val product: Product,
    private val selectSizeInt: Int,
    private val selectColorInt: Int
) : BaseBottomSheetDialog() {
    private val viewModel: BagViewModel by viewModels()
    private lateinit var binding: BottomLayoutSelectSizeBinding
    private lateinit var adapterSize: ListSizeAdapter
    private lateinit var adapterColor: ListColorAdapter
    private var selectSize: String? = null
    private var selectColor: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val listSize = product.getAllSize()
        val listColor = product.getAllColor()
        adapterSize = ListSizeAdapter {
            selectSize = it
        }
        adapterSize.submitList(listSize)
        adapterColor = ListColorAdapter {
            selectColor = if (selectColor == it) {
                null
            } else {
                it
            }
        }
        adapterColor.submitList(listColor)
        adapterSize.positionCurrent = selectSizeInt
        selectSize = listSize[selectSizeInt]

        adapterColor.positionCurrent = selectColorInt
        selectColor = listColor[selectColorInt]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomLayoutSelectSizeBinding.inflate(inflater, container, false)

        setupObserve()
        bind()
        return binding.root
    }

    private fun setupObserve() {
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

            btnAddToCart.text = getString(R.string.add_to_cart).uppercase()
            btnAddToCart.setOnClickListener {
                if (selectSize.isNullOrBlank()) {
                    viewModel.toastMessage.postValue(getString(R.string.warning_select_size))
                } else if (selectColor.isNullOrBlank()) {
                    viewModel.toastMessage.postValue(getString(R.string.warning_select_color))
                } else {
//                    viewModel.insertBag(
//                        product.id,
//                        selectColor.toString(),
//                        selectSize.toString(),
//                    ).observe(viewLifecycleOwner){
//                        setLoadingButton(progressBar,btnAddToCart,!it)
//                        if(it){
//                            viewModel.toastMessage.postValue(ADD_BAG_SUCCESS)
//                            dismiss()
//                        }
//                    }
                }
            }

        }
    }


    companion object {
        const val TAG = "BOTTOM_SHEET_SIZE"
        const val ADD_BAG_SUCCESS = "Add to Cart Success"
    }
}