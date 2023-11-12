package com.matiasmandelbaum.alejandriaapp.ui.settings

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.matiasmandelbaum.alejandriaapp.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val sharedPreferences = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("darkMode", false)

        val darkModeSwitchPref = findPreference<SwitchPreferenceCompat>("darkModeSwitch")
        darkModeSwitchPref?.isChecked = isDarkMode

        darkModeSwitchPref?.setOnPreferenceChangeListener { preference: Preference, newValue: Any ->
            val isDarkModeOn = newValue as Boolean
            if (isDarkModeOn) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            val editor = sharedPreferences.edit()
            editor.putBoolean("darkMode", isDarkModeOn)
            editor.apply()

            true
        }
    }
}