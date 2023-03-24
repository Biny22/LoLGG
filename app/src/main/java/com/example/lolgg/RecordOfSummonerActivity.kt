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
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class RecordOfSummonerActivity : AppCompatActivity() {

    val summonerInfo by lazy {
        intent.getSerializableExtra("key") as SummonerDTO
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summoner_of_record)

        val summonerId = findViewById<TextView>(R.id.summonerId)
        summonerId.text = summonerInfo.name
        val profileIconView = findViewById<ImageView>(R.id.profileIcon)
        val mostChampionView = findViewById<ImageView>(R.id.mostChampionImgView)



        var profileIconBitmap : Bitmap
        var mostChampionViewBitmap : Bitmap
        runBlocking {
            profileIconBitmap = getProFileIcon()!!
            mostChampionViewBitmap = getMostChampionView()!!
            getChampions()
        }

        profileIconView.setImageBitmap(profileIconBitmap)
        mostChampionView.setImageBitmap(mostChampionViewBitmap)
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
        var bitmap : Bitmap? = null
        withContext(Dispatchers.IO) {
            val apiKey = "RGAPI-40948133-70ea-4a2d-b8e5-716446986a7e"
            val requestURL = "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/Viego_0.jpg"
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

    private suspend fun getChampions()
    {
        val requestURL = "https://ddragon.leagueoflegends.com/cdn/13.6.1/data/ko_KR/champion.json"
        val url = URL(requestURL)
        val httpURLConnection = url.openConnection() as HttpURLConnection

        try {
            val inputStream = httpURLConnection.inputStream
            inputStream.
            println("하잉~~")
            val scan = Scanner(inputStream)

            while(scan.hasNext())
            {
                println(scan.nextLine())
            }
        } catch (e : Exception)
        {
            println("죽음")
        }
    }


    /*
    private suspend fun getMostChampion() : String
    {
        var bitmap : Bitmap? = null
        withContext(Dispatchers.IO) {
            val apiKey = "RGAPI-40948133-70ea-4a2d-b8e5-716446986a7e"
            val requestURL = "https://ddragon.leagueoflegends.com/cdn/img/champion/splash/Viego_0.jpg"
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

     */



    /*
    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)


        return super.onCreateView(name, context, attrs)
    }

     */
}

data class ChampionsDTO(val type : String, val format : String, val version: String, val data : List<Champion>)

data class Champion(val version : String, val key : String, val id : String, val title : String, val blurb : String, val info : InfoDTO, val image : ImageDTO,
                    val tags : List<String>, val partype : String, val stats : StatsDTO)

data class InfoDTO(val attack : Int, val defense : Int, val magic : Int, val difficulty : Int)

data class ImageDTO(val full : String, val sprite : String, val group : String, val x : Int, val y : Int, val w : Int, val h : Int)

data class StatsDTO(val hp : Int, val hpperlevel: Int, val mp : Int, val mpperlevel : Int, val movespeed : Int, val armor : Int, val armorperlevel : Float,
                    val spellblock : Int, val spellblockperlevel : Float, val attackrange : Int, val hpregen : Float, val hpregenperlevel : Float,
                    val mpregen : Float, val mpregenperlevel : Float, val crit : Int, val critperlevel : Int, val attackdamage : Int, val attackdamageperlevel : Float,
                    val attackspeedperlevel : Float, val attackspeed : Float)

