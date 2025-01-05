package com.example.wordlebyshik.fragments

import android.animation.ObjectAnimator
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.wordlebyshik.R

class HowToPlayFragment : Fragment() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var gridLayout: GridLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_how_to_play, container, false)

        sharedPreferences = requireActivity().getSharedPreferences("settings_prefs", AppCompatActivity.MODE_PRIVATE)

        val backgroundColor = sharedPreferences.getInt("background_color", Color.WHITE)
        view.setBackgroundColor(backgroundColor)

        gridLayout = view.findViewById(R.id.grid_letters)

        createGrid()

        return view
    }

    private fun createGrid(): List<TextView> {
        val textViews = mutableListOf<TextView>()
        val rows = 1
        val columns = 5

        gridLayout.rowCount = rows
        gridLayout.columnCount = columns

        val letters = listOf("Ц", "Ы", "Г", "А", "Н")

        for (i in 0 until columns) {
            val textView = TextView(context).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = 150
                    rowSpec = GridLayout.spec(0, 1f)
                    columnSpec = GridLayout.spec(i, 1f)
                    setMargins(8, 8, 8, 8)
                }
                text = letters[i]
                setTextColor(resources.getColor(R.color.black, null))
                gravity = Gravity.CENTER
                setTextSize(18f)
                setPadding(16, 16, 16, 16)

                when (i) {
                    0, 1, 2 -> animateColorChange(this, Color.GREEN)
                    3 -> animateColorChange(this, Color.YELLOW)
                    4 -> animateColorChange(this, Color.RED)
                }
            }
            gridLayout.addView(textView)
            textViews.add(textView)
        }
        return textViews
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
}