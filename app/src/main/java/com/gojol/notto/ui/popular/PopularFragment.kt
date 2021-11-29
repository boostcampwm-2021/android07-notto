package com.gojol.notto.ui.popular

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.gojol.notto.R
import com.gojol.notto.databinding.FragmentPopularBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PopularFragment : Fragment() {

    private lateinit var binding: FragmentPopularBinding
    private lateinit var adapter: PopularAdapter

    private val popularViewModel: PopularViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

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
        initRefreshLayout()
    }

    override fun onResume() {
        super.onResume()

        popularViewModel.fetchKeywords()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.popular_toolbar_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_refresh -> {
                binding.swipeRefreshLayout.isRefreshing = true
                popularViewModel.fetchKeywords()

                return true
            }
        }

        return super.onOptionsItemSelected(item)
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
            binding.swipeRefreshLayout.isRefreshing = false
            binding.pbPopular.isVisible = false
            binding.tvNetworkFail.isVisible = it.isNullOrEmpty()

            adapter.submitList(it)
        })
    }

    private fun initRefreshLayout() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            popularViewModel.fetchKeywords()
        }
    }
}
