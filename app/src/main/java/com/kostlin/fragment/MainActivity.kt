package com.kostlin.fragment

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kostlin.fragment.databinding.FragmentMainBinding
import com.kostlin.fragment.ui.main.MainFragment
import com.kostlin.fragment.ui.main.PubgFragment

class MainActivity : AppCompatActivity() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }

    }
}