package com.example.lolgg

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class Spells {
    data class SpellsDTO(val type : String, val version : String, val data : List<SpellDTO>)

    data class SpellDTO(val id : String, val name : String, val description : String, val key : String, val image : String)

    suspend fun getSpells(): SpellsDTO = parsingSpellsDTO()

    private suspend fun parsingSpellsDTO() : SpellsDTO
    {
        val type : String
        val version : String
        val data : MutableList<SpellDTO>
        withContext(Dispatchers.Default) {
            val spellsJson = requestSpellsDTO()
            type = spellsJson["type"].toString()
            version = spellsJson["version"].toString()

            val spellData = JSONObject(spellsJson["data"].toString())
            data = getSpellDTO(spellData)
        }

        return SpellsDTO(type, version, data)
    }

    private suspend fun requestSpellsDTO() : JSONObject
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

    private fun getSpellDTO(spellData : JSONObject) : MutableList<SpellDTO>
    {
        val data = mutableListOf<SpellDTO>()
        val gson = Gson()
        val ex = SpellDTO("","","","","")

        for (spellName in spellData.keys()) {
            val spell = JSONObject(spellData[spellName].toString())

            val id = spell["id"].toString()
            val name = spell["name"].toString()
            val description = spell["description"].toString()
            val key = spell["key"].toString()
            val image = spell["image"].toString()

            data.add(SpellDTO(id, name, description, key, image))
        }

        return data
    }
}

