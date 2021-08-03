package com.skysam.hchirinos.rosqueteslucy.ui.costumers

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.Keyboard
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Costumer
import com.skysam.hchirinos.rosqueteslucy.databinding.DialogAddCostumerBinding

/**
 * Created by Hector Chirinos (Home) on 1/8/2021.
 */
class AddCostumerDialog: DialogFragment() {
    private var _binding: DialogAddCostumerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CostumersViewModel by activityViewModels()
    private val costumers = mutableListOf<Costumer>()
    private lateinit var buttonPositive: Button
    private lateinit var buttonNegative: Button

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddCostumerBinding.inflate(layoutInflater)

        val listUnits = listOf(*resources.getStringArray(R.array.identificador))
        val adapterUnits = ArrayAdapter(requireContext(), R.layout.layout_spinner, listUnits)
        binding.spinner.adapter = adapterUnits

        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_add_costumer))
            .setView(binding.root)
            .setPositiveButton(R.string.btn_save, null)
            .setNegativeButton(R.string.btn_cancel, null)

        val dialog = builder.create()
        dialog.show()

        loadViewModel()

        buttonNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        buttonNegative.setOnClickListener { dialog.dismiss() }
        buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        buttonPositive.setOnClickListener { validateCostumer() }

        binding.etNameCostumer.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            Keyboard.close(binding.root)
            val nameSelected = parent.getItemAtPosition(position)
            var symbolIdentifier = ""
            var numberIdentifier = ""
            for (cos in costumers) {
                if (cos.name == nameSelected) {
                    val values: List<String> = cos.identifier.split("-")
                    symbolIdentifier = "${values[0]}-"
                    numberIdentifier = values[1]
                }
            }
            when(symbolIdentifier) {
                "J-" -> binding.spinner.setSelection(0)
                "V-" -> binding.spinner.setSelection(1)
                "E-" -> binding.spinner.setSelection(2)
                "G-" -> binding.spinner.setSelection(3)
            }
            binding.etIdCostumer.setText(numberIdentifier)
        }

        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadViewModel() {
        viewModel.costumers.observe(this.requireActivity(), {
            if (_binding != null) {
                costumers.clear()
                costumers.addAll(it)
                val names = mutableListOf<String>()
                for (cos in costumers) {
                    if (!names.contains(cos.name)) {
                        names.add(cos.name)
                    }
                }
                val adapterAutoComplete = ArrayAdapter(requireContext(), R.layout.list_autocomplete_text, names)
                binding.etNameCostumer.setAdapter(adapterAutoComplete)
            }
        })
    }

    private fun validateCostumer() {
        binding.tfNameCostumer.error = null
        binding.tfIdCostumer.error = null
        binding.tfLocationCostumer.error = null

        val name = binding.etNameCostumer.text.toString()
        if (name.isEmpty()) {
            binding.tfNameCostumer.error = getString(R.string.error_field_empty)
            binding.etNameCostumer.requestFocus()
            return
        }
        val rif = binding.etIdCostumer.text.toString()
        if (rif.isEmpty()) {
            binding.tfIdCostumer.error = getString(R.string.error_field_empty)
            binding.etIdCostumer.requestFocus()
            return
        }
        val identifier = "${binding.spinner.selectedItem}$rif"
        val location = binding.etLocationCostumer.text.toString()
        if (location.isEmpty()) {
            binding.tfLocationCostumer.error = getString(R.string.error_field_empty)
            binding.etLocationCostumer.requestFocus()
            return
        }
        var costumerExists = false
        for (cos in costumers) {
            if (cos.name == name) {
                if (cos.identifier != identifier) {
                    binding.tfNameCostumer.error = getString(R.string.error_costumer_exists)
                    costumerExists = true
                    break
                }
            }
        }
        if (costumerExists) return
        var identifierExists = false
        for (cos in costumers) {
            if (cos.identifier == identifier) {
                if (cos.name != name) {
                    binding.tfIdCostumer.error = getString(R.string.error_identifier_exists)
                    identifierExists = true
                    break
                }
            }
        }
        if (identifierExists) return
        var locationExists = false
        for (cos in costumers) {
            if (cos.name == name && cos.identifier == identifier) {
                if (cos.location == location) {
                    binding.tfLocationCostumer.error = getString(R.string.error_location_exists)
                    locationExists = true
                    break
                }
            }
        }
        if (locationExists) return

        Keyboard.close(binding.root)
        val costumer = Costumer("", name, identifier, location)
        viewModel.addCostumer(costumer)
        Toast.makeText(requireContext(), getString(R.string.text_saving), Toast.LENGTH_SHORT).show()
        dialog?.dismiss()
    }
}