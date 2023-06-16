package com.example.lolgg.detailRecord.fragment_synthesis

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.lolgg.*

class SynthesisRecordAdapter(private val summonerDTO: SummonerDTO, private val match : MatchDTO) : RecyclerView.Adapter<SynthesisRecordAdapter.SynthesisViewHolder>() {
    inner class SynthesisViewHolder(private val view : View) : RecyclerView.ViewHolder(view) {
        val championImgView : ImageView = view.findViewById(R.id.championImgView)
        val kdaView : TextView = view.findViewById(R.id.kda)
        val killRateView : TextView = view.findViewById(R.id.killRate)
        val perksPrimaryStyleImgView : MutableList<ImageView> = getPerks()
        val spellImgView : MutableList<ImageView> = getSpell()
        val itemImgView : MutableList<ImageView> = getItems()
        val summonerNameView : TextView = view.findViewById(R.id.summonerName)

        private fun getItems() : MutableList<ImageView>
        {
            val items = mutableListOf<ImageView>()
            items.add(view.findViewById(R.id.itemImgView1))
            items.add(view.findViewById(R.id.itemImgView2))
            items.add(view.findViewById(R.id.itemImgView3))
            items.add(view.findViewById(R.id.itemImgView4))
            items.add(view.findViewById(R.id.itemImgView5))
            items.add(view.findViewById(R.id.itemImgView6))
            items.add(view.findViewById(R.id.itemImgView7))

            return items
        }

        private fun getPerks() : MutableList<ImageView>
        {
            val perks = mutableListOf<ImageView>()
            perks.add(view.findViewById(R.id.perksPrimaryStyleImgView))
            perks.add(view.findViewById(R.id.perksSubStyleImgView))

            return perks
        }

        private fun getSpell() : MutableList<ImageView>
        {
            val spell = mutableListOf<ImageView>()
            spell.add(view.findViewById(R.id.spellImgView1))
            spell.add(view.findViewById(R.id.spellImgView2))

            return spell
        }

        private fun clickListener()
        {
            // 해당 소환사의 정보로 이동
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SynthesisViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.synthesis_item, parent, false)
        return SynthesisViewHolder(view)
    }
    override fun getItemCount(): Int {
        return 5
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: SynthesisViewHolder, position: Int) {

        var player : ParticipantDTO? = null
        for(summoners in match.participants)
        {
            if(summoners.summonerName != summonerDTO.name)
                continue

            player = summoners
            break
        }

        if(player == null)
            return

        holder.summonerNameView.text = player.summonerName
        holder.kdaView.text = "${player.kills} / ${player.deaths} / ${player.assists}"

        holder.killRateView.text = ""

    }
/*
    fun setIcon(requestURL : String, radius : Int, imageView : ImageView)
    {
        Glide.with(context).load(requestURL)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(radius))).into(imageView)
    }
 */
}