package com.example.lolgg

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.example.lolgg.network.Network
import kotlinx.coroutines.*
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val edit = findViewById<EditText>(R.id.summonerEdit)
        var isLoading = false

        edit.setOnKeyListener { view, i, keyEvent ->
            println("keyEvent : $i")
            if (keyEvent.action != KeyEvent.ACTION_UP
                && i == KeyEvent.KEYCODE_ENTER && !isLoading)
            {
                Log.d("gd","소환사 검색 클릭")
                isLoading = true
                val network = Network(edit)
                var summonerDTO : SummonerDTO?

                runBlocking {
                    summonerDTO = network.summonerDTO
                }

                if(summonerDTO != null)
                {
                    Log.d("Log","success")
                    val intent = Intent(this,RecordOfSummonerActivity::class.java)
                    intent.putExtra("getSummoner",summonerDTO)
                    startActivity(intent)
                }
                else
                {
                    Toast.makeText(applicationContext, "존재하지 않는 소환사입니다.", Toast.LENGTH_LONG).show()
                }
                isLoading = false
            }
            true
        }


    }

    /*
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
            val png : Bitmap
            runBlocking {
                png = getItemIcon(image)
            }


            val goldJson = JSONObject(itemJson["gold"].toString())
            val base = goldJson["base"].toString()
            val total = goldJson["total"].toString()
            val itemGoldDTO = ItemGoldDTO(base, total)

            val itemDTO = ItemDTO(name, description, plaintext, png, itemGoldDTO)
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

    suspend fun getItemIcon(itemId : String) : Bitmap
    {
        val bitmap : Bitmap
        withContext(Dispatchers.IO) {
            val requestURL = "http://ddragon.leagueoflegends.com/cdn/13.6.1/img/item/${itemId}"
            val url = URL(requestURL)
            val httpURLConnection = url.openConnection() as HttpURLConnection
            val inputStream = httpURLConnection.inputStream
            bitmap = BitmapFactory.decodeStream(inputStream)
        }

        return bitmap
    }

     */
}