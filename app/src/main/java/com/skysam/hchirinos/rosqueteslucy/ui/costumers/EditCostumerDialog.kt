package com.skysam.hchirinos.rosqueteslucy.ui.costumers

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.Keyboard
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Customer
import com.skysam.hchirinos.rosqueteslucy.databinding.DialogAddCostumerBinding

/**
 * Created by Hector Chirinos (Home) on 3/8/2021.
 */
class EditCostumerDialog(private val customer: Customer): DialogFragment() {
    private var _binding: DialogAddCostumerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CostumersViewModel by activityViewModels()
    private val costumers = mutableListOf<Customer>()
    private lateinit var buttonPositive: Button
    private lateinit var buttonNegative: Button

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddCostumerBinding.inflate(layoutInflater)

        val listUnits = listOf(*resources.getStringArray(R.array.identificador))
        val adapterUnits = ArrayAdapter(requireContext(), R.layout.layout_spinner, listUnits)
        binding.spinner.adapter = adapterUnits
        binding.etNameCostumer.setText(customer.name)
        val values: List<String> = customer.identifier.split("-")
        val symbolIdentifier = "${values[0]}-"
        val numberIdentifier = values[1]
        when(symbolIdentifier) {
            "J-" -> binding.spinner.setSelection(0)
            "V-" -> binding.spinner.setSelection(1)
            "E-" -> binding.spinner.setSelection(2)
            "G-" -> binding.spinner.setSelection(3)
        }
        binding.etAddressCostumer.setText(customer.address)
        binding.etIdCostumer.setText(numberIdentifier)
        binding.tfLocationCostumer.visibility = View.GONE
        binding.btnCancel.visibility = View.GONE
        binding.btnSave.visibility = View.GONE

        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_edit_costumer))
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
            }
        })
    }

    private fun validateCostumer() {
        binding.tfNameCostumer.error = null
        binding.tfIdCostumer.error = null

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
        val address = binding.etAddressCostumer.text.toString()
        if (address.isEmpty()) {
            binding.tfAddressCostumer.error = getString(R.string.error_field_empty)
            binding.etAddressCostumer.requestFocus()
            return
        }
        val identifier = "${binding.spinner.selectedItem}$rif"
        if (name != customer.name) {
            var costumerExists = false
            for (cos in costumers) {
                if (cos.name == name) {
                    binding.tfNameCostumer.error = getString(R.string.error_costumer_exists)
                    binding.etNameCostumer.requestFocus()
                    costumerExists = true
                    break
                }
            }
            if (costumerExists) return
        }
        if (identifier != customer.identifier) {
            var identifierExists = false
            for (cos in costumers) {
                if (cos.identifier == identifier) {
                    if (cos.name != name) {
                        binding.tfIdCostumer.error = getString(R.string.error_identifier_exists)
                        binding.etIdCostumer.requestFocus()
                        identifierExists = true
                        break
                    }
                }
            }
            if (identifierExists) return
        }

        Keyboard.close(binding.root)
        val costumerUpdate = Customer(customer.id, name, identifier, address, customer.locations)
        viewModel.editCostumer(costumerUpdate)
        Toast.makeText(requireContext(), getString(R.string.text_saving), Toast.LENGTH_SHORT).show()
        dialog?.dismiss()
    }
}