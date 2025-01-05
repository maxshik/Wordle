package com.example.wordlebyshik.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.wordlebyshik.R
import com.google.gson.Gson
import java.io.File

class StatisticsFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var statisticsTextView: TextView
    private lateinit var winPercentageTextView: TextView
    private lateinit var lossesTextView: TextView
    private lateinit var resetStatsButton: Button

    data class GameStats(var wins: Int = 0, var losses: Int = 0)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_statistics, container, false)

        statisticsTextView = view.findViewById(R.id.statisticsTextView)
        winPercentageTextView = view.findViewById(R.id.winPercentageTextView)
        lossesTextView = view.findViewById(R.id.lossesTextView)
        resetStatsButton = view.findViewById(R.id.resetStatsButton)

        sharedPreferences = requireActivity().getSharedPreferences("settings_prefs", AppCompatActivity.MODE_PRIVATE)

        val backgroundColor = sharedPreferences.getInt("background_color", android.graphics.Color.WHITE)
        view.setBackgroundColor(backgroundColor)

        updateStatistics()

        resetStatsButton.setOnClickListener {
            resetStatistics()
        }

        return view
    }

    private fun updateStatistics() {
        val stats = readStatsFromFile()
        val totalGames = stats.wins + stats.losses
        val winPercentage = if (totalGames > 0) (stats.wins * 100) / totalGames else 0

        winPercentageTextView.text = "Вы выигрываете в $winPercentage% случаев"
        lossesTextView.text = "Проигрыши: ${stats.losses}"
    }

    private fun resetStatistics() {
        writeStatsToFile(GameStats(0, 0))
        updateStatistics()
    }

    private fun readStatsFromFile(): GameStats {
        val file = File(requireContext().filesDir, "game_stats.json")
        return if (file.exists()) {
            val json = file.readText()
            Gson().fromJson(json, GameStats::class.java)
        } else {
            GameStats()
        }
    }

    private fun writeStatsToFile(stats: GameStats) {
        val json = Gson().toJson(stats)
        val file = File(requireContext().filesDir, "game_stats.json")
        file.writeText(json)
    }
}