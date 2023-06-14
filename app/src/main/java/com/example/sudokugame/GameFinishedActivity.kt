package com.example.sudokugame
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView

/**
 * Activity displayed when the game is finished.
 */
class GameFinishedActivity : AppCompatActivity() {

    private var mode = 1
    private lateinit var activity : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_finished)

        // Get the activity type from the intent extras
        activity = intent.getStringExtra("activity")!!

        // Set up the text view with the appropriate content based on the activity type
        val textView = findViewById<TextView>(R.id.time)
        var text = ""
        when (activity) {
            "classic" -> {
                val mistakes = intent.getIntExtra("mistakesCount", 0)
                val minutes = intent.getIntExtra("minutes", 0)
                val seconds = intent.getIntExtra("seconds", 0)
                mode = intent.getIntExtra("mode", 1)
                text = "You finished the game in \n$minutes "
                text += if (minutes == 1) "minute"
                else "minutes"
                text += " and $seconds "
                text += if (seconds == 1) "second"
                else "seconds"
                text += ".\nYou made $mistakes "
                text += if (mistakes == 1) "mistake."
                else "mistakes."
            }
            "twoPlayers" -> {
                text = intent.getStringExtra("type")!!
            }
        }
        textView.text = text
    }

    /**
     * Callback when the menu button is clicked.
     * Returns to the main menu activity.
     */
    fun menu(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        this.startActivity(intent)
        this.finish()
    }

    /**
     * Callback when the again button is clicked.
     * Restarts the game based on the activity type.
     */
    fun again(view: View) {
        when(activity) {
            "classic" -> {
                val intent = Intent(this, ClassicGameActivity::class.java)
                intent.putExtra("mode", mode)
                this.startActivity(intent)
                this.finish()
            }
            "twoPlayers" -> {
                val intent = Intent(this, TwoPlayersActivity::class.java)
                this.startActivity(intent)
                this.finish()
            }
        }
    }
}
