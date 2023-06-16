package com.example.lolgg.detailRecord.fragment_synthesis

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lolgg.*
import com.example.lolgg.databinding.FragmentSynthesisBinding
import kotlin.properties.Delegates


class SynthesisFragment : Fragment() {

    private var summonerDTO : SummonerDTO by Delegates.notNull()
    private var match : MatchDTO by Delegates.notNull()
    private var index by Delegates.notNull<Int>()
    private var bind : FragmentSynthesisBinding? = null
    private val binding get() = bind

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            summonerDTO = it.getSerializable("getSummoner") as SummonerDTO
            match = it.getSerializable("getMatchDTO") as MatchDTO
            index = it.getInt("getIndex")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        println("ㅎㅇㅎㅇㅎㅇ")
        val synthesisRecyclerview = view?.findViewById<RecyclerView>(R.id.synthesisRecyclerview)
        val synthesisAdapter = SynthesisRecordAdapter(summonerDTO, match)
        synthesisRecyclerview?.adapter = synthesisAdapter

        bind = FragmentSynthesisBinding.inflate(inflater, container, false)
        return binding?.root
    }

    companion object {
        fun newInstance(param1: String, param2: String) =
            SynthesisFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}