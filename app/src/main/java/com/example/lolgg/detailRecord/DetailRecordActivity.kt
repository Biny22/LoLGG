package com.example.lolgg.detailRecord

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.lolgg.*
import com.example.lolgg.databinding.ActivityMainBinding
import com.example.lolgg.detailRecord.fragment_synthesis.SynthesisFragment
import com.example.lolgg.dto.Runes
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.runBlocking

class DetailRecordActivity : AppCompatActivity() {

    val matchDTO: MatchDTO by lazy {
        intent.getSerializableExtra("MatchDTO") as MatchDTO
    }

    val summonerDTO: SummonerDTO by lazy {
        intent.getSerializableExtra("SummonerDTO") as SummonerDTO
    }

    val championsDTO: ChampionsDTO by lazy {
        intent.getSerializableExtra("ChampionsDTO") as ChampionsDTO
    }

    val itemsDTO : ItemsDTO by lazy {
        intent.getSerializableExtra("ItemsDTO") as ItemsDTO
    }

    val runes : Runes.RunesForgedDTO by lazy {
        intent.getSerializableExtra("Runes.RunesForgedDTO") as Runes.RunesForgedDTO
    }

    val spells : Spells.SpellsDTO by lazy {
        intent.getSerializableExtra("Spells.SpellsDTO") as Spells.SpellsDTO
    }

    val itemFragment = ArrayList<Fragment>()

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_record)

        val tabLayout : TabLayout = findViewById(R.id.tabLayout)
        val viewPager = findViewById<ViewPager2>(R.id.detailPager)
        val adapter = MyPagerAdapter(this)
        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when(position)
            {
                0 -> tab.text = "종합"
                1 -> tab.text = "팀 분석"
                2 -> tab.text = "빌드"
            }
        }.attach()

        /*
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab?) {
                when(tab?.position)
                {
                    0 -> replaceFragment(0)
                    1 -> replaceFragment(0)
                    2 -> replaceFragment(0)
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }
        })

         */
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        initViewPager()

        return super.onCreateView(name, context, attrs)
    }

    private fun initViewPager()
    {
        //val demoCollectionAdapter = DemoCollectionAdapter(SynthesisFragment(), summonerDTO, matchDTO)
       // viewPager.adapter = demoCollectionAdapter
    }

    private fun replaceFragment(index : Int)
    {
        /*
        when(index)
        {
            0 -> {
                println("replaceFragment")
                val bundle = Bundle()
                bundle.putSerializable("getSummoner", summonerDTO)
                bundle.putSerializable("getMatch", matchDTO)
            }
        }
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.detailPager,itemFragment[0])
        fragmentTransaction.commit()

         */
    }

    private inner class MyPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

        override fun getItemCount(): Int {
            // 페이지 수 반환
            return 3
        }

        override fun createFragment(position: Int): Fragment {
            // 각 탭에 대한 프래그먼트 반환
            return SynthesisFragment(summonerDTO, matchDTO, championsDTO, itemsDTO, runes, spells)
        }
    }
}

class DemoCollectionAdapter(
    val fragment: Fragment, val summonerDTO: SummonerDTO,
    val matchDTO: MatchDTO, val championsDTO: ChampionsDTO,
    val itemsDTO: ItemsDTO, val runes: Runes.RunesForgedDTO,
    val spellsDTO: Spells.SpellsDTO) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = 3

    override fun createFragment(position: Int): Fragment {
        // Return a NEW fragment instance in createFragment(int)
        val fragment = SynthesisFragment(summonerDTO, matchDTO, championsDTO, itemsDTO, runes, spellsDTO)
        return fragment
    }
}

