package com.example.lolgg

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.lolgg.network.Network
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class RecordOfSummonerAdapter(private val summonerDTO : SummonerDTO) : RecyclerView.Adapter<RecordOfSummonerAdapter.RecordViewHolder>() {
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

        init {
            itemImgView = getItemView()
            spellImgView = getSpellView()
            runeImgView = getRuneView()
            championImgView = view.findViewById(R.id.championImgView)
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

    private val matches : MutableList<String> by lazy {
        runBlocking {
            network.requestMatchId()
        }
    }

    val itemsDTO : ItemsDTO by lazy {
        runBlocking {
            getItem()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder
    {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.record_item, parent, false)
        return RecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int)
    {
        // 여기서 꾸미기
        println(itemsDTO.type)
        val summaryMatchInfoDTO = getSummaryMatchInfo(position)
        if(position == 1)
        {
            getSummaryMatchInfo(1)
        }
        for(i in 0 until holder.itemImgView.size)
        {
            //holder.itemImgView[i].setImageBitmap()
        }
    }

    suspend fun getItem() : ItemsDTO
    {
        val itemsDTO : ItemsDTO

        withContext(Dispatchers.IO) {
            val requestURL = "https://ddragon.leagueoflegends.com/cdn/13.6.1/data/ko_KR/item.json"
            val url = URL(requestURL)
            val httpURLConnection = url.openConnection() as HttpURLConnection
            val inputStream = httpURLConnection.inputStream
            val scan = Scanner(inputStream)
            val items = JSONObject(scan.nextLine())

            val response = JSONObject(items["data"].toString())
            val dataList = mutableListOf<ItemDataDTO>()

            for(key in response.keys())
            {
                val itemJson = JSONObject(response[key].toString())
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
                dataList.add(itemDataDTO)
            }

            itemsDTO = ItemsDTO(items["type"].toString(), items["version"].toString(), dataList)
        }

        return itemsDTO
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
        val gameMode = infodata["gameMode"].toString()
        val gameType = infodata["gameType"].toString()
        val participantsData = JSONArray(infodata["participants"].toString())

        val p = JSONObject(participantsData[count].toString())
        println(p)

        val assists = p["assists"].toString()
        val championId = p["championId"].toString()
        val championName = p["championName"].toString()
        val deaths = p["deaths"].toString()
        val items = mutableListOf<String>()
        for(i in 0..6)
        {
            val item = p["item$i"].toString()
            items.add(item)
        }
        val win = p["win"] as Boolean
        val kill = p["kills"].toString()

        val summaryParticipantDTO = SummaryParticipantDTO(assists, championId, championName, deaths, items, kill, win)
        val summaryMatchInfoDTO = SummaryMatchInfoDTO(gameCreation, gameStartTimeStamp, gameEndTimeStamp,
            gameMode, gameType, summaryParticipantDTO)

        return summaryMatchInfoDTO
    }

    suspend fun requestSummaryMatchesInfo(matchId : String) : JSONObject
    {
        val matchData : JSONObject
        withContext(Dispatchers.IO) {
            val apiKey = "RGAPI-0cc3c992-cff7-4f50-8095-5cc91ed2aa29"
            val requestURL = "https://asia.api.riotgames.com/lol/match/v5/matches/${matchId}?api_key=$apiKey"
            val url = URL(requestURL)
            val httpURLConnection = url.openConnection() as HttpURLConnection
            val inputStream = httpURLConnection.inputStream
            val scan = Scanner(inputStream)
            matchData = JSONObject(scan.nextLine())
        }

        return matchData
    }

    override fun getItemCount(): Int {
        return matches.size
    }
}


data class ItemsDTO(val type : String, val version : String, val data : List<ItemDataDTO>)

data class ItemDataDTO(val itemIdList : String, val itemDTO : ItemDTO)

data class ItemDTO(val name : String, val description : String, val plaintext : String,
                   val image : String, val gold : ItemGoldDTO)

data class ItemGoldDTO(val base : String, val total : String)

