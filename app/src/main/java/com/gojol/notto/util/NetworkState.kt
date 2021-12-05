package com.gojol.notto.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import androidx.core.content.getSystemService
import androidx.lifecycle.LiveData

class NetworkState(private val context: Context) : LiveData<Boolean>() {

    private val connectivityManager: ConnectivityManager? = context.getSystemService()
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    override fun onActive() {
        super.onActive()
        connectivityManager ?: return
        updateConnection()
        initNetworkCallback()

        registerNetworkCallback()
    }

    override fun onInactive() {
        super.onInactive()
        unregisterNetworkCallback()
    }

    private fun initNetworkCallback() {
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                postValue(true)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                postValue(false)
            }
        }
    }

    private fun registerNetworkCallback() {
        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
        connectivityManager?.registerNetworkCallback(networkRequest, networkCallback)
    }

    private fun unregisterNetworkCallback() {
        connectivityManager?.unregisterNetworkCallback(networkCallback)
    }

    private fun updateConnection() {
        val isConnect = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            connectivityManager?.activeNetwork != null
        } else {
            connectivityManager?.activeNetworkInfo?.isConnected ?: false
        }
        postValue(isConnect)
    }
}
