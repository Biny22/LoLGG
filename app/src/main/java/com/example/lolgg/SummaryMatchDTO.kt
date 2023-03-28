package com.example.lolgg

data class SummaryMatchDTO(val matchInfoDTO : SummaryMatchInfoDTO)

data class SummaryMatchInfoDTO(val gameCreation : String, val gameStartTimeStamp : String, val gameEndTimeStamp : String,
                        val gameMode : String, val gameType : String, val participants : List<SummaryParticipantDTO>)

data class SummaryParticipantDTO(val assists : String, val championId : String, val championName : String, val death : String, val items : List<String>, val kill : String, val win : Boolean)
