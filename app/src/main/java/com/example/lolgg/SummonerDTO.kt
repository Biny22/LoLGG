package com.example.lolgg

import java.io.Serializable

data class SummonerDTO(val accountId : String, val profileIconId : Int, val revisionDate : Long,
                       val name : String, val id : String, val puuid : String, val summonerLevel : Int, val soloRank : RankDTO?, val flexRank : RankDTO?) : Serializable

data class RankDTO(val leagueId : String, val tier : String, val leaguePoints : String, val wins : String, val losses : String) : Serializable