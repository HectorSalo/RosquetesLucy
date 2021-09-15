package com.skysam.hchirinos.rosqueteslucy.ui.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.NavHostFragment
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import androidx.preference.SwitchPreferenceCompat
import com.skysam.hchirinos.rosqueteslucy.BuildConfig
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.Constants
import com.skysam.hchirinos.rosqueteslucy.database.SharedPref


class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        setHasOptionsMenu(true)

        val listDaysExpired = findPreference<ListPreference>("days_expired")
        listDaysExpired?.value = SharedPref.getDaysExpired().toString()

        listDaysExpired?.setOnPreferenceChangeListener { _, newValue ->
            val days = newValue as String
            SharedPref.changeDaysExpired(days.toInt())
            true
        }

        val switchLock = findPreference<SwitchPreferenceCompat>(Constants.PREFERENCES_LOCK)
        val screenChangePin = findPreference<PreferenceScreen>(Constants.PREFERENCES_PIN_LOCK)
        if (SharedPref.isLock()) {
            switchLock?.isChecked = true
            switchLock?.setIcon(R.drawable.ic_lock_24)
            screenChangePin?.isVisible = true
        } else {
            switchLock?.isChecked = false
            switchLock?.setIcon(R.drawable.ic_lock_open_24)
            screenChangePin?.isVisible = false
        }

        switchLock?.setOnPreferenceChangeListener { _, newValue ->
            val isOn = newValue as Boolean
            if (isOn) {
                //switchLock.setIcon(R.drawable.ic_lock_24)
                val pinDialog = PinDialog()
                pinDialog.show(requireActivity().supportFragmentManager, tag)
            } else {
                switchLock.setIcon(R.drawable.ic_lock_open_24)
            }
            true
        }

        val aboutPreference: PreferenceScreen = findPreference("about")!!
        aboutPreference.setOnPreferenceClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_nav_home_settings_to_nav_about)
            true
        }

        val versionPreferenceScreen = findPreference<PreferenceScreen>("name_version")
        versionPreferenceScreen?.title = getString(R.string.version_name, BuildConfig.VERSION_NAME)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            requireActivity().finish()
        }
        return super.onOptionsItemSelected(item)
    }
}