package com.example.lolgg.network

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.EditText
import com.beust.klaxon.JsonArray
import com.bumptech.glide.Glide
import com.example.lolgg.*
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.FileNotFoundException
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.*

class Network {

    private val apiKey = "RGAPI-6227b0ea-0a53-4a6c-ba51-aa3d9805a2d7"

    val version = runBlocking { "http://ddragon.leagueoflegends.com/cdn/${requestVersion()}/" }
    var summonerDTO : SummonerDTO?

    constructor(summonerDTO: SummonerDTO)
    {
        this.summonerDTO = summonerDTO
    }

    constructor(edit : EditText)
    {
        runBlocking {
            summonerDTO = getSummonerDTO(edit)
        }
    }

    private suspend fun requestVersion() : String
    {
        var version = ""

        withContext(Dispatchers.IO) {
            val requestURL = "https://ddragon.leagueoflegends.com/api/versions.json"
            val url = URL(requestURL)
            val httpURLConnection = url.openConnection() as HttpURLConnection

            val scan : Scanner
            try{
                val inputStream = httpURLConnection.inputStream
                scan = Scanner(inputStream)

                if(scan.hasNextLine())
                {
                    val sampleList = mutableListOf<String>()
                    version = Gson().fromJson(scan.nextLine(), sampleList::class.java)[0]
                }
            } catch (e : Exception) {
                println("버전 가져오다가 죽음")
                return@withContext
            }
        }

        return version
    }

    private suspend fun getSummonerDTO(edit : EditText) : SummonerDTO?
    {
        val summonerData : JSONObject = requestSummoner(edit) ?: return null
        val summonerDTO : SummonerDTO
        withContext(Dispatchers.Default) {
            val id = summonerData["id"].toString()
            val accountId = summonerData["accountId"].toString()
            val puuid = summonerData["puuid"].toString()
            val name = summonerData["name"].toString()
            val profileIcon = summonerData["profileIconId"] as Int
            val revisionData = summonerData["revisionDate"] as Long
            val summonerLevel = summonerData["summonerLevel"] as Int
            val ranks = getLeagueEntry(id)
            //홍성희
            summonerDTO = SummonerDTO(accountId,profileIcon,revisionData,name,id,puuid,summonerLevel, ranks[0], ranks[1])
        }

        return summonerDTO
    }

    // mainActivity 이후로 다 쓰임
    private suspend fun requestSummoner(edit : EditText) : JSONObject?
    {
        var summonerData : JSONObject? = null

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
                    summonerData = JSONObject(scan.nextLine())
                }
                return@withContext summonerDTO
            } catch (e : Exception) {
                println("죽음")
                return@withContext null
            }

        }

        return summonerData
    }

    private suspend fun requestLeagueEntry(encryptedSummonerId : String) : String?
    {
        var leagueData : String? = null
        withContext(Dispatchers.IO) {
            val requestURL = "https://kr.api.riotgames.com/lol/league/v4/entries/by-summoner/$encryptedSummonerId?api_key=$apiKey"
            val url = URL(requestURL)
            val httpURLConnection = url.openConnection() as HttpURLConnection

            try{
                val inputStream = httpURLConnection.inputStream
                val scan = Scanner(inputStream)

                if(!scan.hasNext())
                    return@withContext null

                leagueData = scan.nextLine()

            } catch (e : Exception) {
                println("rank 가져오다가 죽음")
            }
        }

        return leagueData
    }

    private suspend fun getLeagueEntry(encryptedSummonerId: String) : Array<RankDTO?>
    {
        val ranks : Array<RankDTO?> = arrayOf(null, null)
        val leagueString = requestLeagueEntry(encryptedSummonerId) ?: return ranks

        val ranksData = JSONArray(leagueString)

        for(i in 0 until ranksData.length())
        {
            val rankData = JSONObject(ranksData[i].toString())
            val queueType = rankData["queueType"].toString()
            val tier = rankData["tier"].toString()
            val rank = rankData["rank"].toString()
            val leaguePoints = rankData["leaguePoints"].toString()
            val wins = rankData["wins"].toString()
            val losses = rankData["losses"].toString()

            val rankDTO = RankDTO(tier, rank, leaguePoints, wins, losses)
            when(queueType)
            {
                "RANKED_FLEX_SR" ->  ranks[1] = rankDTO
                "RANKED_SOLO_5x5" ->  ranks[0] = rankDTO
            }
        }

        return ranks
    }

    suspend fun getProFileIcon() : Bitmap?
    {
        var bitmap : Bitmap? = null

        withContext(Dispatchers.IO) {
            val requestURL = "${version}img/profileicon/${summonerDTO?.profileIconId}.png"
            val url = URL(requestURL)
            val httpURLConnection = url.openConnection() as HttpURLConnection

            try{
                val inputStream = httpURLConnection.inputStream

                bitmap = BitmapFactory.decodeStream(inputStream)
            } catch (e : Exception) {
                println("profileIcon 가져오다가 죽음")
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
            val requestURL = "https://kr.api.riotgames.com/lol/champion-mastery/v4/champion-masteries/by-summoner/${summonerDTO?.id}/top?count=1&api_key=$apiKey"
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
            val requestURL = "${version}data/ko_KR/champion.json"
            val url = URL(requestURL)
            val httpURLConnection = url.openConnection() as HttpURLConnection
            val inputStream = httpURLConnection.inputStream
            val scan = Scanner(inputStream)
            championsData = JSONObject(scan.nextLine())
        }

        return championsData
    }

    suspend fun requestMatchId(start : Int, count : Int) : MutableList<String>
    {
        val matches = mutableListOf<String>()
        withContext(Dispatchers.IO) {
            val requestURL  = "https://asia.api.riotgames.com/lol/match/v5/matches/by-puuid/${summonerDTO?.puuid}/ids?start=$start&count=$count&api_key=$apiKey"
            val url = URL(requestURL)
            val httpURLConnection = url.openConnection() as HttpURLConnection
            val inputStream = httpURLConnection.inputStream
            val scan = Scanner(inputStream)
            val s = scan.nextLine()
            val tokenizer = StringTokenizer(s.substring(1,s.length-1))

            while (tokenizer.hasMoreTokens())
            {
                val match = tokenizer.nextToken("\",")

                if(match == "")
                    return@withContext null

                matches.add(match)
            }
        }

        return matches
    }

    suspend fun requestMatch(matchId : String) : JSONObject?
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
}