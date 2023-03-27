package com.example.lolgg

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class RecordOfSummonerAdapter(private val summonerDTO : SummonerDTO, private val apiKey : String) : RecyclerView.Adapter<RecordOfSummonerAdapter.RecordViewHolder>() {
    inner class RecordViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        // 여기서 레이아웃 구성 요소 가져오기
        // 리스너 등록
        val itemImgView: MutableList<ImageView>
        val spellImgView: MutableList<ImageView>
        val runeImgView: MutableList<ImageView>
        val championImgView: ImageView

        init {
            itemImgView = getItemView()
            spellImgView = getSpellView()
            runeImgView = getRuneView()
            championImgView = view.findViewById(R.id.championImgView)
        }

        private fun getItemView(): MutableList<ImageView> {
            val list = mutableListOf<ImageView>()
            list.add(view.findViewById(R.id.itemImgView1))
            list.add(view.findViewById(R.id.itemImgView2))
            list.add(view.findViewById(R.id.itemImgView3))
            list.add(view.findViewById(R.id.itemImgView4))
            list.add(view.findViewById(R.id.itemImgView5))
            list.add(view.findViewById(R.id.itemImgView6))
            list.add(view.findViewById(R.id.itemImgView7))

            return list
        }

        private fun getSpellView(): MutableList<ImageView> {
            val list = mutableListOf<ImageView>()
            list.add(view.findViewById(R.id.spellImgView1))
            list.add(view.findViewById(R.id.spellImgView2))

            return list
        }

        private fun getRuneView(): MutableList<ImageView> {
            val list = mutableListOf<ImageView>()
            list.add(view.findViewById(R.id.perksPrimaryStyleImgView))
            list.add(view.findViewById(R.id.perksSubStyleImgView))

            return list
        }
    }

    private val matches : MutableList<String> by lazy {
        runBlocking {
            requestMatches()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordViewHolder
    {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.record_item, parent, false)
        return RecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordViewHolder, position: Int)
    {
        // 여기서 꾸미기

    }

    override fun getItemCount(): Int {
        return 5
    }

    private suspend fun requestMatches() : MutableList<String>
    {
        val matches = mutableListOf<String>()
        withContext(Dispatchers.IO) {
            val requestURL = "https://asia.api.riotgames.com/lol/match/v5/matches/by-puuid/${summonerDTO.puuid}/ids?start=0&count=20&api_key=$apiKey"
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


data class ItemsDTO(val type : String, val version : String, val data : List<ItemDTO>)

data class ItemDTO(val itemIdList : List<String>, val itemDataDTO : List<ItemDataDTO>)

data class ItemDataDTO(val name : String, val description : String, val plaintext : String,
                       val image : ItemImageDTO, val gold : ItemGoldDTO)

data class ItemImageDTO(val full : String, val sprite : String)

data class ItemGoldDTO(val base : String, val total : String)