package com.kostlin.fragment.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kostlin.fragment.R

class BrawlFragment : Fragment(R.layout.fragment_brawl) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return super.onCreateView(inflater, container, savedInstanceState)

    }

    companion object {
        fun newInstance() = BrawlFragment()
    }
}
