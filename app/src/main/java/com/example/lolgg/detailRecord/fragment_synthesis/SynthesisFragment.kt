package com.example.lolgg.detailRecord.fragment_synthesis

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.lolgg.*
import com.example.lolgg.dto.Runes


class SynthesisFragment(val summonerDTO: SummonerDTO, val match: MatchDTO,
                        val championsDTO: ChampionsDTO, val itemsDTO: ItemsDTO,
                        val runes: Runes.RunesForgedDTO, val spells: Spells.SpellsDTO) : Fragment() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    @SuppressLint("ResourceAsColor")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        println("ㅎㅇㅎㅇㅎㅇ")
        val view = inflater.inflate(R.layout.fragment_synthesis, container, false)
        p(view)
        return view
    }

    fun p(view : View)
    {
        val team1ParticipantDTO : MutableList<ParticipantDTO> = mutableListOf()
        val team2ParticipantDTO : MutableList<ParticipantDTO> = mutableListOf()

        var playerTeamId = "100"

        for(participant in match.participants)
        {
            if(participant.teamId == "100")
            {
                team1ParticipantDTO.add(participant)
            }
            else
            {
                team2ParticipantDTO.add(participant)

            }

            val summonerName = participant.summonerName

            if(summonerName != summonerDTO.name)
                continue

            playerTeamId = participant.teamId
        }

        val team1BanImgView : MutableList<ImageView> = mutableListOf()
        team1BanImgView.add(view.findViewById(R.id.team1Ban1))
        team1BanImgView.add(view.findViewById(R.id.team1Ban2))
        team1BanImgView.add(view.findViewById(R.id.team1Ban3))
        team1BanImgView.add(view.findViewById(R.id.team1Ban4))
        team1BanImgView.add(view.findViewById(R.id.team1Ban5))

        val team2BanImgView : MutableList<ImageView> = mutableListOf()
        team2BanImgView.add(view.findViewById(R.id.team2Ban1))
        team2BanImgView.add(view.findViewById(R.id.team2Ban2))
        team2BanImgView.add(view.findViewById(R.id.team2Ban3))
        team2BanImgView.add(view.findViewById(R.id.team2Ban4))
        team2BanImgView.add(view.findViewById(R.id.team2Ban5))


        if(playerTeamId == "100")
        {
            recyclerview1(view, team1ParticipantDTO)
            recyclerview2(view, team2ParticipantDTO)

            setBanImage(1, match.teams[0].bans, team1BanImgView)
            setBanImage(2, match.teams[1].bans, team2BanImgView)
        }
        else
        {
            recyclerview1(view, team2ParticipantDTO)
            recyclerview2(view, team1ParticipantDTO)

            setBanImage(2, match.teams[1].bans, team2BanImgView)
            setBanImage(1, match.teams[0].bans, team1BanImgView)
        }
    }


    fun recyclerview1(view : View, playerParticipantDTO: MutableList<ParticipantDTO>)
    {
        val teamResult = view.findViewById<TextView>(R.id.team1Result)
        val teamResultColor = view.findViewById<TextView>(R.id.team1ResultColor)
        setResultColor(teamResult, teamResultColor, playerParticipantDTO)

        val synthesisRecyclerview = view.findViewById<RecyclerView>(R.id.synthesisRecyclerview)
        val synthesisRecordAdapter = SynthesisRecordAdapter(summonerDTO, playerParticipantDTO, championsDTO, itemsDTO, runes, spells, 0)
        synthesisRecyclerview?.adapter = synthesisRecordAdapter

        val layoutManager = LinearLayoutManager(context)
        synthesisRecyclerview?.setLayoutManager(LinearLayoutManager(context))
        //synthesisRecyclerview?.addItemDecoration(SpaceItemDecoration(0,6))
    }

    fun recyclerview2(view : View, playerParticipantDTO: MutableList<ParticipantDTO>)
    {
        val teamResult = view.findViewById<TextView>(R.id.team2Result)
        val teamResultColor = view.findViewById<TextView>(R.id.team2ResultColor)
        setResultColor(teamResult, teamResultColor, playerParticipantDTO)

        val synthesisRecyclerview = view.findViewById<RecyclerView>(R.id.synthesisRecyclerview2)
        val synthesisRecordAdapter = SynthesisRecordAdapter(summonerDTO, playerParticipantDTO, championsDTO, itemsDTO, runes, spells, 5)
        synthesisRecyclerview?.adapter = synthesisRecordAdapter

        val layoutManager = LinearLayoutManager(context)
        synthesisRecyclerview?.setLayoutManager(LinearLayoutManager(context))
        //synthesisRecyclerview?.addItemDecoration(SpaceItemDecoration(0,6))

    }

    fun setResultColor(teamResult : TextView, resultColor : TextView, playerParticipantDTO: MutableList<ParticipantDTO>)
    {
        if(playerParticipantDTO[0].win)
        {
            println("gd")
            teamResult.text = "승리"
            resultColor.setBackgroundResource(R.color.victoryColor)
        }
        else
        {
            teamResult.text = "패배"
            resultColor.setBackgroundResource(R.color.defeatColor)
        }
    }

    fun setBanImage(team : Int, teamBan : MutableList<BanDTO?>, banImageView : MutableList<ImageView>)
    {
        val max = if(team == 100) 5 else 10

        for(turn in 1..5)
        {
            var championId = "-1"
            for(ban in teamBan)
            {
                if(ban?.championId == null)
                    continue

                val pickTurn = if(team == 1) ban.pickTurn.toInt() else ban.pickTurn.toInt() + 5

                if(turn != pickTurn)
                    continue

                championId = ban.championId
                break
            }

            for(champion in championsDTO.data)
            {
                if(champion.key != championId)
                    continue

                setIcon("http://ddragon.leagueoflegends.com/cdn/13.7.1/img/champion/${champion.id}.png",
                    50, banImageView[turn-1])
            }

        } }

    fun setIcon(requestURL : String, radius : Int, imageView : ImageView)
    {
        Glide.with(imageView.context).load(requestURL)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(radius))).into(imageView)
    }
}