package com.example.lolgg

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.EditText
import android.widget.Toast
import kotlinx.coroutines.*
import org.json.JSONObject
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.nio.charset.StandardCharsets.UTF_8
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val edit = findViewById<EditText>(R.id.summonerEdit)

        edit.setOnKeyListener { view, i, keyEvent ->
            println("keyEvent : $i")
            if (keyEvent.action != KeyEvent.ACTION_UP && i == KeyEvent.KEYCODE_ENTER)
            {
                var summonerInfo : SummonerDTO?

                runBlocking {
                    summonerInfo = request(edit)
                }

                if(summonerInfo != null)
                {
                    Log.d("Log","success")
                    val intent = Intent(this,RecordOfSummonerActivity::class.java)
                    intent.putExtra("key",summonerInfo)
                    startActivity(intent)
                }
                else
                {
                    Toast.makeText(applicationContext, "존재하지 않는 소환사입니다.", Toast.LENGTH_LONG).show()
                }

            }
            true
        }
    }

    private suspend fun request(edit : EditText) : SummonerDTO?
    {
        var summonerInfo : SummonerDTO? = null

        withContext(Dispatchers.IO) {
            val apiKey = "RGAPI-59ff4281-f144-4b29-ab58-5821b173ccf6"
            val summoner = URLEncoder.encode(edit.text.toString(), UTF_8.toString())
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

                    summonerInfo = SummonerDTO(accountId,profileIcon,revisionData,name,id,puuid,summonerLevel)
                }
                else
                    println("없음")
            } catch (e : Exception) {
                println("죽음")
                return@withContext null
            }

        }

        return summonerInfo
    }
}