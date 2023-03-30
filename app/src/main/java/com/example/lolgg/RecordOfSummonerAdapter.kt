package com.example.lolgg

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.lolgg.network.Network
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class RecordOfSummonerAdapter(private val summonerDTO : SummonerDTO, private val context: Context) : RecyclerView.Adapter<RecordOfSummonerAdapter.RecordViewHolder>() {
    inner class RecordViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        // 여기서 레이아웃 구성 요소 가져오기
        // 리스너 등록
        val itemImgView: MutableList<ImageView>
        val spellImgView: MutableList<ImageView>
        val runeImgView: MutableList<ImageView>
        val championImgView: ImageView
        val kdaTextView : TextView
        val killRateTextView : TextView
        val gameModeTextView : TextView
        val dateTextView : TextView
        val periodTextView : TextView
        val resultTextView : TextView
        val linearLayout : LinearLayout

        init {
            itemImgView = getItemView()
            spellImgView = getSpellView()
            runeImgView = getRuneView()
            championImgView = view.findViewById(R.id.championImgView)
            kdaTextView = view.findViewById(R.id.kda)
            killRateTextView = view.findViewById(R.id.killRate)
            gameModeTextView = view.findViewById(R.id.gameMode)
            dateTextView = view.findViewById(R.id.date)
            periodTextView = view.findViewById(R.id.period)
            resultTextView = view.findViewById(R.id.result)
            linearLayout = view.findViewById(R.id.linearLayout)
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
    }

    private val network : Network = Network(summonerDTO)
    val apiKey = "RGAPI-b02fb073-a130-4272-851f-b672dc7d8f5d"

    private val matches : MutableList<String> by lazy {
        runBlocking {
            network.requestMatchId()
        }
    }

    val itemsDTO : ItemsDTO by lazy { getItem() }

    val spellsDTO : SpellsDTO by lazy { getSpells() }

    val runeDTO : RunesForgedDTO by lazy { getRunesReforgedDTO() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder
    {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.record_item, parent, false)
        return RecordViewHolder(view)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onBindViewHolder(holder: RecordViewHolder, position: Int)
    {
        // 여기서 꾸미기
        println(itemsDTO.type)
        val matchInfoDTO = getSummaryMatchInfo(position)

        // 게임 진행 시간 set
        val timestamp  = matchInfoDTO.gameEndTimestamp.toLong() - matchInfoDTO.gameStartTimestamp.toLong()
        val periodFormat = SimpleDateFormat("mm : ss")
        holder.periodTextView.text = periodFormat.format(timestamp)

        // 챔피온 아이콘 set
        val championIcon : Bitmap
        runBlocking {
            championIcon = requestChampionIcon(matchInfoDTO)
        }
        holder.championImgView.setImageBitmap(championIcon)

        // test
        if(position == 1)
        {
            getSummaryMatchInfo(1)
        }

        // 아이템 아이콘 set
        for(i in 0 until holder.itemImgView.size)
        {
            val itemId = matchInfoDTO.participants.items[i]
            if(itemId == "0")
                continue

            val itemIcon : Bitmap
            runBlocking {
                itemIcon = getItemIcon(matchInfoDTO, i)
            }
            holder.itemImgView[i].setImageBitmap(itemIcon)
        }

        // spell 아이콘 set
        for(i in 0 until holder.spellImgView.size)
        {
            val spellKeyOfSummoner = matchInfoDTO.participants.spellId[i]
            for(spell in spellsDTO.data)
            {
                if(spell.key != spellKeyOfSummoner)
                    continue

                val spellIcon : Bitmap
                runBlocking {
                    spellIcon = getSpellIcon(spell)
                }
                holder.spellImgView[i].setImageBitmap(spellIcon)
            }
        }

        /*
        for(i in 0 until holder.runeImgView.size)
        {
            val runeIdOfSummoner = matchInfoDTO.participants.runeOfSummonerDTO.styles.styles[i].style
            println("사용자의 룬 id : $runeIdOfSummoner")
            for(rune in runeDTO.dataId)
            {
                if(rune != runeIdOfSummoner)
                    continue


                val runeIcon : Bitmap
                runBlocking {
                    runeIcon = getRuneIcon(rune)
                }
                holder.runeImgView[i].setImageBitmap(runeIcon)


            }
        }

         */

        // primaryStyle
        for(runePath in runeDTO.data)
        {
            if(runePath.id != matchInfoDTO.participants.runeOfSummonerDTO.primaryStyle.style)
                continue

            // 룬 경로가 같다면
            for(rune in runePath.slots[0].runes)
            {
                // rune 은 왕룬임
                if(rune.id != matchInfoDTO.participants.runeOfSummonerDTO.primaryStyle.runeId[0])
                    continue

                // 동일한 룬이라면
                val runeIcon : Bitmap
                runBlocking {
                    runeIcon = getRuneIcon(rune.icon)
                }
                holder.runeImgView[0].setImageBitmap(runeIcon)
                break
            }
            break
        }

        // 보조 룬
        for(runePath in runeDTO.data)
        {
            if(runePath.id != matchInfoDTO.participants.runeOfSummonerDTO.subStyle.style)
                continue

            // 룬 경로가 같다면
            val runeIcon : Bitmap
            runBlocking {
                runeIcon = getRuneIcon(runePath.icon)
            }
            holder.runeImgView[1].setImageBitmap(runeIcon)
            break
        }

        // gameMode
        val queueId = getQueueId()
        for(key in queueId.keys)
        {
            if(key != matchInfoDTO.queueId)
                continue

            val gameMode = queueId[key]
            holder.gameModeTextView.text = gameMode
        }


        // kda set
        val kill = matchInfoDTO.participants.kill
        val death = matchInfoDTO.participants.deaths
        val assist = matchInfoDTO.participants.assists
        val kda = "$kill / $death / $assist"
        holder.kdaTextView.text = kda

        // date set
        val date = matchInfoDTO.gameEndTimestamp.toLong()
        val dateFormat = SimpleDateFormat("MM월 dd일")
        holder.dateTextView.text = dateFormat.format(date)

        val result = matchInfoDTO.participants.win as Boolean
        if(result)
        {
            holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.victoryColor))
            holder.resultTextView.text = "승리"
        }
        else
        {
            holder.linearLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.defeatColor))
            holder.resultTextView.text = "패배"
        }
    }

    override fun getItemCount(): Int {
        return matches.size
    }

    suspend fun getRuneIcon(runeIcon : String) : Bitmap
    {
        val bitmap : Bitmap
        withContext(Dispatchers.IO) {
            val requestURL = "https://ddragon.canisback.com/img/$runeIcon"
            val url = URL(requestURL)
            val httpURLConnection = url.openConnection() as HttpURLConnection
            val inputStream = httpURLConnection.inputStream
            bitmap = BitmapFactory.decodeStream(inputStream)
        }

        return bitmap
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













    suspend fun getItemIcon(matchInfoDTO : SummaryMatchInfoDTO, index : Int) : Bitmap
    {
        val bitmap : Bitmap
        withContext(Dispatchers.IO) {
            val requestURL = "http://ddragon.leagueoflegends.com/cdn/13.6.1/img/item/${matchInfoDTO.participants.items[index]}.png"
            val url = URL(requestURL)
            val httpURLConnection = url.openConnection() as HttpURLConnection
            val inputStream = httpURLConnection.inputStream
            bitmap = BitmapFactory.decodeStream(inputStream)
        }

        return bitmap
    }

    suspend fun getSpellIcon(spell: SpellDTO) : Bitmap
    {
        val bitmap : Bitmap
        withContext(Dispatchers.IO) {
            val requestURL = "http://ddragon.leagueoflegends.com/cdn/13.6.1/img/spell/${spell.id}.png"
            val url = URL(requestURL)
            val httpURLConnection = url.openConnection() as HttpURLConnection
            val inputStream = httpURLConnection.inputStream
            bitmap = BitmapFactory.decodeStream(inputStream)
        }

        return bitmap
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


    suspend fun requestChampionIcon(matchInfoDTO : SummaryMatchInfoDTO) : Bitmap
    {
        val bitmap : Bitmap
        withContext(Dispatchers.IO) {
            val requestURL = "http://ddragon.leagueoflegends.com/cdn/13.6.1/img/champion/${matchInfoDTO.participants.championName}.png"
            val url = URL(requestURL)
            val httpURLConnection = url.openConnection() as HttpURLConnection
            val inputStream = httpURLConnection.inputStream
            bitmap = BitmapFactory.decodeStream(inputStream)
        }

        return bitmap
    }

    fun getItem() : ItemsDTO
    {
        val items = runBlocking { requestItemsDTO() }
        val itemsJson = JSONObject(items["data"].toString())
        val data = mutableListOf<ItemDataDTO>()

        for(key in itemsJson.keys())
        {
            val itemJson = JSONObject(itemsJson[key].toString())
            val name = itemJson["name"].toString()
            val description = itemJson["description"].toString()
            val plaintext = itemJson["plaintext"].toString()

            val imageJson = JSONObject(itemJson["image"].toString())
            val image = imageJson["full"].toString()

            val goldJson = JSONObject(itemJson["gold"].toString())
            val base = goldJson["base"].toString()
            val total = goldJson["total"].toString()
            val itemGoldDTO = ItemGoldDTO(base, total)

            val itemDTO = ItemDTO(name, description, plaintext, image, itemGoldDTO)
            val itemDataDTO = ItemDataDTO(key, itemDTO)
            data.add(itemDataDTO)
        }

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

    fun getSummaryMatchInfo(index : Int) : SummaryMatchInfoDTO
    {
        val matchData = runBlocking { requestSummaryMatchesInfo(matches[index]) }
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
        }

        val gameCreation = infodata["gameCreation"].toString()
        val gameStartTimeStamp = infodata["gameStartTimestamp"].toString()
        val gameEndTimeStamp = infodata["gameEndTimestamp"].toString()
        val queueId = infodata["queueId"].toString()
        val gameType = infodata["gameType"].toString()
        val participantsData = JSONArray(infodata["participants"].toString())

        val p = JSONObject(participantsData[count].toString())
        println(p)

        val assists = p["assists"].toString()
        val championId = p["championId"].toString()
        val championName = p["championName"].toString()
        val deaths = p["deaths"].toString()

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
            deaths, items, kill,runeOfSummonerDTO, spellId, win)
        val summaryMatchInfoDTO = SummaryMatchInfoDTO(gameCreation, gameStartTimeStamp, gameEndTimeStamp,
            queueId, gameType, summaryParticipantDTO)

        return summaryMatchInfoDTO
    }

    suspend fun requestSummaryMatchesInfo(matchId : String) : JSONObject
    {
        val matchData : JSONObject
        withContext(Dispatchers.IO) {
            val requestURL = "https://asia.api.riotgames.com/lol/match/v5/matches/${matchId}?api_key=$apiKey"
            val url = URL(requestURL)
            val httpURLConnection = url.openConnection() as HttpURLConnection
            val inputStream = httpURLConnection.inputStream
            val scan = Scanner(inputStream)
            matchData = JSONObject(scan.nextLine())
        }

        return matchData
    }
}


data class ItemsDTO(val type : String, val version : String, val data : List<ItemDataDTO>)

data class ItemDataDTO(val itemId : String, val itemDTO : ItemDTO)

data class ItemDTO(val name : String, val description : String, val plaintext : String,
                   val image : String, val gold : ItemGoldDTO)

data class ItemGoldDTO(val base : String, val total : String)

