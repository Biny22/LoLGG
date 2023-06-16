package com.example.lolgg

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.content.res.Resources
import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.lolgg.detailRecord.DetailRecordActivity
import com.example.lolgg.network.Network
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.Serializable
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class RecordOfSummonerAdapter(private val summonerDTO : SummonerDTO, private val context: Context) : RecyclerView.Adapter<ViewHolder>() {
    inner class RecordViewHolder(private val view: View) : ViewHolder(view) {

        val itemImgView: MutableList<ImageView> = getItemView()
        val spellImgView: MutableList<ImageView> = getSpellView()
        val runeImgView: MutableList<ImageView> = getRuneView()
        val championImgView: ImageView = view.findViewById(R.id.championImgView)
        val kdaTextView : TextView = view.findViewById(R.id.kda)
        val killRateTextView : TextView = view.findViewById(R.id.killRate)
        val gameModeTextView : TextView = view.findViewById(R.id.gameMode)
        val dateTextView : TextView = view.findViewById(R.id.date)
        val periodTextView : TextView = view.findViewById(R.id.period)
        val resultTextView : TextView = view.findViewById(R.id.result)

        val linearLayout : LinearLayout = view.findViewById(R.id.linearLayout)
        val container : ConstraintLayout = view.findViewById(R.id.recordItem)

        init {
            clickListener()
        }

        private fun getItemView(): MutableList<ImageView> {
            val list = mutableListOf<ImageView>()
            list.add(view.findViewById(R.id.itemImgView1))
            list.add(view.findViewById(R.id.itemImgView2))
            list.add(view.findViewById(R.id.itemImgView3))
            list.add(view.findViewById(R.id.itemImgView4))
            list.add(view.findViewById(R.id.itemImgView5))
            list.add(view.findViewById(R.id.itemImgView6))
            list.add(view.findViewById(R.id.itemImgView7))

            return list
        }

        private fun getSpellView(): MutableList<ImageView> {
            val list = mutableListOf<ImageView>()
            list.add(view.findViewById(R.id.spellImgView1))
            list.add(view.findViewById(R.id.spellImgView2))

            return list
        }

        private fun getRuneView(): MutableList<ImageView> {
            val list = mutableListOf<ImageView>()
            list.add(view.findViewById(R.id.perksPrimaryStyleImgView))
            list.add(view.findViewById(R.id.perksSubStyleImgView))

            return list
        }

        private fun clickListener()
        {
            view.setOnClickListener {
                val position = adapterPosition
                val matchDTO = matches.matchDTO[position]
                //val intent = Intent(context, )
                val intent = Intent(context, DetailRecordActivity::class.java)
                intent.putExtra("MatchDTO",matchDTO)
                intent.putExtra("SummonerDTO",summonerDTO)
                intent.addFlags(FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                Log.d("item", "Click Click")
            }
        }
    }

    inner class RecordLoadingViewHolder(private val view: View) : ViewHolder(view) {
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
    }

    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 1

    private val network : Network = Network(summonerDTO)
    val apiKey = "RGAPI-e03f0e4f-0d09-49e4-adde-7b2c8ccf1c61"
    var itemPosition = 0

    val matches = Matches(network)

    val spells = runBlocking { Spells().getSpells() }

    private val runes = Runes().getRunesReforgedDTO()

    val itemDTO : ItemsDTO by lazy { runBlocking { getItem() } }

    val championsDTO : ChampionsDTO by lazy { network.getChampionsDTO() }

    private lateinit var resources : Resources

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        if(viewType == VIEW_TYPE_ITEM) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.record_item, parent, false)
            return RecordViewHolder(view)
        }

        else
        {
            Log.d("onCreateViewHolder", "loading")
            val view = LayoutInflater.from(parent.context).inflate(R.layout.record_loading, parent, false)
            return RecordLoadingViewHolder(view)
        }
    }

    fun onBindRecordViewHolder(holder: RecordViewHolder, position: Int)
    {
        val matchDTO = matches.getMatchDTO(position) ?: return
        var index : Int = -1

        for(i in 0 until matchDTO.participants.size)
        {
            val summonerName = matchDTO.participants[i].summonerName

            if(summonerName != summonerDTO.name)
                continue

            index = i
            break
        }
        //홍성희홍성희

        // 게임 진행 시간 set
        val gameStartTimestamp = matchDTO.gameStartTimestamp.toLong()
        val gameEndTimestamp = matchDTO.gameEndTimestamp.toLong()
        val timestamp  = gameEndTimestamp - gameStartTimestamp
        setPeriodTextView(timestamp, holder.periodTextView)

        // set championIcon
        val championId = matchDTO.participants[index].championId
        setChampionIcon(championId, holder.championImgView)

        // set itemIcon
        //setItemIcon(holder.itemImgView, matchInfoDTO.participants.items)
        setItemIcon(holder.itemImgView, matchDTO.participants[index].purchasedItems)

        // set spellIcon
        val spellId = matchDTO.participants[index].summonerSpell
        setSpellIcon(spellId, holder.spellImgView)

        // set primaryStyle
        val primaryStyle = matchDTO.participants[index].perks.primaryStyle
        setPrimaryStyleIcon(primaryStyle, holder.runeImgView[0])

        // set subStyle
        val subStyle = matchDTO.participants[index].perks.subStyle
        setSubStyleIcon(subStyle, holder.runeImgView[1])

        // set gameMode
        setGameMode(matchDTO.queueId, holder.gameModeTextView)

        // set kda
        val kills = matchDTO.participants[index].kills
        val deaths = matchDTO.participants[index].deaths
        val assists = matchDTO.participants[index].assists
        setKda(kills, deaths, assists, holder.kdaTextView)

        // set date
        setDate(gameEndTimestamp, holder.dateTextView)

        // set Result
        val result = matchDTO.participants[index].win
        setResult(timestamp, result, holder.linearLayout, holder.resultTextView)

        // killRate set
        val killParticipation = matchDTO.participants[index].challenges.killParticipation
        setKillRate(killParticipation, holder.killRateTextView)


        holder.container.setBackgroundResource(R.drawable.round_ex1)
        holder.container.clipToOutline = true // 테두리 바깥으로 튀어나가지 않게 함
        itemPosition++
    }

    fun onBindLoadingViewHolder(holder: RecordLoadingViewHolder, position: Int)
    {

    }

    override fun getItemCount(): Int = matches.matchId.size

    override fun getItemViewType(position: Int): Int
    {
        return if(matches.matchId[position] == "null")  VIEW_TYPE_LOADING else VIEW_TYPE_ITEM
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
                val radius = 25
                setIcon(requestURL, radius, spellImgView[i])
                break
            }
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

    @SuppressLint("SimpleDateFormat")
    fun setPeriodTextView(timestamp : Long, periodTextView: TextView)
    {
        val periodFormat = SimpleDateFormat("mm : ss")
        periodTextView.text = periodFormat.format(timestamp)
    }

    fun setGameMode(queueId : String, gameModeTextView : TextView)
    {
        val queue = getQueueId()
        for(key in queue.keys)
        {
            if(key != queueId)
                continue

            val gameMode = queue[key]
            gameModeTextView.text = gameMode
        }
    }

    fun setKda(kills : String, deaths : String, assists : String, kdaTextView : TextView)
    {
        val kda = "$kills / $deaths / $assists"
        kdaTextView.text = kda
    }

    @SuppressLint("SimpleDateFormat")
    fun setDate(gameEndTimestamp : Long, dateTextView : TextView)
    {
        val dateFormat = SimpleDateFormat("MM월 dd일")
        dateTextView.text = dateFormat.format(gameEndTimestamp)
    }

    fun setResult(timestamp: Long, result : Boolean, linearLayout: LinearLayout, resultTextView : TextView)
    {
        when
        {
            timestamp <= 225000 -> {
                linearLayout.setBackgroundResource(R.drawable.round_replay)
                resultTextView.text = "다시"
            } // 홍성희
            result -> {
                linearLayout.setBackgroundResource(R.drawable.round_victory)
                resultTextView.text = "승리"
            }
            else -> {
                linearLayout.setBackgroundResource(R.drawable.round_defeat)
                resultTextView.text = "패배"
            }
        }
    }

    fun setKillRate(killParticipation : String, killRateTextView : TextView)
    {
        val killRate = killParticipation.toDouble() * 100
        killRateTextView.text = "${killRate.toInt()} %"
    }

    fun setItemIcon(itemImgView : MutableList<ImageView>, items : List<String>)
    {
        for(i in 0 until itemImgView.size)
        {
            val itemId = items[i]

            if(itemId == "0")
            {
                Glide.with(context).clear(itemImgView[i])
                itemImgView[i].setBackgroundResource(R.drawable.round_ex3)
                continue
            }

            for(item in itemDTO.data)
            {
                if(itemId != item.itemId)
                    continue

                val itemIcon = item.itemDTO.image
                val requestURL = "http://ddragon.leagueoflegends.com/cdn/13.7.1/img/item/${itemIcon}"
                val radius = 25
                setIcon(requestURL, radius, itemImgView[i])
                break
            }
        }
    }

    fun setIcon(requestURL : String, radius : Int, imageView : ImageView)
    {
        Glide.with(context).load(requestURL)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(radius))).into(imageView)
    }




    suspend fun getItem() : ItemsDTO
    {
        val type : String
        val version : String
        val data = mutableListOf<ItemDataDTO>()

        withContext(Dispatchers.Default) {
            val items = runBlocking { requestItemsDTO() }
            val itemsJson = JSONObject(items["data"].toString())

            for(key in itemsJson.keys())
            {
                val itemJson = JSONObject(itemsJson[key].toString())
                val name = itemJson["name"].toString()
                val description = itemJson["description"].toString()
                val plaintext = itemJson["plaintext"].toString()

                val imageJson = JSONObject(itemJson["image"].toString())
                val imageId = imageJson["full"].toString()

                val goldJson = JSONObject(itemJson["gold"].toString())
                val base = goldJson["base"].toString()
                val total = goldJson["total"].toString()
                val itemGoldDTO = ItemGoldDTO(base, total)

                val itemDTO = ItemDTO(name, description, plaintext, imageId, itemGoldDTO)
                val itemDataDTO = ItemDataDTO(key, itemDTO)
                data.add(itemDataDTO)
            }

            type = items["type"].toString()
            version = items["version"].toString()
        }
        // 9초

        return ItemsDTO(type, version, data)
    }

    suspend fun requestItemsDTO() : JSONObject
    {
        val items : JSONObject

        withContext(Dispatchers.IO) {
            val requestURL = "https://ddragon.leagueoflegends.com/cdn/13.6.1/data/ko_KR/item.json"
            val url = URL(requestURL)
            val httpURLConnection = url.openConnection() as HttpURLConnection
            val inputStream = httpURLConnection.inputStream
            val scan = Scanner(inputStream)
            items = JSONObject(scan.nextLine())
        }

        return items
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        if(holder is RecordOfSummonerAdapter.RecordViewHolder)
        {
            onBindRecordViewHolder(holder, position)
        }
        else
        {
            Log.d("onBindViewHolder", "loading")
            onBindLoadingViewHolder(holder as RecordLoadingViewHolder, position)
        }

    }
}


data class ItemsDTO(val type : String, val version : String, val data : List<ItemDataDTO>) : Serializable

data class ItemDataDTO(val itemId : String, val itemDTO : ItemDTO)

data class ItemDTO(val name : String, val description : String, val plaintext : String,
                   val image : String, val gold : ItemGoldDTO)

data class ItemGoldDTO(val base : String, val total : String)

class SpaceItemDecoration(private val spanCount: Int, private val space: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)

        /** 첫번째 행(row-1) 이후부터 있는 아이템에만 상단에 [space] 만큼의 여백을 추가한다. 즉, 첫번째 행에 있는 아이템에는 상단에 여백을 주지 않는다.*/
        if (position >= spanCount){
            outRect.top = space
            outRect.left = space
            outRect.right = space
        }
    }
}

