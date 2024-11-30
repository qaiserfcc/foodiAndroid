package com.bhub.foodi.data

import androidx.lifecycle.MutableLiveData
import com.bhub.foodi.utilities.ADDRESS_USER
import io.paperdb.Paper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShippingAddressRepository @Inject constructor(
    private val userManager: UserManager
) {
    val listAddress = MutableLiveData<MutableList<ShippingAddress>>()
    val address = MutableLiveData<ShippingAddress>()
    fun fetchAddress() {
        if (userManager.isLogged()) {
            val list: MutableList<ShippingAddress> =
                Paper.book().read<MutableList<ShippingAddress>>(ADDRESS_USER) ?: mutableListOf()
            listAddress.postValue(list)
        }
    }

    fun getAddress(idAddress: String) {
        if (userManager.isLogged()) {
            val id = idAddress.toLongOrNull() ?: 0L
            val list: MutableList<ShippingAddress> =
                Paper.book().read<MutableList<ShippingAddress>>(ADDRESS_USER) ?: mutableListOf()
            list.forEach {
                if (it.id == id) {
                    address.postValue(it)
                }
            }

        }
    }

    fun setAddressOnFirebase(address: ShippingAddress) {
        val list: MutableList<ShippingAddress> =
            Paper.book().read<MutableList<ShippingAddress>>(ADDRESS_USER) ?: mutableListOf()
        list.add(address)
        Paper.book().write<MutableList<ShippingAddress>>(ADDRESS_USER, list)
    }

    fun deleteAddressOnFirebase(address: ShippingAddress) {
        Paper.book().read<MutableList<ShippingAddress>>(ADDRESS_USER)?.removeIf {
            it.id == address.id
        }
        fetchAddress()
    }
}