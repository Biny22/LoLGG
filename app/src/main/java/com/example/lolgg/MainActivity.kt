package com.example.lolgg

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.widget.EditText
import android.widget.Toast
import com.example.lolgg.network.Network
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val edit = findViewById<EditText>(R.id.summonerEdit)

        edit.setOnKeyListener { view, i, keyEvent ->
            println("keyEvent : $i")
            if (keyEvent.action != KeyEvent.ACTION_UP && i == KeyEvent.KEYCODE_ENTER)
            {
                val network = Network(edit)
                var summonerDTO : SummonerDTO?

                runBlocking {
                    summonerDTO = network.summonerDTO
                }

                if(summonerDTO != null)
                {
                    Log.d("Log","success")
                    val intent = Intent(this,RecordOfSummonerActivity::class.java)
                    intent.putExtra("key",summonerDTO)
                    startActivity(intent)
                }
                else
                {
                    Toast.makeText(applicationContext, "존재하지 않는 소환사입니다.", Toast.LENGTH_LONG).show()
                }

            }
            true
        }
    }
}