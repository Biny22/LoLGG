package com.example.lolgg

data class SummaryMatchDTO(val matchInfoDTO : SummaryMatchInfoDTO)

data class SummaryMatchInfoDTO(val gameCreation : String, val gameStartTimestamp : String, val gameEndTimestamp : String,
                        val gameMode : String, val gameType : String, val participants : SummaryParticipantDTO)

data class SummaryParticipantDTO(val assists : String, val championId : String, val championName : String, val deaths : String, val items : List<String>, val kill : String, val win : Boolean)

