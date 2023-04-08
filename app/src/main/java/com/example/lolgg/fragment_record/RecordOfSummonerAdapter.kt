package com.example.lolgg.fragment_record

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.lolgg.R


data class ItemsDTO(val type : String, val version : String, val data : List<ItemDataDTO>)

data class ItemDataDTO(val itemIdDTO : String, val itemDTO : ItemDTO)

data class ItemDTO(val name : String, val description : String, val plaintext : String,
                   val image : ItemImageDTO, val gold : ItemGoldDTO)

data class ItemImageDTO(val full : String, val sprite : String)

data class ItemGoldDTO(val base : String, val total : String)