package com.kostlin.fragment.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kostlin.fragment.R
import com.kostlin.fragment.model.Match
import com.kostlin.fragment.model.MatchesAdapter
import com.kostlin.fragment.model.MatchesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainFragment : Fragment() {

    private lateinit var matchesRecyclerView: RecyclerView
    private lateinit var matchesAdapter: MatchesAdapter
    private var matchList: List<Match> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        matchesRecyclerView = view.findViewById(R.id.matchesRecyclerView)
        matchesRecyclerView.layoutManager = LinearLayoutManager(activity)

        matchesAdapter = MatchesAdapter(matchList)
        matchesRecyclerView.adapter = matchesAdapter

        fetchMatches()
    }
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.sportsdata.io")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val matchesApi = retrofit.create(MatchesApi::class.java)

    private fun fetchMatches() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                // Выполняем запрос на сервер через Retrofit
                val matches = matchesApi.getMatches()

                // Обновляем список матчей в адаптере и уведомляем его об изменениях
                withContext(Dispatchers.Main) {
                    matchesAdapter.matchList = matches
                    matchesAdapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
