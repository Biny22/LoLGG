package com.example.lolgg

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import kotlin.math.roundToInt

class TierOfSummonerAdapter(private val summonerDTO: SummonerDTO, private val context: Context) : RecyclerView.Adapter<TierOfSummonerAdapter.TierViewHolder>() {
    inner class TierViewHolder(private val view : View) : ViewHolder(view) {
        val rankOddsTextView : TextView = view.findViewById(R.id.rankOdds)
        val rankTypeTextView : TextView  = view.findViewById(R.id.rankType)
        val tierImageView : ImageView = view.findViewById(R.id.tierImageView)
        val tierNameTextView : TextView = view.findViewById(R.id.tierName)
        val leaguePointTextView : TextView = view.findViewById(R.id.leaguePoint)
        val container : ConstraintLayout = view.findViewById(R.id.tierItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TierViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tier_item, parent, false)
        return TierViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TierViewHolder, position: Int) {

        holder.container.setBackgroundResource(R.drawable.round_stroke)
        if(position == 0)
        {
            holder.rankTypeTextView.text = "개인 랭크"
            if(summonerDTO.soloRank == null)
                return

            val rankDTO = summonerDTO.soloRank
            val wins = rankDTO.wins.toInt()
            val losses = rankDTO.losses.toInt()
            val odds = (wins / (wins + losses) * 100.0).roundToInt()

            //holder.tierImageView.setBackgroundResource(R.drawable.emblem_challenger)
            holder.leaguePointTextView.text = "${rankDTO.leaguePoints} LP"
            holder.rankOddsTextView.text = "${rankDTO.wins}승 ${rankDTO.losses}패 ($odds%)"
            holder.tierNameTextView.text = "${rankDTO.leagueId}  ${rankDTO.tier}"
// 홍성희
            Glide.with(context).load(R.drawable.emblem_challenger).override(500,250).into(holder.tierImageView)
        }
        else
        {
            holder.rankTypeTextView.text = "자유 랭크"

            if(summonerDTO.flexRank == null)
                return
        }
    }

    override fun getItemCount(): Int {
        return 2
    }
}