package com.kostlin.fragment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.kostlin.fragment.databinding.FragmentMainBinding


class SpashScreenActivity : AppCompatActivity() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private var allowed: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spashscreen)

        Handler().postDelayed({
            if (allowed) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                finish()

            } else {
                return@postDelayed
            }

        }, 3000)

    }

}
