package com.example.lolgg

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.lolgg.network.Network
import kotlinx.coroutines.runBlocking

class RecordOfSummonerActivity : AppCompatActivity() {

    private val summonerDTO by lazy {
        intent.getSerializableExtra("key") as SummonerDTO
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summoner_of_record)
        val summonerId = findViewById<TextView>(R.id.summonerId)
        summonerId.text = summonerDTO.name
        val profileIconView = findViewById<ImageView>(R.id.profileIcon)
        val mostChampionView = findViewById<ImageView>(R.id.mostChampionImgView)

        val network = Network(summonerDTO)
        var profileIconBitmap : Bitmap
        var mostChampionViewBitmap : Bitmap
        val championsDTO = network.getChampionsDTO()

        val recyclerView = findViewById<RecyclerView>(R.id.recordOfSummoner)
        val recyclerViewAdapter = RecordOfSummonerAdapter(summonerDTO)
        recyclerView.adapter = recyclerViewAdapter

        runBlocking {
            profileIconBitmap = network.getProFileIcon()!!
            mostChampionViewBitmap = network.getMostChampionView()!!
        }
        profileIconView.setImageBitmap(profileIconBitmap)
        mostChampionView.setImageBitmap(mostChampionViewBitmap)
       // println(s)
    }

    // champion data 를 가져오는 메소

    /*
    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)


        return super.onCreateView(name, context, attrs)
    }

     */
}

data class ChampionsDTO(val type : String, val format : String, val version: String, val data : List<ChampionDTO>)

data class ChampionDTO(val version : String, val id : String, val key : String, val name : String, val title : String, val blurb : String, val info : ChampionInfoDTO, val image : ChampionImageDTO,
                       val tags : List<String>, val partype : String, val stats : ChampionStatsDTO)

data class ChampionInfoDTO(val attack : Int, val defense : Int, val magic : Int, val difficulty : Int)

data class ChampionImageDTO(val full : String, val sprite : String, val group : String, val x : Int, val y : Int, val w : Int, val h : Int)

/*
data class StatsDTO(val hp : Int, val hpperlevel: Int, val mp : Int, val mpperlevel : Int, val movespeed : Int, val armor : Int, val armorperlevel : Double,
                    val spellblock : Int, val spellblockperlevel : Double, val attackrange : Int, val hpregen : Double, val hpregenperlevel : Double,
                    val mpregen : Double, val mpregenperlevel : Double, val crit : Int, val critperlevel : Int, val attackdamage : Int, val attackdamageperlevel : Double,
                    val attackspeedperlevel : Double, val attackspeed : Double)

 */
data class ChampionStatsDTO(val hp : String, val hpperlevel: String, val mp : String, val mpperlevel : String, val movespeed : String, val armor : String, val armorperlevel : String,
                            val spellblock : String, val spellblockperlevel : String, val attackrange : String, val hpregen : String, val hpregenperlevel : String,
                            val mpregen : String, val mpregenperlevel : String, val crit : String, val critperlevel : String, val attackdamage : String, val attackdamageperlevel : String,
                            val attackspeedperlevel : String, val attackspeed : String)
