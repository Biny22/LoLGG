package com.example.lolgg.network

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.EditText
import com.example.lolgg.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.*

class Network {

    private val apiKey = "RGAPI-0cc3c992-cff7-4f50-8095-5cc91ed2aa29"
    var summonerDTO : SummonerDTO


    constructor(summonerDTO: SummonerDTO)
    {
        this.summonerDTO = summonerDTO
    }

    constructor(edit : EditText)
    {
        runBlocking {
            summonerDTO = requestSummoner(edit)!!
        }
    }

    // mainActivity 이후로 다 쓰임
    private suspend fun requestSummoner(edit : EditText) : SummonerDTO?
    {
        var summonerDTO : SummonerDTO? = null
        withContext(Dispatchers.IO) {
            val summoner = URLEncoder.encode(edit.text.toString(), StandardCharsets.UTF_8.toString())
            val requestURL = "https://kr.api.riotgames.com/lol/summoner/v4/summoners/by-name/$summoner?api_key=$apiKey"
            val url = URL(requestURL)
            val httpURLConnection = url.openConnection() as HttpURLConnection

            val scan : Scanner
            try{
                val inputStream = httpURLConnection.inputStream
                scan = Scanner(inputStream)

                if(scan.hasNext())
                {
                    val summonerData = JSONObject(scan.nextLine())

                    val id = summonerData["id"].toString()
                    val accountId = summonerData["accountId"].toString()
                    val puuid = summonerData["puuid"].toString()
                    val name = summonerData["name"].toString()
                    val profileIcon = summonerData["profileIconId"] as Int
                    val revisionData = summonerData["revisionDate"] as Long
                    val summonerLevel = summonerData["summonerLevel"] as Int

                    summonerDTO = SummonerDTO(accountId,profileIcon,revisionData,name,id,puuid,summonerLevel)
                }
                return@withContext summonerDTO
            } catch (e : Exception) {
                println("죽음")
                return@withContext null
            }

        }

        return summonerDTO
    }

    suspend fun getProFileIcon() : Bitmap?
    {
        var bitmap : Bitmap? = null
        println(summonerDTO?.profileIconId)
        withContext(Dispatchers.IO) {
            val requestURL = "http://ddragon.leagueoflegends.com/cdn/13.6.1/img/profileicon/${summonerDTO.profileIconId}.png"
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

    suspend fun getMostChampionView() : Bitmap?
    {
        // 여기서 소환사의 mostChampion을 가져와 jpg를 요청해야함.
        var bitmap : Bitmap? = null
        withContext(Dispatchers.IO) {
            val key = requestMostChampionKey()
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

    fun getChampionsDTO() : ChampionsDTO
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

    private fun getStatsDTO(champion: JSONObject) : ChampionStatsDTO
    {
        val stats = JSONObject(champion["stats"].toString())
        return ChampionStatsDTO(stats["hp"].toString(), stats["hpperlevel"].toString(), stats["mp"].toString(), stats["mpperlevel"].toString(),
            stats["movespeed"].toString(), stats["armor"].toString(), stats["armorperlevel"].toString(), stats["spellblock"].toString(),
            stats["spellblockperlevel"].toString(), stats["attackrange"].toString(), stats["hpregen"].toString(), stats["hpregenperlevel"].toString(),
            stats["mpregen"].toString(), stats["mpregenperlevel"].toString(), stats["crit"].toString(), stats["critperlevel"].toString(),
            stats["attackdamage"].toString(), stats["attackdamageperlevel"].toString(), stats["attackspeedperlevel"].toString(), stats["attackspeed"].toString())
    }

    private fun getInfoDTO(champion: JSONObject) : ChampionInfoDTO
    {
        val info = JSONObject(champion["info"].toString())

        return ChampionInfoDTO(info["attack"] as Int, info["defense"] as Int, info["magic"] as Int, info["difficulty"] as Int)
    }

    private fun getImageDTO(champion: JSONObject) : ChampionImageDTO
    {
        val image = JSONObject(champion["image"].toString())

        return ChampionImageDTO(image["full"].toString(), image["sprite"].toString(), image["group"].toString(),
            image["x"] as Int, image["y"] as Int, image["w"] as Int, image["h"] as Int)
    }

    private suspend fun requestMostChampionKey() : String
    {
        var key : String
        withContext(Dispatchers.IO) {
            val requestURL = "https://kr.api.riotgames.com/lol/champion-mastery/v4/champion-masteries/by-summoner/${summonerDTO.id}/top?count=1&api_key=$apiKey"
            val url = URL(requestURL)
            val httpURLConnection = url.openConnection() as HttpURLConnection
            val inputStream = httpURLConnection.inputStream
            val scan = Scanner(inputStream)
            val JSONObject = JSONObject(JSONArray(scan.nextLine())[0].toString())
            key = JSONObject["championId"].toString()
        }

        return key
    }

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

    suspend fun requestMatchId() : MutableList<String>
    {
        val matches = mutableListOf<String>()
        withContext(Dispatchers.IO) {
            val requestURL  = "https://asia.api.riotgames.com/lol/match/v5/matches/by-puuid/${summonerDTO.puuid}/ids?start=0&count=20&api_key=$apiKey"

            val url = URL(requestURL)
            val httpURLConnection = url.openConnection() as HttpURLConnection
            val inputStream = httpURLConnection.inputStream
            val scan = Scanner(inputStream)
            val s = scan.nextLine()
            val tokenizer = StringTokenizer(s)

            while (tokenizer.hasMoreTokens())
            {
                val match = tokenizer.nextToken("[\",]")
                matches.add(match)
            }
        }

        return matches
    }
}