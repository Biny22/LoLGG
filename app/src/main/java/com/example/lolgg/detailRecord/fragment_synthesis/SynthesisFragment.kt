package com.example.lolgg.detailRecord.fragment_synthesis

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
        recyclerview1(view)
        recyclerview2(view)
        return view
    }


    fun recyclerview1(view : View)
    {
        val synthesisRecyclerview = view.findViewById<RecyclerView>(R.id.synthesisRecyclerview)
        val synthesisRecordAdapter = SynthesisRecordAdapter(summonerDTO, match, championsDTO, itemsDTO, runes, spells, 0)
        synthesisRecyclerview?.adapter = synthesisRecordAdapter
        val layoutManager = LinearLayoutManager(context)
        synthesisRecyclerview?.setLayoutManager(LinearLayoutManager(context))
        //synthesisRecyclerview?.addItemDecoration(SpaceItemDecoration(0,6))
    }

    fun recyclerview2(view : View)
    {
        val synthesisRecyclerview = view.findViewById<RecyclerView>(R.id.synthesisRecyclerview2)
        val synthesisRecordAdapter = SynthesisRecordAdapter(summonerDTO, match, championsDTO, itemsDTO, runes, spells, 5)
        synthesisRecyclerview?.adapter = synthesisRecordAdapter
        val layoutManager = LinearLayoutManager(context)
        synthesisRecyclerview?.setLayoutManager(LinearLayoutManager(context))
        //synthesisRecyclerview?.addItemDecoration(SpaceItemDecoration(0,6))

    }
}