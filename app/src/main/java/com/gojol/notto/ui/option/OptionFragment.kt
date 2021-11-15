package com.gojol.notto.ui.option

import android.app.AlarmManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.gojol.notto.R
import com.gojol.notto.databinding.FragmentOptionBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OptionFragment : Fragment() {

    private lateinit var binding: FragmentOptionBinding
    private val optionViewModel: OptionViewModel by viewModels()
    private lateinit var licenseAdapter: LicenseAdapter
    private lateinit var alarmManager: AlarmManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_option, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewmodel = optionViewModel

        initRecyclerView()
        initObserver()
        initListener()
    }

    private fun initRecyclerView() {
        licenseAdapter = LicenseAdapter()
        binding.rvOptionOpenSourceLicense.adapter = licenseAdapter
    }

    private fun initObserver() {
        optionViewModel.licenseList.observe(viewLifecycleOwner) {
            licenseAdapter.setLicenseList(it)
        }
        optionViewModel.isPushChecked.observe(viewLifecycleOwner) {
            if (it) DayNotificationManager.setAlarm(requireContext())
            else DayNotificationManager.cancelAlarm(requireContext())
        }
    }

    private fun initListener() {
        binding.btnOptionExpand.setOnClickListener {
            if (binding.rvOptionOpenSourceLicense.visibility == View.VISIBLE) {
                binding.rvOptionOpenSourceLicense.visibility = View.GONE
                it.animate().setDuration(200).rotation(0f)
            } else {
                binding.rvOptionOpenSourceLicense.visibility = View.VISIBLE
                it.animate().setDuration(200).rotation(180f)
            }
        }
    }
}
