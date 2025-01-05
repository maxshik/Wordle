package com.example.wordlebyshik.fragments

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.GridLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wordlebyshik.GameStats
import com.example.wordlebyshik.R
import com.example.wordlebyshik.databinding.FragmentMainBinding
import com.google.gson.Gson
import java.io.File

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private lateinit var gridLayout: GridLayout
    private lateinit var currentTextViews: List<TextView>
    private var currentGuess = 0
    private val maxGuesses = 5
    private var targetWord = ""
    private var isGameOver = false

    private lateinit var sharedPreferences: SharedPreferences
    private val prefsName = "settings_prefs"

    private val wordsList = arrayOf(
        "ВЕТЕР", "ЗВЕЗД", "СЛОВО", "ЧАЙКА", "ПАНДА",
        "ПТИЦА", "ФИРМА", "КРЫША", "ЛУЧИК", "МОРЕЙ",
        "СЛОНА", "КРАСА", "ДОРОГ", "ТЕПЛО", "ШКОЛА",
        "ПЕРЕМ", "КАМЕН", "ГРУДЬ", "ПЕНЬК", "ПЕСОК",
        "КНИГА", "ЛАМПА", "ЗЕРНО", "ДРУЖА", "ДЫМКА",
        "ШАЛЬК", "СЧАСТ", "ЛИМОН", "ТЕНИК", "ГОЛОД",
        "ОКНОТ", "РЕКАМ", "СНЕГИ", "САМОЧ", "ПОДРУ",
        "КОРАМ", "СОЦЬК", "МУЗЫК", "ПЛАЧИ", "СЛУЖА"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root

        sharedPreferences = requireActivity().getSharedPreferences(prefsName, AppCompatActivity.MODE_PRIVATE)

        gridLayout = view.findViewById(R.id.grid_letters)
        currentTextViews = createGrid()

        loadBackgroundColor(view)
        selectRandomWord()

        createKeyboard(view)

        return view
    }

    private fun selectRandomWord() {
        targetWord = wordsList.random()
        Log.i("Test", targetWord)
    }

    private fun loadBackgroundColor(view: View) {
        val backgroundColor = sharedPreferences.getInt("background_color", Color.WHITE)
        view.setBackgroundColor(backgroundColor)
    }

    private fun createGrid(): List<TextView> {
        val textViews = mutableListOf<TextView>()
        val rows = maxGuesses
        val columns = 5

        gridLayout.rowCount = rows
        gridLayout.columnCount = columns

        for (i in 0 until rows * columns) {
            val textView = TextView(context).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = 150
                    rowSpec = GridLayout.spec(i / columns, 1f)
                    columnSpec = GridLayout.spec(i % columns, 1f)
                    setMargins(8, 8, 8, 8)
                }
                text = ""
                setBackgroundResource(R.drawable.rounded_background)
                setTextColor(resources.getColor(R.color.black, null))
                gravity = Gravity.CENTER
                setTextSize(18f)
                setPadding(16, 16, 16, 16)
            }
            gridLayout.addView(textView)
            textViews.add(textView)
        }
        return textViews
    }

    private fun createKeyboard(view: View) {
        val keyboardLayout: GridLayout = view.findViewById(R.id.keyboard_layout)
        val letters = listOf("Й", "Ц", "У", "К", "Е", "Н", "Г", "Ш", "Щ", "З", "Х", "Ъ",
            "Ф", "Ы", "В", "А", "П", "Р", "О", "Л", "Д", "Ж", "Э", "Я", "Ч", "С", "М", "И", "Т", "Ь", "Б", "Ю")

        for (i in letters.indices) {
            val button = Button(context).apply {
                text = letters[i]
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = 150
                    rowSpec = GridLayout.spec(i / 8, 1f)
                    columnSpec = GridLayout.spec(i % 8, 1f)
                    setMargins(2, 2, 2, 2)
                }
                setOnClickListener { fillCellWithLetter(text.toString()) }
            }
            keyboardLayout.addView(button)
        }

        val deleteButton = Button(context).apply {
            text = "⬅️"
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = 150
                rowSpec = GridLayout.spec(3, 1f)
                columnSpec = GridLayout.spec(7, 1f)
                setMargins(8, 8, 8, 8)
            }
            setOnClickListener { deleteLastLetter() }
        }
        keyboardLayout.addView(deleteButton)

        val confirmButton = Button(context).apply {
            text = "✔️"
            layoutParams = GridLayout.LayoutParams().apply {
                width = 0
                height = 150
                rowSpec = GridLayout.spec(3, 1f)
                columnSpec = GridLayout.spec(6, 1f)
                setMargins(2, 2, 2, 2)
            }
            setOnClickListener { confirmSelection() }
        }
        keyboardLayout.addView(confirmButton)
    }

    private fun writeStatsToFile(stats: GameStats) {
        val json = Gson().toJson(stats)
        val file = File(requireContext().filesDir, "game_stats.json")

        file.writeText(json)
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

    private fun confirmSelection() {
        if (isGameOver || currentGuess >= maxGuesses) return

        val startIndex = currentGuess * 5
        val guessedWord = currentTextViews.subList(startIndex, startIndex + 5).joinToString("") { it.text.toString() }

        if (guessedWord.length < 5) {
            return
        }

        checkGuess(guessedWord)
        currentGuess++

        val stats = readStatsFromFile()

        if (guessedWord == targetWord) {
            isGameOver = true
            stats.wins++
            Toast.makeText(context, "Поздравляем! Вы угадали слово!", Toast.LENGTH_LONG).show()
            createGrid()
        } else if (currentGuess >= maxGuesses) {
            isGameOver = true
            stats.losses++
            Toast.makeText(context, "Игра окончена! Правильное слово: $targetWord", Toast.LENGTH_LONG).show()
            createGrid()
        }

        writeStatsToFile(stats)
    }

    private fun setRoundedBackground(textView: TextView) {
        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(Color.WHITE)
            cornerRadius = 16f
        }
        textView.background = drawable
    }

    private fun checkGuess(guessedWord: String) {
        for (i in guessedWord.indices) {
            val textView = currentTextViews[currentGuess * 5 + i]

            setRoundedBackground(textView)

            when {
                guessedWord[i] == targetWord[i] -> {
                    animateColorChange(textView, Color.GREEN)
                }
                targetWord.contains(guessedWord[i]) -> {
                    animateColorChange(textView, Color.YELLOW)
                }
                else -> {
                    animateColorChange(textView, Color.RED)
                }
            }
        }
    }

    private fun animateColorChange(textView: TextView, color: Int) {
        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(color)
            cornerRadius = 44f
        }
        textView.background = drawable

        val colorAnimation = ObjectAnimator.ofArgb(
            drawable,
            "color",
            Color.TRANSPARENT,
            color
        )
        colorAnimation.duration = 500
        colorAnimation.start()
    }

    private fun fillCellWithLetter(letter: String) {
        if (isGameOver || currentGuess >= maxGuesses) return

        for (i in 0 until 5) {
            val textView = currentTextViews[currentGuess * 5 + i]
            if (textView.text.isEmpty()) {
                textView.text = letter
                animateTextView(textView)
                fadeInTextView(textView)
                return
            }
        }
    }

    private fun fadeInTextView(textView: TextView) {
        textView.alpha = 0f
        textView.visibility = View.VISIBLE

        val fadeIn = ObjectAnimator.ofFloat(textView, "alpha", 0f, 1f)
        fadeIn.duration = 300
        fadeIn.start()
    }

    private fun animateTextView(textView: TextView) {
        val scaleUp = ObjectAnimator.ofPropertyValuesHolder(
            textView,
            PropertyValuesHolder.ofFloat("scaleX", 1.2f),
            PropertyValuesHolder.ofFloat("scaleY", 1.2f)
        )
        scaleUp.duration = 150

        val scaleDown = ObjectAnimator.ofPropertyValuesHolder(
            textView,
            PropertyValuesHolder.ofFloat("scaleX", 1f),
            PropertyValuesHolder.ofFloat("scaleY", 1f)
        )
        scaleDown.duration = 150

        val animatorSet = AnimatorSet()
        animatorSet.play(scaleUp).before(scaleDown)
        animatorSet.start()
    }

    private fun deleteLastLetter() {
        if (isGameOver || currentGuess >= maxGuesses) return

        for (i in 4 downTo 0) {
            val textView = currentTextViews[currentGuess * 5 + i]
            if (textView.text.isNotEmpty()) {
                textView.text = ""
                return
            }
        }
    }
}