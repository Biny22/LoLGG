package com.example.lolgg.fragment_record

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.lolgg.R

class RecordOfSummonerAdapter : RecyclerView.Adapter<RecordOfSummonerAdapter.RecordOfSummonerViewHolder>() {
    inner class RecordOfSummonerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // 누르면 실행되는 부분
        val itemList : MutableList<String> // 아이템 리스트
        val runeList : MutableList<String> // 룬을 담고 있는 리스트
        val champion : ImageView // 챔피온 이미지를 담고 있는
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecordOfSummonerViewHolder {
        val view =  LayoutInflater.from(parent.context).inflate(R.layout.record_item,parent,false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecordOfSummonerViewHolder, position: Int) {
        TODO("Not yet implemented")
        //
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
        // 아이템의 개수 반환
    }
}



data class ItemsDTO(val type : String, val version : String, val data : List<ItemDataDTO>)

data class ItemDataDTO(val itemIdDTO : String, val itemDTO : ItemDTO)

data class ItemDTO(val name : String, val description : String, val plaintext : String,
                   val image : ItemImageDTO, val gold : ItemGoldDTO)

data class ItemImageDTO(val full : String, val sprite : String)

data class ItemGoldDTO(val base : String, val total : String)