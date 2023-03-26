package com.example.lolgg

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class RecordOfSummonerActivity : AppCompatActivity() {

    val summonerInfo by lazy {
        intent.getSerializableExtra("key") as SummonerDTO
    }

    val apiKey = "RGAPI-6f74d5dd-c566-4708-89c3-ed55590841f4"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summoner_of_record)
        val summonerId = findViewById<TextView>(R.id.summonerId)
        summonerId.text = summonerInfo.name
        val profileIconView = findViewById<ImageView>(R.id.profileIcon)
        val mostChampionView = findViewById<ImageView>(R.id.mostChampionImgView)



        var profileIconBitmap : Bitmap
        var mostChampionViewBitmap : Bitmap
        val championsDTO = getChampionsDTO()
        runBlocking {
            profileIconBitmap = getProFileIcon()!!
            mostChampionViewBitmap = getMostChampionView()!!
        }
        profileIconView.setImageBitmap(profileIconBitmap)
        mostChampionView.setImageBitmap(mostChampionViewBitmap)
       // println(s)
    }

    private suspend fun getProFileIcon() : Bitmap?
    {
        var bitmap : Bitmap? = null
        withContext(Dispatchers.IO) {
            val requestURL = "http://ddragon.leagueoflegends.com/cdn/13.6.1/img/profileicon/${summonerInfo.profileIconId}.png"
            val url = URL(requestURL)
            val httpURLConnection = url.openConnection() as HttpURLConnection

            try{
                val inputStream = httpURLConnection.inputStream
                bitmap = BitmapFactory.decodeStream(inputStream)
            } catch (e : Exception) {
                println("죽음")
            }

        }
        return bitmap
    }

    private suspend fun getMostChampionView() : Bitmap?
    {
        // 여기서 소환사의 mostChampion을 가져와 jpg를 요청해야함.
        var bitmap : Bitmap? = null
        withContext(Dispatchers.IO) {
            val key = requestMostChampionKey()
            println(key)
            val data = getChampionsDTO().data
            var championId = ""
            for(champion in data)
            {
                if(champion.key != key)
                    continue

                championId = champion.id
            }
            val requestURL = "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/${championId}_0.jpg"
            val url = URL(requestURL)
            val httpURLConnection = url.openConnection() as HttpURLConnection

            try{
                val inputStream = httpURLConnection.inputStream
                bitmap = BitmapFactory.decodeStream(inputStream)
            } catch (e : Exception) {
                println("죽음")
            }
        }
        return bitmap
    }

    private suspend fun requestMostChampionKey() : String
    {
        var key : String
        withContext(Dispatchers.IO) {
            val requestURL = "https://kr.api.riotgames.com/lol/champion-mastery/v4/champion-masteries/by-summoner/${summonerInfo.id}/top?count=1&api_key=$apiKey"
            val url = URL(requestURL)
            val httpURLConnection = url.openConnection() as HttpURLConnection
            val inputStream = httpURLConnection.inputStream
            val scan = Scanner(inputStream)
            val JSONObject = JSONObject(JSONArray(scan.nextLine())[0].toString())
            key = JSONObject["championId"].toString()
        }

        return key
    }





    // champion data 를 가져오는 메소드

    private suspend fun requestChampionData() : JSONObject
    {
        var championsData : JSONObject

        withContext(Dispatchers.IO) {
            val requestURL = "https://ddragon.leagueoflegends.com/cdn/13.6.1/data/ko_KR/champion.json"
            val url = URL(requestURL)
            val httpURLConnection = url.openConnection() as HttpURLConnection
            val inputStream = httpURLConnection.inputStream
            val scan = Scanner(inputStream)
            championsData = JSONObject(scan.nextLine())
        }

        return championsData
    }

    private fun getChampionsDTO() : ChampionsDTO
    {
        val championsData : JSONObject

        runBlocking {
             championsData = requestChampionData()
        }

        val type = championsData["type"].toString()
        val format = championsData["version"].toString()
        val version = championsData["version"].toString()
        val data = JSONObject(championsData["data"].toString())
        val championsDTOList = getChampionsDTOList(data)

        return ChampionsDTO(type, format, version, championsDTOList)
    }

    private fun getChampionsDTOList(data : JSONObject) : MutableList<ChampionDTO>
    {
        val championList = mutableListOf<ChampionDTO>()
        for(dataKey in data.keys())
        {
            val champion = JSONObject(data[dataKey].toString())
            val championDTO = getChampionDTO(champion)
            championList.add(championDTO)
        }

        return championList
    }

    private fun getChampionDTO(champion : JSONObject) : ChampionDTO
    {
        val version = champion["version"].toString()
        val id = champion["id"].toString()
        val key = champion["key"].toString()
        val name = champion["name"].toString()
        val title = champion["title"].toString()
        val blurb = champion["blurb"].toString()
        val infoDTO = getInfoDTO(champion)
        val imageDTO = getImageDTO(champion)
        val tags = getTags(champion)
        val partype = champion["partype"].toString()
        val statsDTO = getStatsDTO(champion)

        return ChampionDTO(version, id, key, name, title, blurb, infoDTO, imageDTO, tags, partype, statsDTO)
    }

    private fun getTags(champion : JSONObject) : MutableList<String>
    {
        val tags = mutableListOf<String>()
        val tokenizer = StringTokenizer(champion["tags"].toString())

        while(tokenizer.hasMoreTokens())
        {
            tags.add(tokenizer.nextToken("[\",]"))
        }

        return tags
    }

    private fun getStatsDTO(champion: JSONObject) : StatsDTO
    {
        val stats = JSONObject(champion["stats"].toString())
        return StatsDTO(stats["hp"].toString(), stats["hpperlevel"].toString(), stats["mp"].toString(), stats["mpperlevel"].toString(),
            stats["movespeed"].toString(), stats["armor"].toString(), stats["armorperlevel"].toString(), stats["spellblock"].toString(),
            stats["spellblockperlevel"].toString(), stats["attackrange"].toString(), stats["hpregen"].toString(), stats["hpregenperlevel"].toString(),
            stats["mpregen"].toString(), stats["mpregenperlevel"].toString(), stats["crit"].toString(), stats["critperlevel"].toString(),
            stats["attackdamage"].toString(), stats["attackdamageperlevel"].toString(), stats["attackspeedperlevel"].toString(), stats["attackspeed"].toString())
    }

    private fun getInfoDTO(champion: JSONObject) : InfoDTO
    {
        val info = JSONObject(champion["info"].toString())

        return InfoDTO(info["attack"] as Int, info["defense"] as Int, info["magic"] as Int, info["difficulty"] as Int)
    }

    private fun getImageDTO(champion: JSONObject) : ImageDTO
    {
        val image = JSONObject(champion["image"].toString())

        return ImageDTO(image["full"].toString(), image["sprite"].toString(), image["group"].toString(),
            image["x"] as Int, image["y"] as Int, image["w"] as Int, image["h"] as Int)
    }

    /*
    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)


        return super.onCreateView(name, context, attrs)
    }

     */
}

data class ChampionsDTO(val type : String, val format : String, val version: String, val data : List<ChampionDTO>)

data class ChampionDTO(val version : String, val id : String, val key : String, val name : String, val title : String, val blurb : String, val info : InfoDTO, val image : ImageDTO,
                       val tags : List<String>, val partype : String, val stats : StatsDTO)

data class InfoDTO(val attack : Int, val defense : Int, val magic : Int, val difficulty : Int)

data class ImageDTO(val full : String, val sprite : String, val group : String, val x : Int, val y : Int, val w : Int, val h : Int)

/*
data class StatsDTO(val hp : Int, val hpperlevel: Int, val mp : Int, val mpperlevel : Int, val movespeed : Int, val armor : Int, val armorperlevel : Double,
                    val spellblock : Int, val spellblockperlevel : Double, val attackrange : Int, val hpregen : Double, val hpregenperlevel : Double,
                    val mpregen : Double, val mpregenperlevel : Double, val crit : Int, val critperlevel : Int, val attackdamage : Int, val attackdamageperlevel : Double,
                    val attackspeedperlevel : Double, val attackspeed : Double)

 */
data class StatsDTO(val hp : String, val hpperlevel: String, val mp : String, val mpperlevel : String, val movespeed : String, val armor : String, val armorperlevel : String,
                    val spellblock : String, val spellblockperlevel : String, val attackrange : String, val hpregen : String, val hpregenperlevel : String,
                    val mpregen : String, val mpregenperlevel : String, val crit : String, val critperlevel : String, val attackdamage : String, val attackdamageperlevel : String,
                    val attackspeedperlevel : String, val attackspeed : String)
