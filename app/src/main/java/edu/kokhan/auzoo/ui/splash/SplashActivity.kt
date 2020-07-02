package edu.kokhan.auzoo.ui.splash

import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.Animation.RELATIVE_TO_SELF
import android.view.animation.RotateAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import edu.kokhan.auzoo.R
import edu.kokhan.auzoo.ui.MainActivity
import kotlinx.android.synthetic.main.activity_splash.*
import org.koin.android.viewmodel.ext.android.viewModel


class SplashActivity : AppCompatActivity() {

    private val model: SplashViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        model.shouldShowPresentationData.observe(this, Observer {
            MainActivity.start(this)
            finish()
        })

        model.rotateImageData.observe(this, Observer {
            val rotate =
                RotateAnimation(0f, 360f, RELATIVE_TO_SELF, 0.5f, RELATIVE_TO_SELF, 0.5f).apply {
                    duration = 1000
                    repeatCount = Animation.ABSOLUTE
                }

            ic_splash.startAnimation(rotate)
        })

    }
}
