package com.matiasmandelbaum.alejandriaapp.common.splashscreen

import android.animation.ValueAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.matiasmandelbaum.alejandriaapp.databinding.ActivitySplashScreenBinding
import com.matiasmandelbaum.alejandriaapp.ui.MainActivity


class FirstScreen : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    companion object {
        private const val PROGRESS_START = 0
        private const val PROGRESS_END = 100
        private const val ANIMATION_DURATION = 3000L
        private const val DELAY_TIME = 3000L
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }

        val progressBar = binding.progressBarSplashScreen
        val progressAnimator = ValueAnimator.ofInt(PROGRESS_START, PROGRESS_END)
        progressAnimator.duration = ANIMATION_DURATION
        progressAnimator.addUpdateListener { animation ->
            progressBar.progress = animation.animatedValue as Int
        }
        progressAnimator.start()

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }, DELAY_TIME)
    }
}