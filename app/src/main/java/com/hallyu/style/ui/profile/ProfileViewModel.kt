package com.hallyu.style.ui.profile

import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hallyu.style.core.BaseViewModel
import com.hallyu.style.data.Card
import com.hallyu.style.data.OrderRepository
import com.hallyu.style.data.PaymentRepository
import com.hallyu.style.data.ReviewRepository
import com.hallyu.style.data.ShippingAddressRepository
import com.hallyu.style.data.UserManager
import com.hallyu.style.utilities.GlideDefault
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import de.hdodenhof.circleimageview.CircleImageView
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


    fun getAddress(){
        shippingAddressRepository.fetchAddress()
    }

    fun getReviews(){
        reviewRepository.getAllReviewOfUser()
    }

    fun getTotalOrder(){
        orderRepository.getSize()
    }
    fun getUserInfo(){
        viewModelScope.launch {
            orderRepository.getUserInfo()
            isLoading.postValue(false) // Update loading state after the operation
        }
        isLoading.postValue(true) // Set loading state before starting the operation
    }

    fun getPayment() {
        if (userManager.getPayment().isNotBlank()) {
            paymentRepository.getCard(userManager.getPayment())
        }
        else{
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

    fun deleteAccount(){
        isLoading.postValue(true)
        viewModelScope.launch {
            orderRepository.deleteAccount()
        }
    }

    fun getAvatar(): String {
        return userManager.getAvatar()
    }
}