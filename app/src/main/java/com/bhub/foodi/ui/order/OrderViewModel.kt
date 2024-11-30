package com.bhub.foodi.ui.order


import android.content.Context
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.bhub.foodi.R
import com.bhub.foodi.core.BaseViewModel
import com.bhub.foodi.data.BagRepository
import com.bhub.foodi.data.Order
import com.bhub.foodi.data.OrderRepository
import com.bhub.foodi.data.ProductOrder
import com.bhub.foodi.utilities.CANCELLED
import com.bhub.foodi.utilities.DELIVERED
import com.bhub.foodi.utilities.PROCESSING
import com.bhub.foodi.utilities.statuses
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val bagRepository: BagRepository,
    private val orderRepository: OrderRepository,
) : BaseViewModel() {
    private val idOrder = MutableStateFlow("")
    val order = idOrder.flatMapLatest {
        orderRepository.getOrder(it)
    }.asLiveData()


    fun setIdOrder(id: String) {
        idOrder.value = id
    }

    fun getOrderStatus(status: Int): MutableLiveData<List<Order>> {
        return orderRepository.getOrderStatus(status)
    }

    fun setUIStatus(context: Context, textView: TextView, status: Int) {
        when (status) {
            DELIVERED -> {
                textView.text = statuses[0]
                textView.setTextColor(ContextCompat.getColor(context, R.color.green))
            }

            PROCESSING -> {
                textView.text = statuses[1]
                textView.setTextColor(ContextCompat.getColor(context, R.color.yellow))
            }

            CANCELLED -> {
                textView.text = statuses[2]
                textView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
            }
        }
    }

    fun reOrder(list: List<ProductOrder>) {
        for (productOrder in list) {
//            productOrder.apply {
//                bagRepository.insertBag(
//                    size = size,
//                    color = color,
//                    idProduct = idProduct,
//                    quantity = units.toLong()
//                )
//            }
        }
        dismiss.postValue(true)
    }
}