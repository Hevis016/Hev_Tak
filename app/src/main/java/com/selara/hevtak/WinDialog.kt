package com.selara.hevtak

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.selara.hevtak.databinding.ActivityWinDialogBinding

class WinDialog(
    context: Context,
    private val message: String,
    private val gameActivity: AppCompatActivity? = null
) : Dialog(context) {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var binding: ActivityWinDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set the dialog's background to transparent
        window?.setBackgroundDrawableResource(android.R.color.transparent)
        window?.setDimAmount(0.8f)

        // Initialize MediaPlayer with the correct context
        mediaPlayer = MediaPlayer.create(context, R.raw.button_click)

        // Initialize ViewBinding
        binding = ActivityWinDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set the message text
        binding.messageTxt.text = message

        // Prevent the dialog from closing when touching outside
        setCanceledOnTouchOutside(false)

        // Set the click listener for the "Start Again" button
        // Set the click listener for the "Start Again" button
        binding.startAgainButton.setOnClickListener {
            mediaPlayer.start()
            dismiss() // Закриваємо діалог
            gameActivity?.let { activity ->
                if (activity is PlayerGame) {
                    activity.resetGame(it) // Викликаємо метод resetGame для PlayerGame
                } else if (activity is BotGame) {
                    activity.resetGame(it) // Викликаємо метод resetGame для BotGame
                }
            }
        }


        // Set the click listener for the "Головне меню" button
        binding.menuButton.setOnClickListener {
            mediaPlayer.start()
            dismiss() // Close the dialog
            val intent = Intent(context, Menu::class.java) // Adjust MainActivity to your actual main activity class
            context.startActivity(intent)
            gameActivity?.finish() // Optionally finish the current game activity
        }
    }

    override fun dismiss() {
        super.dismiss()
        // Release resources for MediaPlayer instances when the dialog is dismissed
        mediaPlayer.release()
    }
}
