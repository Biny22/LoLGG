package com.example.lolgg

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.lolgg.network.Network
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.FileNotFoundException
import java.io.Serializable
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class RecordOfSummonerAdapter(private val summonerDTO : SummonerDTO, private val context: Context) : RecyclerView.Adapter<RecordOfSummonerAdapter.RecordViewHolder>() {
    inner class RecordViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

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
    }

    inner class RecordLoadingViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
    }


    private val VIEW_TYPE_ITEM = 0
    private val VIEW_TYPE_LOADING = 0

    private val network : Network = Network(summonerDTO)
    val apiKey = "RGAPI-17cf3052-66a7-406f-a245-764d7b2a9d7b"

    val matches : MutableList<String> by lazy {
        runBlocking {
            val start = 0
            val count = 20
            network.requestMatchId(start, count)
        }
    }

    val spellsDTO : SpellsDTO by lazy { getSpells() }

    val itemDTO : ItemsDTO by lazy { getItem() }

    val runeDTO : RunesForgedDTO by lazy { getRunesReforgedDTO() }

    val championsDTO : ChampionsDTO by lazy { network.getChampionsDTO() }

    private lateinit var resources : Resources

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder
    {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.record_item, parent, false)
        return RecordViewHolder(view)
        /*
        if(viewType == VIEW_TYPE_ITEM) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.record_item, parent, false)
            return RecordViewHolder(view)
        }

        else
        {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.record_loading, parent, false)
            return RecordLoadingViewHolder(view)
        }

         */
    }

    fun onBindRecordViewHolder(holder: RecordViewHolder, position: Int)
    {

        //Log.d("recordActivity", "start : $start")
        val matchInfoDTO = getSummaryMatchInfo(position) ?: return

        //홍성희홍성희

        // 게임 진행 시간 set
        val gameStartTimestamp = matchInfoDTO.gameStartTimestamp.toLong()
        val gameEndTimestamp = matchInfoDTO.gameEndTimestamp.toLong()
        setPeriodTextView(gameStartTimestamp, gameEndTimestamp, holder.periodTextView)

        // set championIcon
        val championId = matchInfoDTO.participants.championId
        setChampionIcon(championId, holder.championImgView)

        // set itemIcon
        setItemIcon(holder.itemImgView, matchInfoDTO.participants.items)

        // set spellIcon
        val spellId = matchInfoDTO.participants.spellId
        setSpellIcon(spellId, holder.spellImgView)

        // set primaryStyle
        val primaryStyle = matchInfoDTO.participants.runeOfSummonerDTO.primaryStyle
        setPrimaryStyleIcon(primaryStyle, holder.runeImgView[0])

        // set subStyle
        val subStyle = matchInfoDTO.participants.runeOfSummonerDTO.subStyle
        setSubStyleIcon(subStyle, holder.runeImgView[1])

        // set gameMode
        setGameMode(matchInfoDTO.queueId, holder.gameModeTextView)

        // set kda
        setKda(matchInfoDTO.participants, holder.kdaTextView)

        // set date
        setDate(gameEndTimestamp, holder.dateTextView)

        // set Result
        val result = matchInfoDTO.participants.win
        setResult(result, holder.linearLayout, holder.resultTextView)

        // killRate set
        val killParticipation = matchInfoDTO.participants.challenges.killParticipation
        setKillRate(killParticipation, holder.killRateTextView)


        holder.container.setBackgroundResource(R.drawable.round_ex1)
        holder.container.clipToOutline = true // 테두리 바깥으로 튀어나가지 않게 함

    }

    fun onBindLoadingViewHolder(holder: RecordLoadingViewHolder, position: Int)
    {

    }

    override fun getItemCount(): Int {
        return matches.size
    }

    override fun getItemViewType(position: Int): Int {
        if(matches[position] == "null")
            Log.d("getItemViewType", "${matches[position]}")
        return if(matches[position] != "null") VIEW_TYPE_ITEM else VIEW_TYPE_LOADING
    }
// 홍성희홍성희

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
            for(spell in spellsDTO.data)
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
        for(runePath in runeDTO.data)
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
        for(runePath in runeDTO.data)
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
    fun setPeriodTextView(gameStartTimestamp : Long, gameEndTimestamp : Long, periodTextView: TextView)
    {
        val timestamp  = gameEndTimestamp - gameStartTimestamp
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

    fun setKda(participants : SummaryParticipantDTO, kdaTextView : TextView)
    {
        val kill = participants.kill
        val death = participants.deaths
        val assist = participants.assists
        val kda = "$kill / $death / $assist"
        kdaTextView.text = kda
    }

    @SuppressLint("SimpleDateFormat")
    fun setDate(gameEndTimestamp : Long, dateTextView : TextView)
    {
        val dateFormat = SimpleDateFormat("MM월 dd일")
        dateTextView.text = dateFormat.format(gameEndTimestamp)
    }

    fun setResult(result : Boolean,linearLayout: LinearLayout, resultTextView : TextView)
    {
        if(result)
        {
            linearLayout.setBackgroundResource(R.drawable.round_victory)
            resultTextView.text = "승리"
        }
        else
        {
            linearLayout.setBackgroundResource(R.drawable.round_defeat)
            resultTextView.text = "패배"
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
            //setImageDrawable(holder.itemImgView[i], itemIcon!!)
        }
    }

    fun setIcon(requestURL : String, radius : Int, imageView : ImageView)
    {
        Glide.with(context).load(requestURL)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(radius))).into(imageView)
    }

    suspend fun requestRunesReforgedDTO() : JSONArray
    {
        val runesReforgedData : JSONArray

        withContext(Dispatchers.IO) {
            val requestURL = "https://ddragon.leagueoflegends.com/cdn/10.16.1/data/ko_KR/runesReforged.json"
            val url = URL(requestURL)
            val httpURLConnection = url.openConnection() as HttpURLConnection
            val inputStream = httpURLConnection.inputStream
            val scan = Scanner(inputStream)
            runesReforgedData = JSONArray(scan.nextLine())
        }

        return runesReforgedData
    }

    fun getRunesReforgedDTO() : RunesForgedDTO
    {
        // [ 지배, 영감, 결의, 마법... ] -> runesReforgedJson
        val runesReforgedJson = runBlocking { requestRunesReforgedDTO() }

        return getRuneDataDTO(runesReforgedJson)
    }

    fun getRuneDataDTO(runesReforgedJson : JSONArray) : RunesForgedDTO
    {
        val data = mutableListOf<RuneDataDTO>()

        for(i in 0 until runesReforgedJson.length())
        {
            // { 지배 }
            val json = JSONObject(runesReforgedJson[i].toString())

            val id = json["id"].toString()
            val key = json["key"].toString()
            val icon = json["icon"].toString()
            val name = json["name"].toString()

            val slotsArray = JSONArray(json["slots"].toString())
            val slots = getSlots(slotsArray)

            data.add(RuneDataDTO(id, key, icon, name, slots))
        }

        return RunesForgedDTO(data)
    }

    fun getSlots(slotsArray : JSONArray) : List<SlotDTO>
    {
        val slots = mutableListOf<SlotDTO>()

        for(j in 0 until slotsArray.length())
        {
            val runePaths = JSONObject(slotsArray[j].toString())
            val array = JSONArray(runePaths["runes"].toString())

            slots.add(getSlotDTO(array))
        }
        // slots 하나가 지배룬 전체, 영감룬 전체

        return slots
    }

    fun getSlotDTO(slots : JSONArray) : SlotDTO
    {
        val runes = mutableListOf<RuneDTO>()
        for(k in 0 until slots.length())
        {
            val rune = JSONObject(slots[k].toString())
            runes.add(getRuneDTO(rune))
        }

        return SlotDTO(runes)
    }

    fun getRuneDTO(rune : JSONObject) : RuneDTO
    {
        val id = rune["id"].toString()
        val key = rune["key"].toString()
        val icon = rune["icon"].toString()
        val name = rune["name"].toString()
        val shortDesc = rune["shortDesc"].toString()
        val longDesc = rune["longDesc"].toString()

        return RuneDTO(id, key, icon, name, shortDesc, longDesc)
    }

    fun getItem() : ItemsDTO
    {
        // 9초
        val items = runBlocking { requestItemsDTO() }
        val itemsJson = JSONObject(items["data"].toString())
        val data = mutableListOf<ItemDataDTO>()
        var currentTime = 0.0
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

        println("아이템 개수 : ${itemsJson.length()}")
        println("소요 시간 : ${currentTime}")

        val type = items["type"].toString()
        val version = items["version"].toString()

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

    fun getSpells(): SpellsDTO
    {
        val spellsJson = runBlocking { requestSpellsDTO() }
        val type = spellsJson["type"].toString()
        val version = spellsJson["version"].toString()

        val spellData = JSONObject(spellsJson["data"].toString())
        val data = mutableListOf<SpellDTO>()

        for (spellName in spellData.keys()) {
            val spell = JSONObject(spellData[spellName].toString())

            val id = spell["id"].toString()
            val name = spell["name"].toString()
            val description = spell["description"].toString()
            val key = spell["key"].toString()
            val image = spell["image"].toString()
            val spellDTO = SpellDTO(id, name, description, key, image)
            data.add(spellDTO)
        }

        return SpellsDTO(type, version, data)
    }

    suspend fun requestSpellsDTO() : JSONObject
    {
        val spellsData : JSONObject
        withContext(Dispatchers.IO) {
            val requestURL = "https://ddragon.leagueoflegends.com/cdn/13.6.1/data/ko_KR/summoner.json"
            val url = URL(requestURL)
            val httpURLConnection = url.openConnection() as HttpURLConnection
            val inputStream = httpURLConnection.inputStream
            val scan = Scanner(inputStream)
            spellsData = JSONObject(scan.nextLine())
        }

        return spellsData
    }

    fun getSummaryMatchInfo(index : Int) : SummaryMatchInfoDTO?
    {
        val matchData = runBlocking { requestSummaryMatchesInfo(matches[index]) } ?: return null

        println("matchData : $matchData")
        var summaryMatchInfoDTO : SummaryMatchInfoDTO?


            val infodata = JSONObject(matchData["info"].toString())
            val metadata = JSONObject(matchData["metadata"].toString())
            val participants = metadata["participants"].toString()
            val tokenizer = StringTokenizer(participants)
            var count = 0
            while(tokenizer.hasMoreTokens())
            {
                val participant = tokenizer.nextToken("[\",]")
                if(participant == summonerDTO.puuid)
                    break
                count++
            } //홍성희홍성희

            val gameCreation = infodata["gameCreation"].toString()
            val gameStartTimeStamp = infodata["gameStartTimestamp"].toString()

            val gameEndTimeStamp : String = if(infodata.has("gameEndTimestamp")) {
                infodata["gameEndTimestamp"].toString()
            } else {
                val gameDuration = infodata["gameDuration"].toString().toLong()
                (gameDuration + gameStartTimeStamp.toLong()).toString()
            }
            val queueId = infodata["queueId"].toString()
            val gameType = infodata["gameType"].toString()
            val participantsData = JSONArray(infodata["participants"].toString())

            val p = JSONObject(participantsData[count].toString())

            val assists = p["assists"].toString()
            val championId = p["championId"].toString()
            val championName = p["championName"].toString()
            val deaths = p["deaths"].toString()


            val challengesDTO : ChallengesDTO
            if(p.has("challenges"))
            {
                val challenges = JSONObject(p["challenges"].toString())
                val killParticipation : String
                if(challenges.has("killParticipation"))
                    killParticipation = challenges["killParticipation"].toString()
                else
                    killParticipation = "0"

                challengesDTO = ChallengesDTO(killParticipation)
            }
            else
            {
                challengesDTO = ChallengesDTO("0")
            }

            //killParticipation

            // 아이템템
            val items = mutableListOf<String>()
            for(i in 0..6)
            {
                val item = p["item$i"].toString()
                items.add(item)
            }
            val win = p["win"] as Boolean

            // 스펠
            val spellId = mutableListOf<String>()
            for(i in 1..2)
            {
                spellId.add(p["summoner${i}Id"].toString())
            }

            // 킬
            val kill = p["kills"].toString()

            val perks = JSONObject(p["perks"].toString())

            // 능력치 룬
            val ss = JSONObject(perks["statPerks"].toString())
            val defense = ss["defense"].toString()
            val flex = ss["flex"].toString()
            val offense = ss["offense"].toString()
            val statPerks = StatPerksDTO(defense, flex, offense)

            val stylesJson = JSONArray(perks["styles"].toString())
            val styles = mutableListOf<StyleDTO>()

            for(i in 0 until stylesJson.length())
            {
                val s = JSONObject(stylesJson[i].toString())
                val description = s["description"].toString()
                val sel = JSONArray(s["selections"].toString())
                val runeId = mutableListOf<String>()
                val style = s["style"].toString()

                for(j in 0 until sel.length())
                {
                    val path = JSONObject(sel[j].toString())
                    runeId.add(path["perk"].toString())
                }

                val styleDTO = StyleDTO(description, runeId, style)
                styles.add(styleDTO)
            }

            val runeOfSummonerDTO = RuneOfSummonerDTO(statPerks, styles[0], styles[1])

            // 역;!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! 홍성희

            val summaryParticipantDTO = SummaryParticipantDTO(assists, championId, championName,
                challengesDTO, deaths, items, kill,runeOfSummonerDTO, spellId, win)
            summaryMatchInfoDTO = SummaryMatchInfoDTO(gameCreation, gameStartTimeStamp, gameEndTimeStamp,
                queueId, gameType, summaryParticipantDTO)

        return summaryMatchInfoDTO
    }

    suspend fun requestSummaryMatchesInfo(matchId : String) : JSONObject?
    {
        var matchData : JSONObject? = null
        withContext(Dispatchers.IO) {
            try {
                val requestURL = "https://asia.api.riotgames.com/lol/match/v5/matches/${matchId}?api_key=$apiKey"
                val url = URL(requestURL)
                val httpURLConnection = url.openConnection() as HttpURLConnection
                val inputStream = httpURLConnection.inputStream
                val scan = Scanner(inputStream)
                matchData = JSONObject(scan.nextLine())
            } catch (e : FileNotFoundException) {
                e.printStackTrace()
                return@withContext null
            }
        }

        return matchData
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int) {

        onBindRecordViewHolder(holder, position)
        println(position)
        /*
        if(holder is RecordOfSummonerAdapter.RecordOfSummonerViewHolder)
        {
            println("pos : $position")
        }
        else
        {
            println("Gd") // 홍성희홍성희
            //onBindLoadingViewHolder(holder as RecordLoadingViewHolder, position)
        }

         */
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

