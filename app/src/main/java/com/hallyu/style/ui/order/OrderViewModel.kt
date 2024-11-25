package com.hallyu.style.ui.order


import android.content.Context
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import com.hallyu.style.R
import com.hallyu.style.core.BaseViewModel
import com.hallyu.style.data.BagRepository
import com.hallyu.style.data.Order
import com.hallyu.style.data.OrderRepository
import com.hallyu.style.data.ProductOrder
import com.hallyu.style.utilities.CANCELLED
import com.hallyu.style.utilities.DELIVERED
import com.hallyu.style.utilities.PROCESSING
import com.hallyu.style.utilities.statuses
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