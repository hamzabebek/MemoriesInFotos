package com.example.memoriesinfotos

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
            object : CountDownTimer(1800,1000){
            override fun onTick(millisUntilFinished: Long) {
            }
            override fun onFinish() {
                val startIntent = Intent(this@MainActivity,FotoList::class.java)
                startIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(startIntent)
            }
        }.start()
    }
    fun start(view: View){
        val start = Intent(this@MainActivity,FotoList::class.java)
        startActivity(start)
    }
}