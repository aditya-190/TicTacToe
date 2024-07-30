package com.bhardwaj.xo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

class MainActivity : AppCompatActivity() {
    private var btnSinglePLayer: Button? = null
    private var btnMultiplayer: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initialise()
        clickListeners()
    }

    private fun initialise() {
        val mContext: Context = this@MainActivity
        val adActivityMain = findViewById<AdView>(R.id.adActivityMain)
        btnSinglePLayer = findViewById(R.id.btnSinglePlayer)
        btnMultiplayer = findViewById(R.id.btnMultiplayer)
        MobileAds.initialize(mContext)
        val adRequest = AdRequest.Builder().build()
        adActivityMain.loadAd(adRequest)
    }

    private fun clickListeners() {
        btnSinglePLayer!!.setOnClickListener {
            val intent = Intent(this@MainActivity, GameLogic::class.java)
            intent.putExtra("Game", "SinglePlayer")
            startActivity(intent)
        }
        btnMultiplayer!!.setOnClickListener {
            val intent = Intent(this@MainActivity, GameLogic::class.java)
            intent.putExtra("Game", "MultiPlayer")
            startActivity(intent)
        }
    }
}