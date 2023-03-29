package com.example.lolgg

data class RunesForgedDTO(val dataId : List<String>, val data : List<RuneDataDTO>)

data class RuneDataDTO(val id : String, val key : String, val icon : String, val name : String, val slots : List<SlotDTO>)

data class SlotDTO(val runes : List<RuneDTO>)

data class RuneDTO(val id : String, val key : String, val icon : String, val name : String, val sortDesc : String, val longDesc : String)