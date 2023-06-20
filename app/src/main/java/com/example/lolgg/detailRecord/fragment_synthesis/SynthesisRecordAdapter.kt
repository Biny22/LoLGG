package com.example.lolgg.detailRecord.fragment_synthesis

import android.annotation.SuppressLint
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
import com.example.lolgg.dto.Runes


class SynthesisRecordAdapter(private val summonerDTO: SummonerDTO, private val match : MatchDTO,
                             private val championsDTO: ChampionsDTO, private val itemDTO: ItemsDTO,
                             private val runes : Runes.RunesForgedDTO, private val spells: Spells.SpellsDTO,
                             private var index : Int) : RecyclerView.Adapter<SynthesisRecordAdapter.SynthesisViewHolder>() {
    inner class SynthesisViewHolder(private val view : View) : RecyclerView.ViewHolder(view) {
        val championImgView : ImageView = view.findViewById(R.id.championImgView)
        val kdaView : TextView = view.findViewById(R.id.kda)
        val killRateView : TextView = view.findViewById(R.id.killRate)
        val perksImgView : MutableList<ImageView> = getPerks()
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

    @SuppressLint("SetTextI18n", "ResourceAsColor")
    override fun onBindViewHolder(holder: SynthesisViewHolder, position: Int) {


        val player : ParticipantDTO = match.participants[position+index]

        /*
        for(summoners in match.participants)
        {
            if(summoners.summonerName != summonerDTO.name)
                continue

            player = summoners
            break
        }

        if(player == null)
            return

         */

        holder.summonerNameView.text = player.summonerName
        holder.kdaView.text = "${player.kills} / ${player.deaths} / ${player.assists}"

        holder.killRateView.text = ""
        println("onBindViewHolder")

        val championId = player.championId
        setChampionIcon(championId, holder.championImgView)

        val spellId = player.summonerSpell
        setSpellIcon(spellId, holder.spellImgView)

        // set primaryStyle
        val primaryStyle = player.perks.primaryStyle
        setPrimaryStyleIcon(primaryStyle, holder.perksImgView[0])

        // set subStyle
        val subStyle = player.perks.subStyle
        setSubStyleIcon(subStyle, holder.perksImgView[0])


        setItemIcon(holder.itemImgView, player.purchasedItems)

        holder.itemView.setBackgroundResource(R.drawable.round_ex1)
        holder.itemView.clipToOutline = true // 테두리 바깥으로 튀어나가지 않게 함
    }

    fun setChampionIcon(championId : String, championImgView : ImageView)
    {
        for(championDTO in championsDTO.data)
        {
            if(championDTO.key != championId)
                continue

            val requestURL = "http://ddragon.leagueoflegends.com/cdn/13.7.1/img/champion/${championDTO.id}.png"
            val radius = 50
            setIcon(requestURL, radius, championImgView)
            return
        }
    }

    fun setItemIcon(itemImgView : MutableList<ImageView>, items : List<String>)
    {
        for(i in 0 until itemImgView.size)
        {
            val itemId = items[i]

            if(itemId == "0")
            {
                Glide.with(itemImgView[i].context).clear(itemImgView[i])
                itemImgView[i].setBackgroundResource(R.drawable.round_ex3)
                continue
            }

            for(item in itemDTO.data)
            {
                if(itemId != item.itemId)
                    continue

                val itemIcon = item.itemDTO.image
                val requestURL = "http://ddragon.leagueoflegends.com/cdn/13.7.1/img/item/${itemIcon}"
                val radius = 20
                setIcon(requestURL, radius, itemImgView[i])
                break
            }
        }
    }

    fun setSubStyleIcon(subStyle : StyleDTO, runeImgView: ImageView)
    {
        for(runePath in runes.data)
        {
            if(runePath.id != subStyle.style)
                continue

            // 룬 경로가 같다면
            val requestURL = "https://ddragon.canisback.com/img/${runePath.icon}"
            val radius = 25
            setIcon(requestURL, radius, runeImgView)
            break
        }
    }

    fun setPrimaryStyleIcon(primaryStyle : StyleDTO, runeImgView : ImageView)
    {
        for(runePath in runes.data)
        {
            if(runePath.id != primaryStyle.style)
                continue

            // 룬 경로가 같다면
            for(rune in runePath.slots[0].runes)
            {
                // rune 은 왕룬임
                if(rune.id != primaryStyle.runeId[0])
                    continue

                // 동일한 룬이라면
                val requestURL = "https://ddragon.canisback.com/img/${rune.icon}"
                val radius = 25
                setIcon(requestURL, radius, runeImgView)
                break
            }
            break
        }
    }

    fun setSpellIcon(spellId : List<String>, spellImgView : MutableList<ImageView>)
    {
        for(i in 0 until spellImgView.size)
        {
            val spellKeyOfSummoner = spellId[i]
            for(spell in spells.data)
            {
                if(spell.key != spellKeyOfSummoner)
                    continue

                val requestURL = "http://ddragon.leagueoflegends.com/cdn/13.7.1/img/spell/${spell.id}.png"
                val radius = 20
                setIcon(requestURL, radius, spellImgView[i])
                break
            }
        }
    }


    fun setIcon(requestURL : String, radius : Int, imageView : ImageView)
    {
        Glide.with(imageView.context).load(requestURL)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(radius))).into(imageView)
    }

}