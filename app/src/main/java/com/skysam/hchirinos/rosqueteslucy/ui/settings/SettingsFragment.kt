package com.skysam.hchirinos.rosqueteslucy.ui.settings

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceScreen
import androidx.preference.SwitchPreferenceCompat
import com.skysam.hchirinos.rosqueteslucy.BuildConfig
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.Constants
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Sale
import com.skysam.hchirinos.rosqueteslucy.database.SharedPref
import com.skysam.hchirinos.rosqueteslucy.database.repositories.InitSession
import com.skysam.hchirinos.rosqueteslucy.ui.initSession.LoginActivity


class SettingsFragment : PreferenceFragmentCompat() {

    private val viewModel: SettingsViewModel by activityViewModels()
    private var switchLock: SwitchPreferenceCompat? = null
    private var screenChangePin: PreferenceScreen? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val listDaysExpired = findPreference<ListPreference>("days_expired")
        listDaysExpired?.value = SharedPref.getDaysExpired().toString()

        listDaysExpired?.setOnPreferenceChangeListener { _, newValue ->
            val days = newValue as String
            SharedPref.changeDaysExpired(days.toInt())
            true
        }

        switchLock = findPreference(Constants.PREFERENCES_LOCK)
        screenChangePin = findPreference(Constants.PREFERENCES_PIN_LOCK)

        viewModel.lockActived.observe(viewLifecycleOwner, {
            if (it) {
                switchLock?.isChecked = true
                switchLock?.setIcon(R.drawable.ic_lock_24)
                screenChangePin?.isVisible = true
            } else {
                switchLock?.isChecked = false
                switchLock?.setIcon(R.drawable.ic_lock_open_24)
                screenChangePin?.isVisible = false
            }
        })

        switchLock?.setOnPreferenceChangeListener { _, newValue ->
            val isOn = newValue as Boolean
            if (isOn) {
                val pinDialog = PinDialog(false)
                pinDialog.show(requireActivity().supportFragmentManager, tag)
            } else {
                viewModel.changeLockState(false)
            }
            true
        }

        screenChangePin?.setOnPreferenceClickListener {
            val pinDialog = PinDialog(true)
            pinDialog.show(requireActivity().supportFragmentManager, tag)
            true
        }

        val aboutPreference: PreferenceScreen = findPreference("about")!!
        aboutPreference.setOnPreferenceClickListener {
            NavHostFragment.findNavController(this)
                .navigate(R.id.action_nav_home_settings_to_nav_about)
            true
        }

        val signOutPreference: PreferenceScreen = findPreference("signOut")!!
        signOutPreference.setOnPreferenceClickListener {
            signOut()
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

    private fun signOut() {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_sign_out))
            .setMessage(getString(R.string.message_sign_out))
            .setPositiveButton(R.string.title_sign_out) { _, _ ->
                InitSession.signOut()
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()
            }
            .setNegativeButton(R.string.btn_cancel, null)

        val dialog = builder.create()
        dialog.show()
    }
}