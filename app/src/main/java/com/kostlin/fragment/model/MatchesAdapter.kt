package com.kostlin.fragment.model

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kostlin.fragment.databinding.MatchItemBinding

class MatchesAdapter(var matchList: List<Match>) :
    RecyclerView.Adapter<MatchesAdapter.MatchViewHolder>() {
    init {
        Log.d("MatchesAdapter", "Matches: ${matchList.size}")
    }
    class MatchViewHolder(private val binding: MatchItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(match: Match) {
            binding.homeTeamTextView.text = match.areaName
            binding.awayTeamTextView.text = match.name.toString()
            binding.timeTextView.text = match.format.toString()
            binding.leagueTextView.text = match.type
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            MatchViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = MatchItemBinding.inflate(inflater, parent, false)
        return MatchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        holder.bind(matchList[position])
    }

    override fun getItemCount(): Int {
        return matchList.size
    }

}