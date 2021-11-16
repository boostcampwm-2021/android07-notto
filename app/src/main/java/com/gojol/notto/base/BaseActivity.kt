package com.gojol.notto.base

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gojol.notto.BR
import com.gojol.notto.R
import com.gojol.notto.databinding.ActivityProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

abstract class BaseActivity<B : ViewDataBinding, VM : BaseViewModel> : AppCompatActivity() {

    protected abstract val layoutResId: Int

    protected abstract val viewModel: VM

    protected lateinit var binding: B

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, layoutResId)
        binding.lifecycleOwner = this
        binding.setVariable(BR.viewModel, viewModel)
    }

    protected fun <T> LiveData<T>.observe(block: (T?) -> Unit) {
        observe(this@BaseActivity, block)
    }

    protected fun <T> LiveData<T>.observeNotNull(block: (T) -> Unit) {
        observe(this@BaseActivity) { value -> value?.let(block) }
    }
}

@AndroidEntryPoint
class ProfileActivity : BaseActivity<ActivityProfileBinding, ProfileViewModel>() {

    override val layoutResId: Int = R.layout.activity_profile

    override val viewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.name.observeNotNull { name -> updateUi(name) }
    }

    private fun updateUi(name: String) {
        binding.tvName.text = name
        // update
    }
}

abstract class BaseViewModel : ViewModel()

@HiltViewModel
class ProfileViewModel @Inject constructor() : BaseViewModel() {

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> = _name
}
