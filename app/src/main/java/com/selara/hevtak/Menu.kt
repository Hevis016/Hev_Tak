package com.selara.hevtak

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.selara.hevtak.databinding.ActivityMenuBinding

class Menu : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding
    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var backgroundSong: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize and start the background music
        backgroundSong = MediaPlayer.create(this, R.raw.jazz_sound)
        backgroundSong.isLooping = true // Set looping
        backgroundSong.start()

        // Initialize the MediaPlayer for button click sound
        mediaPlayer = MediaPlayer.create(this, R.raw.button_click)

        binding.buttonTwoPlayer.setOnClickListener {
            mediaPlayer.start()
            val intent = Intent(this, PlayerGame::class.java)
            startActivity(intent)
        }

        binding.buttonGameWithBot.setOnClickListener {
            mediaPlayer.start()
            val intent = Intent(this, BotGame::class.java)
            startActivity(intent)
        }


        binding.buttonExit.setOnClickListener {
            mediaPlayer.start()
            finishAffinity()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release resources for MediaPlayer instances
        mediaPlayer.release()
        backgroundSong.release()
    }
}
