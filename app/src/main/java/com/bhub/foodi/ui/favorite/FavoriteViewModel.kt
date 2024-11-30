package com.bhub.foodi.ui.favorite

import android.content.Context
import android.view.View
import androidx.lifecycle.MutableLiveData
import com.bhub.foodi.core.BaseViewModel
import com.bhub.foodi.data.*
import com.bhub.foodi.networkservice.ApiService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val bagRepository: BagRepository,
    private val userManager: UserManager,
    val apiService: ApiService
) :
    BaseViewModel() {
    val favoriteAndProducts = favoriteRepository.favoriteAndProduct
    val isSuccess = favoriteRepository.isSuccess

    init {
//        bagRepository.getBags()
    }

    fun insertBag(idProduct: String, color: String, size: String) {
//        bagRepository.insertBag(idProduct, color, size)
    }


    fun removeFavorite(favorite: Product) {
        favoriteRepository.removeFavorite(favorite)
    }

    fun insertFavorite(product: Product): MutableLiveData<Boolean> {
        return favoriteRepository.insertFavorite(product)
    }


    fun setButtonBag(context: Context, buttonView: View, favorite: Product) {
        bagRepository.setButtonBag(context, buttonView, favorite)
    }

    fun isLogged(): Boolean {
        return userManager.isLogged()
    }
}
