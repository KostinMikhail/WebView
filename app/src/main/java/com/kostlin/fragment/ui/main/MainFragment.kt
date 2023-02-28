package com.kostlin.fragment.ui.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.kostlin.fragment.R
import com.kostlin.fragment.databinding.FragmentMainBinding
import com.kostlin.fragment.ui.logic.DataTransferClass


class MainFragment : Fragment(), DataTransferClass.OnClick {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = MainFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        DataTransferClass.instance!!.setListener(this)


        binding.btnPubg.setOnClickListener {
            val transaction: FragmentTransaction =
                requireActivity().supportFragmentManager.beginTransaction()
            transaction.add(R.id.container, PubgFragment.newInstance())
            transaction.commit()

        }
        binding.btnBrawlStars.setOnClickListener {
            val transaction: FragmentTransaction =
                requireActivity().supportFragmentManager.beginTransaction()
            transaction.add(R.id.container, BrawlFragment.newInstance())
            transaction.commit()
        }

        binding.btnClashRoyal.setOnClickListener {
            val transaction: FragmentTransaction =
                requireActivity().supportFragmentManager.beginTransaction()
            transaction.add(R.id.container, ClashFragment.newInstance())
            transaction.commit()
        }

        binding.btnHomescapes.setOnClickListener {
            val transaction: FragmentTransaction =
                requireActivity().supportFragmentManager.beginTransaction()
            transaction.add(R.id.container, HomescapesFragment.newInstance())
            transaction.commit()
        }

        binding.btnGenshin.setOnClickListener {
            val transaction: FragmentTransaction =
                requireActivity().supportFragmentManager.beginTransaction()
            transaction.add(R.id.container, GenshinFragment.newInstance())
            transaction.commit()
        }

        binding.btnCandy.setOnClickListener {
            val transaction: FragmentTransaction =
                requireActivity().supportFragmentManager.beginTransaction()
            transaction.add(R.id.container, CandyFragment.newInstance())
            transaction.commit()
        }
        binding.tvFirebase.setText("")
    }

    override fun getData(count: String) {
        var s = getData("FireBaseLink").toString()
        binding.tvFirebase.setText(s)
    }
}
