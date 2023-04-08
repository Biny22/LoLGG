package com.example.lolgg

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lolgg.network.Network
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class RecordOfSummonerActivity : AppCompatActivity() {

    private val summonerDTO by lazy {
        intent.getSerializableExtra("getSummoner") as SummonerDTO
    }

    var isLoading = false

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

        val recyclerView = findViewById<RecyclerView>(R.id.recordOfSummoner)
        val recyclerViewAdapter = RecordOfSummonerAdapter(summonerDTO, baseContext)
        recyclerView.adapter = recyclerViewAdapter
        recyclerView.addItemDecoration(SpaceItemDecoration(0,10))
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager

                if(!isLoading)
                {
                    if(layoutManager.findLastCompletelyVisibleItemPosition() == recyclerViewAdapter.itemCount-1
                        && recyclerViewAdapter.matches.size < 50)
                    {
                        isLoading = true
                        val list : MutableList<String>
                        val start = recyclerViewAdapter.matches.size
                        Log.d("dddd","start : $start")

                        //recyclerViewAdapter.matches.add(null.toString())
                        //recyclerViewAdapter.notifyItemInserted(recyclerViewAdapter.itemCount - 1)

                        runBlocking {
                            //recyclerViewAdapter.matches.removeAt(start+1)
                            list = network.requestMatchId(start,10)

                            if(list.size == 0)
                            {
                                return@runBlocking
                            }

                            Handler(Looper.getMainLooper()).postDelayed({
                                for(i in 0..9)
                                {
                                    recyclerViewAdapter.matches.add(list[i])
                                    recyclerViewAdapter.notifyItemInserted(start+i)
                                }
                                isLoading = false
                            }, 2000)
                        }
                        //runBlocking { loadMore(recyclerViewAdapter) }
                    }
                }
            }
        })

        val inGameButton = findViewById<Button>(R.id.inGameButton)
        inGameButton.setBackgroundResource(R.drawable.round_ex1)
        val refreshButton = findViewById<Button>(R.id.refreshButton)
        refreshButton.setBackgroundResource(R.drawable.round_ex2)


        runBlocking {
            profileIconBitmap = network.getProFileIcon()!!
            mostChampionViewBitmap = network.getMostChampionView()!!
        }

        val draw =  RoundedBitmapDrawableFactory.create(resources,profileIconBitmap)
        draw.cornerRadius = 75.0f
        profileIconView.setImageDrawable(draw)
        //profileIconView.setImageBitmap(profileIconBitmap)

        mostChampionView.setImageBitmap(mostChampionViewBitmap)
        // println(s)
    }

    suspend fun loadMore(recyclerViewAdapter: RecordOfSummonerAdapter)
    {
        recyclerViewAdapter.matches.add(null.toString())
        recyclerViewAdapter.notifyItemInserted(recyclerViewAdapter.itemCount - 1)

        Handler(Looper.getMainLooper()).postDelayed({

            recyclerViewAdapter.matches.removeAt(recyclerViewAdapter.itemCount-1)
            val scrollPosition = recyclerViewAdapter.itemCount
            recyclerViewAdapter.notifyItemRemoved(scrollPosition)
            val currentSize = scrollPosition
            val nextLimit = currentSize + 10
            println("맨 밑")

            while (currentSize -1 < nextLimit)
            {
                // 아이템 추가
            }

            //recyclerViewAdapter.notifyDataSetChanged();
            isLoading = false
        }, 2000)
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
