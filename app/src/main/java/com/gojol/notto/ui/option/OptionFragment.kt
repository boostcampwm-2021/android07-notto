package com.gojol.notto.ui.option

import android.app.AlarmManager
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.gojol.notto.R
import com.gojol.notto.databinding.FragmentOptionBinding
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OptionFragment : Fragment() {

    private lateinit var binding: FragmentOptionBinding
    private val optionViewModel: OptionViewModel by viewModels()
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

        initObserver()
    }

    private fun initObserver() {
        optionViewModel.isPushChecked.observe(viewLifecycleOwner) {
            if (it) DayNotificationManager.setAlarm(requireContext())
            else DayNotificationManager.cancelAlarm(requireContext())
        }
        optionViewModel.isNavigateToLicenseClicked.observe(viewLifecycleOwner) {
            startActivity(Intent(requireContext(), OssLicensesMenuActivity::class.java))
            OssLicensesMenuActivity.setActivityTitle(getString(R.string.option_license_title))
        }
    }
}
