package com.example.lolgg.dto

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class Runes {

    data class RunesForgedDTO(val data : List<RuneDataDTO>) : Serializable

    data class RuneDataDTO(val id : String, val key : String, val icon : String, val name : String, val slots : List<SlotDTO>) : Serializable

    data class SlotDTO(val runes : List<RuneDTO>) : Serializable

    data class RuneDTO(val id : String, val key : String, val icon : String, val name : String, val sortDesc : String, val longDesc : String) : Serializable


    fun getRunesReforgedDTO() : RunesForgedDTO
    {
        // [ 지배, 영감, 결의, 마법... ] -> runesReforgedJson
        val runesReforgedJson = runBlocking { requestRunesReforgedDTO() }

        return getRuneDataDTO(runesReforgedJson)
    }

    private suspend fun requestRunesReforgedDTO() : JSONArray
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

    private fun getRuneDataDTO(runesReforgedJson : JSONArray) : RunesForgedDTO
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

    private fun getSlots(slotsArray : JSONArray) : List<SlotDTO>
    {
        val slots = mutableListOf<SlotDTO>()

        for(i in 0 until slotsArray.length())
        {
            val runePaths = JSONObject(slotsArray[i].toString())
            val array = JSONArray(runePaths["runes"].toString())

            slots.add(getSlotDTO(array))
        }
        // slots 하나가 지배룬 전체, 영감룬 전체

        return slots
    }

    private fun getSlotDTO(slots : JSONArray) : SlotDTO
    {
        val runes = mutableListOf<RuneDTO>()
        for(i in 0 until slots.length())
        {
            val rune = JSONObject(slots[i].toString())
            runes.add(getRuneDTO(rune))
        }

        return SlotDTO(runes)
    }

    private fun getRuneDTO(rune: JSONObject): RuneDTO {
        val gson = Gson()
        return gson.fromJson(rune.toString(), RuneDTO::class.java)
    }
}