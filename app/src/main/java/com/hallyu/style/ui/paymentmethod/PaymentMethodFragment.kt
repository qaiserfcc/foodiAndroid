package com.hallyu.style.ui.paymentmethod

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.hallyu.style.R
import com.hallyu.style.adapters.ListCardAdapter
import com.hallyu.style.core.BaseFragment
import com.hallyu.style.databinding.FragmentPaymentMethodBinding
import com.hallyu.style.ui.general.ConfirmDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentMethodFragment : BaseFragment<FragmentPaymentMethodBinding>(
    FragmentPaymentMethodBinding::inflate
) {
    private lateinit var adapter: ListCardAdapter
    override val viewModel: PaymentViewModel by viewModels()

    override fun setUpAdapter() {
        adapter = ListCardAdapter({ checkBox, card ->
            if (checkBox.isChecked) {
                viewModel.setDefaultPayment(card.id)
                adapter.notifyDataSetChanged()
            } else {
                viewModel.removeDefaultPayment()
                adapter.notifyDataSetChanged()
            }
        }, { checkBox, card ->
            checkBox.isChecked = viewModel.checkDefaultCard(card.id)
        }, {
            ConfirmDialog(this) {
                viewModel.deleteCard(it)
            }.show()
        })
    }

    override fun setUpObserve() {
        viewModel.fetchData()
        viewModel.apply {
            cards.observe(viewLifecycleOwner) {
                adapter.dataSet = it
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun setUpViews() {
        binding.apply {
            appBarLayout.MaterialToolbar.title = getString(R.string.payment_methods)
            appBarLayout.MaterialToolbar.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
            recyclerViewPaymentCard.adapter = adapter
            recyclerViewPaymentCard.layoutManager = LinearLayoutManager(context)

            btnAddShippingAddress.setOnClickListener {
                val bottomAddPayment = BottomAddPayment()
                bottomAddPayment.show(parentFragmentManager, BottomAddPayment.TAG)
            }
        }
    }
}