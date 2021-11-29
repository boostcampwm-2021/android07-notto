package com.gojol.notto.ui.popular

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
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

        setNetworkCallback()
        registerNetworkCallback()
    }

    override fun onResume() {
        super.onResume()

        popularViewModel.fetchKeywords()
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
            val isOffline = it.isNullOrEmpty()

            binding.pbPopular.isVisible = false
            binding.tvNetworkFail.isVisible = isOffline

            if (isOffline.not()) {
                adapter.submitList(it)
            }
        })
    }

    private fun setNetworkCallback() {
        networkCallBack = object : ConnectivityManager.NetworkCallback() {

            override fun onAvailable(network: Network) {
                if (popularViewModel.items.value.isNullOrEmpty()) {
                    CoroutineScope(Dispatchers.Main).launch {
                        binding.tvNetworkFail.isVisible = false
                        binding.pbPopular.isVisible = true
                    }

                    popularViewModel.fetchKeywords()
                }
            }
        }
    }

    private fun registerNetworkCallback() {
        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()

        getSystemService(
            requireContext(),
            ConnectivityManager::class.java
        )?.registerNetworkCallback(networkRequest, networkCallBack)
    }

    override fun onDestroy() {
        super.onDestroy()

        terminateNetworkCallback()
    }

    private fun terminateNetworkCallback() {
        getSystemService(
            requireContext(),
            ConnectivityManager::class.java
        )?.unregisterNetworkCallback(networkCallBack)
    }
}
