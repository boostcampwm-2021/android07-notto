package com.gojol.notto.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ViewTreeObserver
import android.view.animation.AlphaAnimation
import android.view.animation.TranslateAnimation
import androidx.databinding.DataBindingUtil
import com.gojol.notto.R
import com.gojol.notto.databinding.ActivitySplashBinding
import com.gojol.notto.ui.MainActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)

        initIconAnimation()

        // Animation을 다 보여주기 위해 일부러 delay를 걸어놓음.
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            intent.flags =
                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        }, 3000)
    }

    private fun initIconAnimation() {
        binding.ivSplashIcon1.viewTreeObserver.addOnGlobalLayoutListener {
            val width = binding.ivSplashIcon1.width

            val firstIconBarAnimation = TranslateAnimation(0f, -(width * 0.25f), 0f, 0f)
            firstIconBarAnimation.duration = 500
            firstIconBarAnimation.fillAfter = true

            val lastIconBarAnimation = TranslateAnimation(0f, (width * 0.25f), 0f, 0f)
            lastIconBarAnimation.duration = 500
            lastIconBarAnimation.startOffset = 500
            lastIconBarAnimation.fillAfter = true

            val textAnimation = AlphaAnimation(0.0f, 1.0f)
            textAnimation.duration = 500
            textAnimation.startOffset = 1500

            binding.ivSplashIcon1.startAnimation(firstIconBarAnimation)
            binding.ivSplashIcon3.startAnimation(lastIconBarAnimation)
            binding.tvSplashTitle.startAnimation(textAnimation)
        }
    }
}
