package com.example.lolgg

import java.io.Serializable

data class SummonerDTO(val accountId : String, val profileIconId : Int, val revisionDate : Long,
                       val name : String, val id : String, val puuid : String, val summonerLevel : Int) : Serializable
