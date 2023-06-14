package com.example.sudokugame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView

class GameFinishedActivity : AppCompatActivity() {

    private var mode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_finished)
        val mistakes = intent.getIntExtra("mistakesCount",0)
        val minutes = intent.getIntExtra("minutes",0)
        val seconds = intent.getIntExtra("seconds",0)
        mode = intent.getIntExtra("mode",1)
        val textView = findViewById<TextView>(R.id.time)
        var text = "You finished the game in \n$minutes "
        text += if (minutes == 1) "minute"
        else "minutes"
        text += " and $seconds "
        text += if (seconds == 1) "second"
        else "seconds"
        text += ".\nYou made $mistakes "
        text += if (mistakes == 1) "mistake."
        else "mistakes."
        textView.text = text
    }

    fun menu(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        this.startActivity(intent)
        this.finish()
    }

    fun again(view: View) {
        val intent = Intent(this, ClassicGameActivity::class.java)
        intent.putExtra("mode", mode)
        this.startActivity(intent)
        this.finish()
    }
}