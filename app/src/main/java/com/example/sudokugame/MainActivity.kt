package com.example.sudokugame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        configureButtons()
    }

    private fun configureButtons() {
        findViewById<Button>(R.id.easyButton).setOnClickListener {
            nextClassicActivity(1)
        }
        findViewById<Button>(R.id.mediumButton).setOnClickListener {
            nextClassicActivity(2)
        }
        findViewById<Button>(R.id.hardButton).setOnClickListener {
            nextClassicActivity(3)
        }
        findViewById<Button>(R.id.twoPlayersButton).setOnClickListener {
            nextTwoPlayersActivity()
        }
    }

    private fun nextTwoPlayersActivity() {
        val intent = Intent(this, TwoPlayersActivity::class.java)
        startActivity(intent)
    }

    private fun nextClassicActivity(mode: Int) {
        val intent = Intent(this, ClassicGameActivity::class.java)
        intent.putExtra("mode", mode)
        startActivity(intent)
    }


}