package com.example.lolgg

data class SpellsDTO(val type : String, val version : String, val data : List<SpellDTO>)

data class SpellDTO(val id : String, val name : String, val description : String, val key : String, val image : String)