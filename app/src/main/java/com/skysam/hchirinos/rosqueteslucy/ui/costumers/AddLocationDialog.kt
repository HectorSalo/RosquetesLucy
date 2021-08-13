package com.skysam.hchirinos.rosqueteslucy.ui.costumers

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.Keyboard
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Costumer
import com.skysam.hchirinos.rosqueteslucy.databinding.DialogAddLocationBinding

/**
 * Created by Hector Chirinos (Home) on 11/8/2021.
 */
class AddLocationDialog(private val costumer: Costumer): DialogFragment() {
    private var _binding: DialogAddLocationBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CostumersViewModel by activityViewModels()
    private lateinit var buttonPositive: Button
    private lateinit var buttonNegative: Button

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddLocationBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.text_add_location))
            .setView(binding.root)
            .setPositiveButton(R.string.btn_save, null)
            .setNegativeButton(R.string.btn_cancel, null)

        val dialog = builder.create()
        dialog.show()

        buttonNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        buttonNegative.setOnClickListener { dialog.dismiss() }
        buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        buttonPositive.setOnClickListener { validateLocation() }

        return dialog
    }

    private fun validateLocation() {
        binding.tfLocationCostumer.error = null

        val location = binding.etLocationCostumer.text.toString()
        if (location.isEmpty()) {
            binding.tfLocationCostumer.error = getString(R.string.error_field_empty)
            binding.etLocationCostumer.requestFocus()
            return
        }
        var locationExists = false
        for (loc in costumer.locations) {
            if (loc.name == location) {
                binding.tfLocationCostumer.error = getString(R.string.error_location_exists)
                binding.etLocationCostumer.requestFocus()
                locationExists = true
                break
            }
        }
        if (locationExists) return

        Keyboard.close(binding.root)
        viewModel.addLocation(costumer.id, location)
        Toast.makeText(requireContext(), getString(R.string.text_saving), Toast.LENGTH_SHORT).show()
        dialog?.dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}