package com.hallyu.style.ui.auth

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.JsonObject
import com.hallyu.style.core.BaseViewModel
import com.hallyu.style.core.OnSignInStartedListener
import com.hallyu.style.data.AuthResponse
import com.hallyu.style.data.User
import com.hallyu.style.data.UserData
import com.hallyu.style.data.UserManager
import com.hallyu.style.networkservice.ApiService
import com.hallyu.style.utilities.DateFormat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userManager: UserManager,
    private val googleSignInClient: GoogleSignInClient,
    private val firebaseAuth: FirebaseAuth,
    private val apiService: ApiService
) : BaseViewModel() {
    val onAuthFinishes: MutableLiveData<User?> = MutableLiveData()
    val validNameLiveData: MutableLiveData<String> = MutableLiveData()
    val errorLiveData: MutableLiveData<String> = MutableLiveData()
    val validEmailLiveData: MutableLiveData<String> = MutableLiveData()
    val validPasswordLiveData: MutableLiveData<String> = MutableLiveData()
    var remain = 3
    private val tokenDevice = MutableLiveData("")
//    val isBlock = MutableLiveData(false)

    init {
        if (!userManager.isLogged()) {
            isLoading.postValue(true)
            firebaseAuth.signOut()
            userManager.logOut()
            //Google SignOut
            googleSignInClient.signOut()
            isLoading.postValue(false)
        }
    }


    fun signUp(name: String, email: String, password: String) {
        if (!validName(name) || !validEmail(email) || !validPassword(password)) {
            return
        }
        registerAPI(name, email, password)
    }

    private fun registerAPI(
        name: String, email: String, password: String
    ) {
        viewModelScope.launch { // Use viewModelScope for coroutines
            try {
                val request = JsonObject().apply {
                    addProperty("name", name)
                    addProperty("email", email)
                    addProperty("password", password)
                }
                Log.d("TAG", "signUpAPI: Query request: $request")
                isLoading.postValue(true)

                val response = apiService.registerUser(request)
                if (response.isSuccessful) {
                    val authResponse: AuthResponse? = response.body()
                    authResponse?.let {
                        val token = authResponse.token
                        val user: User = getUserWithToken(authResponse.user, token)
                        userManager.addAccount(user)
                        onAuthFinishes.postValue(user)
                        toastMessage.postValue(REGISTRATION_SUCCESS)
                    }
                } else {
                    Log.e("TAG", "signUpAPI: Failed: response:body ${response.body()}")
                    Log.e("TAG", "signUpAPI: Failed: response:message:  ${response.message()}")
                    Log.e("TAG", "signUpAPI: code: ${response.code()}")
                    errorLiveData.postValue(response.code().toString())

                }
            } catch (e: Exception) {
                Log.e("TAG", "signUpAPI: onFailure: ${e.localizedMessage}")
                errorLiveData.postValue("0")
                isLoading.postValue(false)
            }
        }

    }


    private fun loginAPI(
        email: String, password: String
    ) {
        viewModelScope.launch { // Use viewModelScope for coroutines
            try {
                val request = JsonObject().apply {
                    addProperty("email", email)
                    addProperty("password", password)
                }
                Log.d("TAG", "loginAPI: Query request: $request")
                val response = apiService.login(request)
                if (response.isSuccessful) {
                    Log.d("TAG", "loginAPI request: isSuccessful")
                    val authResponse: AuthResponse? = response.body()
                    authResponse?.let {
                        val token = authResponse.token
                        val user: User = getUserWithToken(authResponse.user, token)
                        userManager.addAccount(user)
                        onAuthFinishes.postValue(user)
                        toastMessage.postValue(LOGIN_SUCCESS)
                    }
                    Log.d("TAG", "loginAPI: authResponse: $authResponse")
                } else {
                    Log.e("TAG", "LoginAPI: response: ${response.body()}")
                    Log.e("TAG", "LoginAPI: onFailure: ${response.errorBody()}")
                    Log.e("TAG", "LoginAPI: code: ${response.code()}")
                    errorLiveData.postValue(response.code().toString())

                }
            } catch (e: Exception) {
                Log.e("TAG", "onFailure: ${e.localizedMessage}")
                isLoading.postValue(false)
            } finally {
                isLoading.postValue(false)

            }
        }

    }

    private fun googleAuthAPI(
        token: String
    ) {
        viewModelScope.launch { // Use viewModelScope for coroutines
            try {
                val request = JsonObject().apply {
                    addProperty("token", token)
                }
                Log.d("TAG", "googleAuthAPI: Query request: $request")
                val response = apiService.authCallBack(request)
                if (response.isSuccessful) {
                    Log.d("TAG", "googleAuthAPI request: isSuccessful")
                    val authResponse: AuthResponse? = response.body()
                    authResponse?.let {
                        val token = authResponse.token
                        val user: User = getUserWithToken(authResponse.user, token)
                        userManager.addAccount(user)
                        onAuthFinishes.postValue(user)
                        toastMessage.postValue(LOGIN_SUCCESS)
                    }
                    Log.d("TAG", "googleAuthAPI: authResponse: $authResponse")
                } else {
                    Log.e("TAG", "LoggoogleAuthAPIinAPI: response: ${response.body()}")
                    Log.e("TAG", "googleAuthAPI: onFailure: ${response.errorBody()}")
                    Log.e("TAG", "googleAuthAPI: code: ${response.code()}")
                    errorLiveData.postValue(response.code().toString())
                }
            } catch (e: Exception) {
                Log.e("TAG", "onFailure: ${e.localizedMessage}")
                isLoading.postValue(false)
            } finally {
                isLoading.postValue(false)

            }
        }

    }

    fun logIn(email: String, password: String) {
        if (!validEmail(email, validEmailLiveData) || !validPasswordLogin(password)) {
            return
        }
        isLoading.postValue(true)
        loginAPI(email, password)
    }

    private fun getUserWithToken(user: UserData, token: String): User {
        val dob = Date()
        return User(
            user.name ?: "",
            user.email.toString(),
            "",
            token,
            DateFormat.dob.format(dob),
            user.photo ?: "",
            id = "${user.id}",
            phone= user.phone ?:""
        )

    }


//    private fun getTokenFirebase(
//        user: FirebaseUser
//    ) {
//        user.getIdToken(false).addOnCompleteListener {
//            if (it.isSuccessful) {
//                val token = it.result.token ?: return@addOnCompleteListener
////                createNewUserManagerForLoginSocial(user, token)
//            }
//        }
//    }

    fun initLogin() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            tokenDevice.postValue(it)
        }
    }


    fun forgotPassword(emailText: String) {
        if (!validEmail(emailText)) {
            return
        }
        if (emailText.isBlank()) {
            toastMessage.postValue("Please enter email")
        } else {
            isLoading.postValue(true)
            firebaseAuth.sendPasswordResetEmail(emailText).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    toastMessage.postValue("Email sent.")
                } else {
                    toastMessage.postValue("Email invalid")
                }
                isLoading.postValue(false)
            }
        }
    }


    //If function hasn't password parameter mean login with social


//    private fun createNewUserManagerForLoginSocial(user: FirebaseUser, token: String) {
//        val dob = Date()
//        val account = User(
//            user.displayName.toString(),
//            user.email.toString(),
//            "",
//            token,
//            DateFormat.dob.format(dob),
//            user.photoUrl.toString()
//        )
//        userManager.addAccount(account)
//        userManager.writeProfile(db, account)
//    }

    fun isLogged(): Boolean {
        return userManager.isLogged()
    }


    fun signInWithGoogle(listener: OnSignInStartedListener) {
        listener.onSignInStarted(googleSignInClient)
    }

    // GOOGLE
    fun firebaseAuthWithGoogle(idToken: String) {
        isLoading.postValue(true)
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                val user = firebaseAuth.currentUser
                user?.let {
                    user.getIdToken(false).addOnCompleteListener {
                        if (it.isSuccessful) {
                            val token = it.result.token?:""
                            googleAuthAPI(token)
                        }
                    }
                }
            } else {
                checkFail(it.exception?.message.toString())
                isLoading.postValue(false)
            }
        }
    }


    private fun checkFail(string: String) {
        Log.d(TAG, "checkFail: reason: $string")

        if (string.contains(KEY_NO_USER)) {
            toastMessage.postValue(WARNING_NO_USER)
        } else if (string.contains(KEY_INVALID_USER)) {
            toastMessage.postValue(WARNING_INVALID_USER)
        } else if (string.contains(KEY_ALREADY_EMAIL)) {
            toastMessage.postValue(WARNING_ALREADY_EMAIL)
        } else {
            toastMessage.postValue("Login Failure: $string")
        }
    }

    fun validName(nameText: String): Boolean {
        return validName(nameText, validNameLiveData)
    }

    fun validEmail(emailText: String): Boolean {
        return validEmail(emailText, validEmailLiveData)
    }

    fun validPassword(passwordText: String?): Boolean {
        return validPassword(passwordText, validPasswordLiveData)
    }

    private fun validPasswordLogin(passwordText: String): Boolean {
        return if (passwordText.isEmpty()) {
            validPasswordLiveData.postValue("Mustn't empty")
            false
        } else {
            validPasswordLiveData.postValue("")
            true
        }
    }

    /*   fun checkEmailExistsOrNot(email: String, password: String, user: User) {

           db.collection(USER_FIREBASE)
               .document(user.id)
               .get().addOnCompleteListener { root ->
                   if (root.isSuccessful) {
                       val document = root.result
                           if (document != null && document.exists()) {
                               Log.d("TAG", "User already exists. log it for token: ${user.token}")
                               firebaseAuth.signInWithEmailAndPassword(email, password)
                                   .addOnCompleteListener { task ->

                                       if (task.isSuccessful) {
                                           val firebaseUser = firebaseAuth.currentUser
                                           userManager.addAccount(user)
                                           userManager.writeProfile(db, user)
                                           userLiveData.postValue(firebaseUser)
                                           toastMessage.postValue(LOGIN_SUCCESS)
                                       } else {
                                           checkFail(task.exception?.message.toString())
                                       }
                                       isLoading.postValue(false)
                                   }
                           } else {
                               Log.d("TAG", "User doesn't exist. create it for token: ${user.token}")
                               firebaseAuth.createUserWithEmailAndPassword(email, password)
                                   .addOnCompleteListener { task ->
                                       if (task.isSuccessful) {
                                           val firebaseUser = firebaseAuth.currentUser
                                           userManager.addAccount(user)
                                           userManager.writeProfile(db, user)
                                           userLiveData.postValue(firebaseUser)
                                           Log.d(TAG, "AuthViewModel: userLiveData from : checkEmailExistsOrNot ")
                                           toastMessage.postValue(REGISTRATION_SUCCESS)
                                       } else {
                                           checkFail(task.exception?.message.toString())
                                       }
                                       isLoading.postValue(false)
                                   }
                           }
                   } else {
                       Log.d("TAG", "Error: ", root.exception)
                   }
               }


       }*/
    companion object {
        const val KEY_NO_USER = "user may have been deleted"
        const val WARNING_NO_USER = "Email does not exist"
        const val KEY_INVALID_USER = "password is invalid"
        const val WARNING_INVALID_USER = "Wrong password"
        const val KEY_BLOCK = "due to many failed login attempts"
        const val WARNING_BLOCK = "The device was blocked login about"
        const val KEY_ALREADY_EMAIL = "The email address is already"
        const val WARNING_ALREADY_EMAIL = "The email address is already"
        const val LOGIN_SUCCESS = "Login Success"
        const val REGISTRATION_SUCCESS = "Registration Success"
        const val EMAIL = "email"
        const val PUBLIC_PROFILE = "public_profile"
        const val USER_FRIEND = "user_friends"
        const val TAG = "Authentication"
        const val TIME_BLOCK = 30000
        const val COUNT_DOWN = 1000
        const val TIME = "time"
    }
}