package com.example.lolgg

import com.example.lolgg.network.Network
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable
import java.lang.reflect.InvocationTargetException

class Matches(private val network: Network) : Serializable {

    var start = 0
    val matchId : MutableList<String> = runBlocking {
        println("matchId 생성 중 ")
        network.requestMatchId(start, 10) }
    val matchDTO : MutableList<MatchDTO?> = mutableListOf()

    init {
        notifyIdInserted(10)
    }

    fun notifyIdInserted(count : Int)
    {
        val end = start + count
        runBlocking {
            for(index in start until end)
            {
                matchDTO.add(parsingMatchDTO(index))
            }
            start = matchId.size
        }
        println("생성 완료")
    }

    private fun notifyIdInserted(start : Int, end : Int)
    {
        for(index in start until end)
        {
            matchId[index]
        }
    }

    private suspend fun parsingMatchDTO(index : Int) : MatchDTO?
    {
        var matchDTO : MatchDTO? = null
        withContext(Dispatchers.Default) {
            val matchJson =  network.requestMatch(matchId[index]) ?: return@withContext null
            val info = JSONObject(matchJson["info"].toString())

            val gameCreation = info["gameCreation"].toString()
            val gameStartTimestamp = info["gameStartTimestamp"].toString()
            val gameEndTimestamp = info["gameEndTimestamp"].toString()
            val gameId = info["gameId"].toString()
            val queueId = info["queueId"].toString()
            val teamArray = JSONArray(info["teams"].toString())
            val teams = getTeams(teamArray)
            val gameType = info["gameType"].toString()
            val participantsArray = JSONArray(info["participants"].toString())
            val participants = getParticipants(participantsArray)

            matchDTO = MatchDTO(gameCreation, gameEndTimestamp, gameStartTimestamp, gameId, queueId, teams, gameType, participants)
        }

        return matchDTO
    }

    fun getMatchDTO(index : Int) : MatchDTO?
    {
        return matchDTO[index]
    }

    fun getParticipants(participantsArray : JSONArray) : MutableList<ParticipantDTO>
    {
        val participants = mutableListOf<ParticipantDTO>()

        for(index in 0 until participantsArray.length())
        {
            val participantJson = JSONObject(participantsArray[index].toString())
            val assists = participantJson["assists"].toString()
            val baronKills = participantJson["baronKills"].toString()
            val championLevel = participantJson["champLevel"].toString()
            val championId = participantJson["championId"].toString()
            val deaths = participantJson["deaths"].toString()
            val detectorWardsPlaced = participantJson["detectorWardsPlaced"].toString()
            val goldEarned = participantJson["goldEarned"].toString()

            val purchasedItems = mutableListOf<String>()
            for(itemIdx in 0..6)
                purchasedItems.add(participantJson["item$itemIdx"].toString())

            val kills = participantJson["kills"].toString()
            val killingSpree = participantJson["largestKillingSpree"].toString()
            val multiKill = participantJson["largestMultiKill"].toString()
            val neutralMinionsKilled = participantJson["neutralMinionsKilled"].toString()

            val perksJson = JSONObject(participantJson["perks"].toString())
            val statPerks = Gson().fromJson(perksJson["statPerks"].toString(), StatPerksDTO::class.java)
            val perksArray = JSONArray(perksJson["styles"].toString())
            val styleList = getStyleList(perksArray)
            val perks = PerksDTO(statPerks, styleList[0], styleList[1])

            val summonerSpell = mutableListOf<String>()
            for(spellIdx in 1..2)
                summonerSpell.add(participantJson["summoner${spellIdx}Id"].toString())

            val summonerName = participantJson["summonerName"].toString()
            val teamPosition = participantJson["teamPosition"].toString()
            val totalDamageDealtToChampion = participantJson["totalDamageDealtToChampions"].toString()
            val totalMinionsKill = participantJson["totalMinionsKilled"].toString()
            val totalDamageTaken = participantJson["totalDamageTaken"].toString()
            val win : Boolean = participantJson["win"].toString().toBoolean()

            val challengesJson = JSONObject(participantJson["challenges"].toString())
            val challenges = getChallenges(challengesJson)

            participants.add(ParticipantDTO(assists, baronKills, championLevel, championId, deaths,
                detectorWardsPlaced, goldEarned, purchasedItems, kills, killingSpree, multiKill,
                neutralMinionsKilled, perks, summonerSpell, summonerName, teamPosition, totalDamageDealtToChampion,
                totalMinionsKill, totalDamageTaken, win, challenges))
        }

        return participants
    }

    fun getChallenges(challengesJson : JSONObject) : ChallengesDTO
    {
        val killParticipation =
            if (challengesJson.has("killParticipation")) challengesJson["killParticipation"].toString()
            else "0"

        return ChallengesDTO(killParticipation)
    }

    fun getStyleList(perksArray : JSONArray) : MutableList<StyleDTO>
    {
        val styleList = mutableListOf<StyleDTO>()

        for(perksIdx in 0 until perksArray.length())
        {
            val styleJson = JSONObject(perksArray[perksIdx].toString())
            val description = styleJson["description"].toString()
            val selectionsArray = JSONArray(styleJson["selections"].toString())
            val runeId = mutableListOf<String>()
            for(selectionsIdx in 0 until selectionsArray.length())
                runeId.add(JSONObject(selectionsArray[selectionsIdx].toString())["perk"].toString())

            val style = styleJson["style"].toString()
            styleList.add(StyleDTO(description, runeId, style))
        }

        return styleList
    }

    fun getTeams(teamArray : JSONArray) : MutableList<TeamDTO>
    {
        val teams = mutableListOf<TeamDTO>()

        for(index in 0 until teamArray.length())
        {
            val teamsJson = JSONObject(teamArray[index].toString())
            val gson = Gson()
            val banArray = gson.fromJson(teamsJson["bans"].toString(), Array<BanDTO>::class.java)
            val bans : MutableList<BanDTO?> = mutableListOf()

            for(banIdx in banArray.indices)
                bans.add(banArray[banIdx])

            val objectives = gson.fromJson(teamsJson["objectives"].toString(), ObjectivesDTO::class.java)
            teams.add(TeamDTO(bans, objectives))
        }

        return teams
    }
}