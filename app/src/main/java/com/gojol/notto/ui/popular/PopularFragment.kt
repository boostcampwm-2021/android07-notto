package com.gojol.notto.ui.popular

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.gojol.notto.R
import com.gojol.notto.databinding.FragmentPopularBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class PopularFragment : Fragment() {

    private lateinit var binding: FragmentPopularBinding
    private lateinit var adapter: PopularAdapter

    private val popularViewModel: PopularViewModel by viewModels()
    private lateinit var networkCallBack: ConnectivityManager.NetworkCallback

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_popular, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner

        initToolbar()
        initAdapter()
        initObservers()
        checkNetwork()
        setNetWorkCallback()
    }

    private fun checkNetwork() {
        val manager = context?.let { getSystemService(it, ConnectivityManager::class.java) }
        val isConnect = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager?.activeNetwork != null
        } else {
            manager?.activeNetworkInfo?.isConnected ?: false
        }
        if (!isConnect){
            binding.progressCircular.isVisible = false
            binding.tvNetworkFail.isVisible = true
        }
    }

    override fun onResume() {
        super.onResume()
        registerNetworkCallback()
    }

    private fun initToolbar() {
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbarPopularKeyword)
    }

    private fun initAdapter() {
        adapter = PopularAdapter()
        binding.rvPopularKeyword.adapter = adapter
    }

    private fun initObservers() {
        popularViewModel.items.observe(viewLifecycleOwner, {
            adapter.submitList(it)
            binding.progressCircular.isVisible = false
        })
    }

    override fun onStop() {
        super.onStop()
        terminateNetworkCallback()
    }

    private fun setNetWorkCallback() {
        networkCallBack = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                CoroutineScope(Dispatchers.Main).launch {
                    if (popularViewModel.items.value.isNullOrEmpty()) {
                        binding.progressCircular.isVisible = true
                        withContext(Dispatchers.IO) {
                            popularViewModel.fetchKeywords()
                        }
                    }
                    binding.tvNetworkFail.isVisible = false
                }
            }

            override fun onLost(network: Network) {
                CoroutineScope(Dispatchers.Main).launch {
                    binding.progressCircular.isVisible = false
                    if (popularViewModel.items.value.isNullOrEmpty()) {
                        binding.tvNetworkFail.isVisible = true
                    }
                }
            }
        }
    }

    private fun registerNetworkCallback() {
        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
        context?.let { getSystemService(it, ConnectivityManager::class.java) }
            ?.registerNetworkCallback(networkRequest, networkCallBack)
    }


    private fun terminateNetworkCallback() {
        context?.let { getSystemService(it, ConnectivityManager::class.java) }
            ?.unregisterNetworkCallback(networkCallBack)
    }
}
