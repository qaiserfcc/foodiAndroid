package com.bhub.foodi.ui.profile

import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import com.bhub.foodi.core.BaseViewModel
import com.bhub.foodi.data.Card
import com.bhub.foodi.data.OrderRepository
import com.bhub.foodi.data.PaymentRepository
import com.bhub.foodi.data.ReviewRepository
import com.bhub.foodi.data.ShippingAddressRepository
import com.bhub.foodi.data.UserManager
import com.bhub.foodi.utilities.GlideDefault
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val shippingAddressRepository: ShippingAddressRepository,
    private val paymentRepository: PaymentRepository,
    private val reviewRepository: ReviewRepository,
    val userManager: UserManager,
    private val firebaseAuth: FirebaseAuth,
    private val googleSignInClient: GoogleSignInClient
) : BaseViewModel() {
    val address = shippingAddressRepository.listAddress
    val payment = paymentRepository.card
    val totalOrder = orderRepository.totalOrder
    val userProfile = orderRepository.userProfile
    val onDelete = orderRepository.deleteAccount
    val reviews = reviewRepository.reviews
    val isLogged = MutableStateFlow(userManager.isLogged())

    fun setupProfileUI(
        fragment: Fragment,
        name: TextView,
        email: TextView,
        avatar: ImageView
    ) {
        if (userManager.isLogged()) {
            name.text = userManager.getName()
            email.text = userManager.getEmail()
            GlideDefault.userImage(
                fragment.requireContext(),
                userManager.getAvatar(),
                avatar
            )
        }
    }


    fun getAddress() {
        shippingAddressRepository.fetchAddress()
    }

    fun getReviews() {
        reviewRepository.getAllReviewOfUser()
    }

    fun getTotalOrder() {
        orderRepository.getSize()
    }

    fun getUserInfo() {
        viewModelScope.launch {
            orderRepository.getUserInfo()
            isLoading.postValue(false) // Update loading state after the operation
        }
        isLoading.postValue(true) // Set loading state before starting the operation
    }

    fun getPayment() {
        if (userManager.getPayment().isNotBlank()) {
            paymentRepository.getCard(userManager.getPayment())
        } else {
            paymentRepository.card.postValue(Card())
        }
    }

    fun logOut() {
        isLoading.postValue(true)
        firebaseAuth.signOut()
        userManager.logOut()
        //Google SignOut
        googleSignInClient.signOut()
        isLoading.postValue(false)
        viewModelScope.launch {
            isLogged.emit(userManager.isLogged())
        }
    }

    fun deleteAccount() {
        isLoading.postValue(true)
        viewModelScope.launch {
            orderRepository.deleteAccount()
        }
    }

    fun getAvatar(): String {
        return userManager.getAvatar()
    }
}