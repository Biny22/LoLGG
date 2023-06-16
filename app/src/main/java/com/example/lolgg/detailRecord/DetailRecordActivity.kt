package com.example.lolgg.detailRecord

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.lolgg.MatchDTO
import com.example.lolgg.R
import com.example.lolgg.SummonerDTO
import com.example.lolgg.databinding.ActivityMainBinding
import com.example.lolgg.detailRecord.fragment_synthesis.SynthesisFragment
import com.google.android.material.tabs.TabLayout

class DetailRecordActivity : AppCompatActivity() {

    val matchDTO: MatchDTO by lazy {
        intent.getSerializableExtra("MatchDTO") as MatchDTO
    }

    val summonerDTO: SummonerDTO by lazy {
        intent.getSerializableExtra("SummonerDTO") as SummonerDTO
    }

    val itemFragment = ArrayList<Fragment>()

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_record)

        val tab : TabLayout = findViewById(R.id.tabLayout)
        itemFragment.add(SynthesisFragment())

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewPager()

        tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

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
    }

    private fun initViewPager()
    {
        val viewPager2Adapter = ViewPager2Adapter(this)
        viewPager2Adapter.addFragment(SynthesisFragment())
        viewPager2Adapter.addFragment(SynthesisFragment())
        viewPager2Adapter.addFragment(SynthesisFragment())

        binding.


        TabLayoutMediator(binding.tlNavigationView, binding.vpViewpagerMain) { tab, position ->
            Log.e("YMC", "ViewPager position: ${position}")
            when (position) {
                0 -> tab.text = "Tab1"
                1 -> tab.text = "Tab2"
                2 -> tab.text = "Tab3"
            }
        }.attach()
    }

    private fun replaceFragment(index : Int)
    {
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
        //fragmentTransaction.replace(R.id.,fragment)
        //fragmentTransaction.commit()

    }
}

class ViewPager2Adapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    var fragments: ArrayList<Fragment> = ArrayList()

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    fun addFragment(fragment: Fragment) {
        fragments.add(fragment)
        notifyItemInserted(fragments.size - 1)
        //TODO: notifyItemInserted!!
    }

    fun removeFragment() {
        fragments.removeLast()
        notifyItemRemoved(fragments.size)
        //TODO: notifyItemRemoved!!
    }

}
