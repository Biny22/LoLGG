package com.example.lolgg

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
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

    val resImg : MutableMap<String, Int> by lazy { getResImg() }

    @JvmName("getResImg1")
    fun getResImg() : MutableMap<String, Int>
    {
        val resImg = mutableMapOf<String, Int>()
        resImg.put("IRON", R.drawable.emblem_iron)
        resImg.put("BRONZE", R.drawable.emblem_bronze)
        resImg.put("SILVER", R.drawable.emblem_silver)
        resImg.put("GOLD", R.drawable.emblem_gold)
        resImg.put("PLATINUM", R.drawable.emblem_platinum)
        resImg.put("DIAMOND", R.drawable.emblem_diamond)
        resImg.put("MASTER", R.drawable.emblem_master)
        resImg.put("GRANDMASTER", R.drawable.emblem_grandmaster)
        resImg.put("CHALLENGER", R.drawable.emblem_challenger)

        return resImg
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TierViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.tier_item, parent, false)
        return TierViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: TierViewHolder, position: Int)
    {
        holder.container.setBackgroundResource(R.drawable.round_stroke)
        if(position == 0)
        {
            holder.rankTypeTextView.text = "개인 랭크"
            if(summonerDTO.soloRank == null)
                return

            val rankDTO = summonerDTO.soloRank
            setItemView(holder, rankDTO)
        }
        else
        {
            holder.rankTypeTextView.text = "자유 랭크"
            if(summonerDTO.flexRank == null)
                return

            val rankDTO = summonerDTO.flexRank
            setItemView(holder, rankDTO)
        }
    }

    override fun getItemCount(): Int {
        return 2
    } // 징주영

    @SuppressLint("SetTextI18n")
    fun setItemView(holder : TierViewHolder, rankDTO: RankDTO)
    {
        val wins = rankDTO.wins.toDouble()
        val losses = rankDTO.losses.toDouble()
        val odds = (wins / (wins + losses) * 100.0).roundToInt()

        holder.leaguePointTextView.text = "${rankDTO.leaguePoints} LP"
        holder.rankOddsTextView.text = "${rankDTO.wins}승 ${rankDTO.losses}패 ($odds%)"
        holder.tierNameTextView.text = "${rankDTO.leagueId}  ${rankDTO.tier}"
        val resId = resImg[rankDTO.leagueId]
        Glide.with(context).load(resId).into(holder.tierImageView)
    }
}