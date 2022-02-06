package com.skysam.hchirinos.rosqueteslucy.ui.settings

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.Keyboard
import com.skysam.hchirinos.rosqueteslucy.database.SharedPref
import com.skysam.hchirinos.rosqueteslucy.databinding.DialogPinSettingsBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Created by Hector Chirinos on 15/09/2021.
 */
class PinDialog(private val isPinChange: Boolean): DialogFragment() {
    private var _binding: DialogPinSettingsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SettingsViewModel by activityViewModels()
    private lateinit var buttonPositive: Button
    private lateinit var buttonNegative: Button

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogPinSettingsBinding.inflate(layoutInflater)

        binding.etPin.doAfterTextChanged { binding.tfPin.error = null }
        binding.etPinRepetir.doAfterTextChanged { binding.tfRepetirPin.error = null }

        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(R.string.lock_title)
            .setView(binding.root)
            .setPositiveButton(R.string.btn_save, null)
            .setNegativeButton(R.string.btn_cancel, null)

        val dialog = builder.create()
        dialog.show()
        dialog.setCanceledOnTouchOutside(false)

        buttonNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)

        buttonPositive.setOnClickListener {
            validatePinNew()
        }

        buttonNegative.setOnClickListener {
            if (!isPinChange) viewModel.changeLockState(false)
            dismiss()
        }

        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun validatePinNew() {
        binding.tfPin.error = null
        binding.tfRepetirPin.error = null
        val pin = binding.etPin.text.toString()
        if (pin.isEmpty()) {
            binding.tfPin.error = getString(R.string.error_field_empty)
            binding.etPin.requestFocus()
            return
        }
        if (pin.length < 4) {
            binding.tfPin.error = getString(R.string.error_pin_length)
            binding.etPin.requestFocus()
            return
        }
        val pinRepeat = binding.etPinRepetir.text.toString()
        if (pinRepeat.isEmpty()) {
            binding.tfRepetirPin.error = getString(R.string.error_field_empty)
            binding.etPinRepetir.requestFocus()
            return
        }
        if (pin != pinRepeat) {
            binding.tfRepetirPin.error = getString(R.string.error_pin_match)
            binding.etPinRepetir.requestFocus()
            return
        }
        Keyboard.close(binding.root)
        SharedPref.changeLock(true)
        SharedPref.changePinLock(pin)
        viewModel.changeLockState(true)

        buttonPositive.visibility = View.GONE
        buttonNegative.visibility = View.GONE
        binding.linearLayout.visibility = View.GONE
        binding.lottieAnimationView.visibility = View.VISIBLE
        binding.lottieAnimationView.playAnimation()

        lifecycleScope.launch {
            delay(2500)
            dialog?.dismiss()
        }
    }
}