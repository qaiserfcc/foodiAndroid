package com.bhub.foodi.core

import android.os.Environment
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import java.io.File
import java.io.FileWriter
import java.io.IOException

abstract class BaseViewModel : ViewModel() {
    val toastMessage = MutableLiveData<String>()
    val isLoading = MutableLiveData(false)
    val dismiss = MutableLiveData(false)

    fun validName(nameText: String, validNameLiveData: MutableLiveData<String>): Boolean {
        return when {
            nameText.isBlank() -> {
                validNameLiveData.postValue("Mustn't empty")
                false
            }

            else -> {
                validNameLiveData.postValue("")
                true
            }
        }
    }

    fun validEmail(emailText: String, validNameLiveData: MutableLiveData<String>): Boolean {
        return if (emailText.isBlank()) {
            validNameLiveData.postValue("Mustn't empty")
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            validNameLiveData.postValue("Invalid Email Address")
            false
        } else {
            validNameLiveData.postValue("")
            true
        }
    }

    fun validPassword(passwordText: String?, validNameLiveData: MutableLiveData<String>): Boolean {
        return when {
            passwordText == null -> {
                validNameLiveData.postValue("Mustn't empty")
                false
            }

            passwordText.length < 6 -> {
                validNameLiveData.postValue("Minimum 6 Character Password")
                false
            }
//            !passwordText.matches(".*[A-Z].*".toRegex()) -> {
//                validNameLiveData.postValue("Must Contain 1 Upper-case Character")
//                false
//            }
//            !passwordText.matches(".*[a-z].*".toRegex()) -> {
//                validNameLiveData.postValue("Must Contain 1 Lower-case Character")
//                false
//            }
            else -> {
                validNameLiveData.postValue("")
                true
            }
        }
    }

    fun saveDataToFile(list: Any, name: String) {
        val outputFile = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
            name
        )

        Log.i("saveDataToFile", "saveDataToFile: is calling")
        val gson = Gson()
        val json = gson.toJson(list)
        try {
            FileWriter(outputFile).use { writer ->
                writer.write(json)
            }
            Log.i("saveDataToFile", "Data saved to ${outputFile.absolutePath}")
        } catch (e: IOException) {
            Log.e("saveDataToFile", "Error writing categories to file", e)
        }

    }

}