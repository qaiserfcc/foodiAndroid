package com.hallyu.style.ui.promotion

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.hallyu.style.adapters.ListPromotionAdapter
import com.hallyu.style.databinding.BottomLayoutPromotionBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BottomPromotion() : BottomSheetDialogFragment() {
    private val viewModel: PromotionViewModel by viewModels()
    private lateinit var binding: BottomLayoutPromotionBinding
    private lateinit var adapter: ListPromotionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        viewModel.fetchData()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomLayoutPromotionBinding.inflate(inflater, container, false)

        adapter = ListPromotionAdapter {
//            viewModel.getPromotion(it.oldTotal)
            dismiss()
        }
        setupObserve()
        bind()
        return binding.root
    }

    private fun setupObserve() {
        viewModel.apply {
            promotions.observe(viewLifecycleOwner) {
                adapter.submitList(it)
            }

            promotion.observe(viewLifecycleOwner) {
                if (it.oldTotal.isNotBlank()) {
                    binding.txtWrongCode.visibility = View.GONE
                    binding.editPromoCode.setText(it.oldTotal)
                }
            }
        }
    }

    fun bind() {
        binding.apply {
            recyclerViewPromotion.layoutManager = LinearLayoutManager(context)
            recyclerViewPromotion.adapter = adapter

            editPromoCode.addTextChangedListener(object : TextWatcher {
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
                    if (!s.isNullOrBlank()) {
                        viewModel.promotions.value?.let {
                            val list = it.filter { promotion -> promotion.oldTotal == s.toString() }
                            println(list)
                            if (list.isEmpty()) {
                                binding.txtWrongCode.visibility = View.VISIBLE
                            } else {
                                if(viewModel.promotion.value?.oldTotal != s.toString()){
//                                    viewModel.getPromotion(s.toString())
                                }
                                binding.txtWrongCode.visibility = View.GONE
                            }
                        }
                    }
                }

            })
        }
    }

    companion object {
        const val TAG = "BOTTOM_SHEET_PROMOTION"
    }
}