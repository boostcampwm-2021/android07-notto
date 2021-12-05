package com.gojol.notto.ui.option

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebViewClient
import androidx.databinding.DataBindingUtil
import com.gojol.notto.R
import com.gojol.notto.databinding.ActivityContributorBinding

class ContributorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityContributorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_contributor)
        binding.lifecycleOwner = this

        initWebView()
        initClickListener()
        loadWebView()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        binding.wvContributor.apply {
            webViewClient = WebViewClient()
            settings.javaScriptEnabled = true
        }
    }

    private fun initClickListener() {
        binding.tbContributor.setNavigationOnClickListener {
            finish()
        }
    }

    private fun loadWebView() {
        val url = intent.getStringExtra("url") ?: finish()
        binding.wvContributor.loadUrl(url as String)
    }
}
