package com.hallyu.style.utilities

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.util.Log

internal object NetworkHelper {
    fun isNetworkAvailable(context: Context): Boolean {
        var result = false
        (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
            result = checkConnection(this, this.activeNetwork)
        }
        return result
    }

    private fun checkConnection(
        connectivityManager: ConnectivityManager,
        network: Network?
    ): Boolean {
        connectivityManager.getNetworkCapabilities(network)?.also {
            // Check if ANY transport is available
            if (it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                it.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            ) {
                Log.i("Internet", "Network available")
                return true
            }
        }
        return false
    }
}