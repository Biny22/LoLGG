package com.example.lolgg

import java.io.Serializable


// 원래는 info임
data class MatchDTO(val gameCreation : String, val gameEndTimestamp : String, val gameStartTimestamp : String, val gameId : String,
                    val queueId : String, val teams : MutableList<TeamDTO>, val gameType : String, val participants : MutableList<ParticipantDTO>) : Serializable


data class ParticipantDTO(val assists : String, val baronKills : String, val champLevel : String, val championId : String, val deaths : String,
                          val detectorWardsPlaced : String, val goldEarned : String, val purchasedItems : List<String>, val kills : String,
                          val killingSpree : String, val multiKill: String, val neutralMinionsKilled : String, val perks : PerksDTO,
                          val summonerSpell : MutableList<String>, val summonerName : String, val teamPosition : String, val totalDamageDealtToChampion : String,
                          val totalMinionsKill : String, val totalDamageTaken : String, val win : Boolean, val challenges : ChallengesDTO) : Serializable

data class TeamDTO(val bans : MutableList<BanDTO?>, val objectives : ObjectivesDTO) : Serializable

data class BanDTO(val championId : String, val pickTurn : String) : Serializable

data class ObjectivesDTO(val baron : ObjectiveDTO, val champion : ObjectiveDTO, val dragon : ObjectiveDTO,
                         val inhibitor : ObjectiveDTO, val riftHerald : ObjectiveDTO, val tower : ObjectiveDTO) : Serializable

data class ObjectiveDTO(val first : String, val kills : String) : Serializable

data class PerksDTO(val statPerksDTO: StatPerksDTO, val primaryStyle : StyleDTO, val subStyle : StyleDTO) : Serializable

data class StatPerksDTO(val defense : String, val flex : String, val offense : String) : Serializable

data class StyleDTO(val description : String, val runeId : List<String>, val style : String) : Serializable

data class ChallengesDTO(val killParticipation : String) : Serializable