package com.example.wordlebyshik

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.wordlebyshik.databinding.ActivityMainBinding
import com.example.wordlebyshik.fragments.HowToPlayFragment
import com.example.wordlebyshik.fragments.MainFragment
import com.example.wordlebyshik.fragments.SettingsFragment
import com.example.wordlebyshik.fragments.StartFragment
import com.example.wordlebyshik.fragments.StatisticsFragment

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadSettings()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        showWelcomeFragment()

        supportActionBar?.title = "Wordle"
        supportActionBar?.setBackgroundDrawable(
            ColorDrawable(Color.parseColor("#992667"))
        )
    }

    private fun loadSettings() {
        val sharedPreferences = getSharedPreferences("settings_prefs", MODE_PRIVATE)
        val isMusicOn = sharedPreferences.getBoolean("music_enabled", true)

        startMusic(isMusicOn)
    }

    fun startMusic(isMusicOn: Boolean) {
        if (isMusicOn) {
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(this, R.raw.bgmusic)
                mediaPlayer?.isLooping = true
                mediaPlayer?.start()
            }
        } else {
            stopMusic()
        }
    }

    private fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.aboutGame -> {
                val howtoplay = HowToPlayFragment()
                replaceFragment(howtoplay)
                true
            }

            R.id.playBtn -> {
                val gameFragment = MainFragment()
                replaceFragment(gameFragment)
                true
            }

            R.id.settings -> {
                val settingsFragment = SettingsFragment()
                replaceFragment(settingsFragment)
                true
            }

            R.id.stats -> {
                val statsFragment = StatisticsFragment()
                replaceFragment(statsFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            //overridePendingTransition() for activity
            .addToBackStack(null)
            .commit()
    }

    private fun showWelcomeFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, StartFragment())
            .commit()

        Handler().postDelayed({
            supportFragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container, MainFragment())
                .commit()
        }, 3000)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
    }
}