package com.hallyu.style.data

import android.accounts.Account
import android.accounts.AccountManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.hallyu.style.utilities.USER_FIREBASE
import com.google.firebase.firestore.FirebaseFirestore

class UserManager(context: Context) {
    private val accountManager: AccountManager = AccountManager.get(context)
    fun addAccount(
        user: User
    ) {
        Log.d(TAG, "addAccount:for token ${user.token} ")
        val data = Bundle()
            .apply {
                this.putString(NAME, user.name)
                this.putString(EMAIL, user.email)
                this.putString(PASSWORD, user.password)
                this.putString(DOB, user.dob)
                this.putString(TOKEN, user.token)
                this.putString(AVATAR, user.avatar)
                this.putString(ADDRESS, user.defaultAddress)
                this.putString(PAYMENT, user.defaultPayment)
                this.putString(ROLE,user.id)
                this.putString(PHONE,user.phone)
            }
        val account = Account(user.email, ACCOUNT_TYPE)
        accountManager.addAccountExplicitly(account, user.token, data)
        accountManager.setAuthToken(account, AUTH_TOKEN_TYPE, user.token)
    }

    private fun getAccount(): Account? {
        return if (accountManager.getAccountsByType(ACCOUNT_TYPE).isNotEmpty()) {
            accountManager.getAccountsByType(ACCOUNT_TYPE)[0]
        } else {
            null
        }
    }

    fun getUser(): User {
        return User(
            getName(),
            getEmail(),
            getPassword(),
            getAccessToken(),
            getDOB(),
            getAvatar(),
            getAddress(),
            getPayment(),
            getRole(),
            getPhone()
        )
    }

    fun isLogged(): Boolean {
        val accounts = accountManager.getAccountsByType(ACCOUNT_TYPE)
        if (accounts.isNotEmpty()) {
            return true
        }
        return false
    }

    fun logOut() {
        if (getAccount() != null) {
            accountManager.removeAccountExplicitly(getAccount())
        }
    }

    fun writeProfile(db: FirebaseFirestore, user: User): MutableLiveData<Int> {
        val isSuccess = MutableLiveData(0)
        db.collection(USER_FIREBASE)
            .document(user.token)
            .set(user)
            .addOnSuccessListener {
                isSuccess.postValue(1)
            }
            .addOnFailureListener {
                isSuccess.postValue(-1)
            }
        return isSuccess
    }


    fun getAccessToken(): String {
        return accountManager.getUserData(getAccount(), TOKEN) ?: ""
    }

    fun getEmail(): String {
        return accountManager.getUserData(getAccount(), EMAIL)
    }

    fun getName(): String {
        return accountManager.getUserData(getAccount(), NAME)
    }

    fun getPassword(): String {
        return accountManager.getUserData(getAccount(), PASSWORD)
    }

    fun getDOB(): String {
        return accountManager.getUserData(getAccount(), DOB)
    }

    fun getAvatar(): String {
        return accountManager.getUserData(getAccount(), AVATAR) ?: ""
    }

    fun setName(name: String) {
        accountManager.setUserData(getAccount(), NAME, name)
    }

    fun setPassword(password: String) {
        accountManager.setUserData(getAccount(), PASSWORD, password)
    }

    fun setDOB(dob: String) {
        accountManager.setUserData(getAccount(), DOB, dob)
    }

    fun setAvatar(avatar: String) {
        accountManager.setUserData(getAccount(), AVATAR, avatar)
    }


    fun getAddress(): String {
        return accountManager.getUserData(getAccount(), ADDRESS) ?: ""
    }

    fun setAddress(idAddress: String) {
        accountManager.setUserData(getAccount(), ADDRESS, idAddress)
    }

    fun getPayment(): String {
        return accountManager.getUserData(getAccount(), PAYMENT) ?: ""
    }

    fun setPayment(idPayment: String) {
        accountManager.setUserData(getAccount(), PAYMENT, idPayment)
    }
    fun setPhone(verifiedPhone: String) {
        accountManager.setUserData(getAccount(), PHONE, verifiedPhone)
    }


    fun getPhone(): String {
        return accountManager.getUserData(getAccount(), PHONE) ?: ""
    }

    fun getRole(): String {
        return accountManager.getUserData(getAccount(), ROLE) ?: "0"
    }

    companion object {
        const val AUTH_TOKEN_TYPE = "com.hallyu.style"
        const val ACCOUNT_TYPE = "com.hallyu.style"
        const val TOKEN = "access_token"
        const val EMAIL = "email"
        const val PASSWORD = "password"
        const val NAME = "name"
        const val DOB = "Date_of_birth"
        const val AVATAR = "avatar"
        const val TAG = "USER_MANAGER"
        const val ADDRESS = "address"
        const val PAYMENT = "payment"
        const val ROLE = "role"
        const val PHONE = "phone"

        @Volatile
        private var instance: UserManager? = null

        fun getInstance(context: Context): UserManager {
            return instance ?: synchronized(this) {
                instance ?: UserManager(context)
            }
        }
    }
}