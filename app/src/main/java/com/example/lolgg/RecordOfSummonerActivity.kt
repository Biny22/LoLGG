package com.example.lolgg

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.lolgg.network.Network
import kotlinx.coroutines.runBlocking

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

        val tierOfSummoner = findViewById<RecyclerView>(R.id.tierOfSummoner)
        val tierOfSummonerAdapter = TierOfSummonerAdapter(summonerDTO, baseContext)
        tierOfSummoner.adapter = tierOfSummonerAdapter
        tierOfSummoner.addItemDecoration(SpaceItemDecoration(0,6))




        val recordOfSummoner = findViewById<RecyclerView>(R.id.recordOfSummoner)
        val recordOfSummonerAdapter = RecordOfSummonerAdapter(summonerDTO, baseContext)
        recordOfSummoner.adapter = recordOfSummonerAdapter
        recordOfSummoner.addItemDecoration(SpaceItemDecoration(0,10))
        val layoutManager = recordOfSummoner.layoutManager as LinearLayoutManager

        recordOfSummoner.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            @SuppressLint("NotifyDataSetChanged")
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if(!isLoading)
                {
                    if(layoutManager.findLastCompletelyVisibleItemPosition() == recordOfSummonerAdapter.itemCount-1
                        && recordOfSummonerAdapter.matches.matchId.size < 50)
                    {
                        isLoading = true
                        val list : MutableList<String>
                        val start = recordOfSummonerAdapter.matches.matchId.size
                        val count = 10

                        recordOfSummonerAdapter.matches.matchId.add(null.toString())

                        runBlocking {
                            list = network.requestMatchId(start,count)
                            if(list.size == 0)
                            {
                                println("더 불러올게 없어용..")
                                return@runBlocking
                            }
                        }

                        Handler(Looper.getMainLooper()).postDelayed({
                            recordOfSummonerAdapter.notifyItemInserted(start)
                            recordOfSummonerAdapter.matches.matchId.removeAt(start)
                            println("새로 불러오는 중이야~")
                            for(i in 0 until list.size)
                            {
                                recordOfSummonerAdapter.matches.matchId.add(list[i])
                            }
                            recordOfSummonerAdapter.matches.notifyIdInserted(count)

                        recordOfSummonerAdapter.notifyDataSetChanged()
                        isLoading = false }, 3000)
                       // runBlocking { loadMore(recyclerViewAdapter) }
                    }
                }
            }
        })



        val inGameButton = findViewById<Button>(R.id.inGameButton)
        inGameButton.setBackgroundResource(R.drawable.round_victory)
        val refreshButton = findViewById<Button>(R.id.refreshButton)
        refreshButton.setBackgroundResource(R.drawable.round_victory)


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
        recyclerViewAdapter.matches.matchId.add(null.toString())
        recyclerViewAdapter.notifyItemInserted(recyclerViewAdapter.itemCount - 1)

        Handler(Looper.getMainLooper()).postDelayed({

            recyclerViewAdapter.matches.matchId.removeAt(recyclerViewAdapter.itemCount-1)
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
