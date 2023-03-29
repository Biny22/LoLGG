package com.example.lolgg


data class SummaryMatchDTO(val matchInfoDTO : SummaryMatchInfoDTO)

data class SummaryMatchInfoDTO(val gameCreation : String, val gameStartTimestamp : String, val gameEndTimestamp : String,
                        val gameMode : String, val gameType : String, val participants : SummaryParticipantDTO)

data class SummaryParticipantDTO(val assists : String, val championId : String, val championName : String, val deaths : String,
                                 val items : List<String>, val kill : String, val runeOfSummonerDTO : RuneOfSummonerDTO, val spellId : List<String>, val win : Boolean)

data class RuneOfSummonerDTO(val statsPerks : StatsPerksOfSummonerDTO, val styles : StylesDTO)

data class StatsPerksOfSummonerDTO(val defense : String, val flex : String, val offense : String)

data class StylesDTO(val styles : List<StyleDTO>)

data class StyleDTO(val description : String, val runeId : List<String>, val style : String)