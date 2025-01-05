package com.example.wordlebyshik.fragments

import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.wordlebyshik.MainActivity
import com.example.wordlebyshik.R
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SettingsFragment : Fragment() {

    private lateinit var musicSwitch: Switch
    private lateinit var colorPickerButton: Button

    private lateinit var sharedPreferences: SharedPreferences
    private val prefsName = "settings_prefs"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

        musicSwitch = view.findViewById(R.id.music_switch)
        colorPickerButton = view.findViewById(R.id.color_picker_button)

        sharedPreferences = requireActivity().getSharedPreferences(prefsName, AppCompatActivity.MODE_PRIVATE)

        loadSettings(view)

        musicSwitch.setOnCheckedChangeListener { _, isChecked ->
            saveMusicSetting(isChecked)
        }

        colorPickerButton.setOnClickListener {
            openColorPicker()
        }

        return view
    }

    private fun loadSettings(view: View) {
        val isMusicEnabled = sharedPreferences.getBoolean("music_enabled", true)
        val backgroundColor = sharedPreferences.getInt("background_color", Color.WHITE)

        musicSwitch.isChecked = isMusicEnabled
        view.setBackgroundColor(backgroundColor)
    }

    private fun saveMusicSetting(isEnabled: Boolean) {
        with(sharedPreferences.edit()) {
            putBoolean("music_enabled", isEnabled)
            apply()
        }
        (activity as MainActivity).startMusic(isEnabled)
    }

    private fun openColorPicker() {
        val colorOptions = arrayOf(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.WHITE, Color.parseColor("#CD0074"))

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Выберите цвет фона")
            .setItems(arrayOf("Красный", "Зеленый", "Синий", "Желтый", "Белый", "Дефолтный")) { _, which ->
                val selectedColor = colorOptions[which]
                saveBackgroundColor(selectedColor)
            }
            .show()
    }

    private fun saveBackgroundColor(color: Int) {
        with(sharedPreferences.edit()) {
            putInt("background_color", color)
            apply()
        }
        view?.setBackgroundColor(color)
    }
}