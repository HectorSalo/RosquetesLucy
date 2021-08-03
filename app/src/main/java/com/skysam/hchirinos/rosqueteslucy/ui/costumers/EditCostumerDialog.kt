package com.skysam.hchirinos.rosqueteslucy.ui.costumers

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Costumer
import com.skysam.hchirinos.rosqueteslucy.databinding.DialogAddCostumerBinding

/**
 * Created by Hector Chirinos (Home) on 3/8/2021.
 */
class EditCostumerDialog(private val costumer: Costumer): DialogFragment() {
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
        binding.etNameCostumer.setText(costumer.name)
        val values: List<String> = costumer.identifier.split("-")
        val symbolIdentifier = "${values[0]}-"
        val numberIdentifier = values[1]
        when(symbolIdentifier) {
            "J-" -> binding.spinner.setSelection(0)
            "V-" -> binding.spinner.setSelection(1)
            "E-" -> binding.spinner.setSelection(2)
            "G-" -> binding.spinner.setSelection(3)
        }
        binding.etIdCostumer.setText(numberIdentifier)
        binding.etLocationCostumer.setText(costumer.location)

        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_edit_costumer))
            .setView(binding.root)
            .setPositiveButton(R.string.text_edit, null)
            .setNegativeButton(R.string.btn_cancel, null)

        val dialog = builder.create()
        dialog.show()

        loadViewModel()

        buttonNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        buttonNegative.setOnClickListener { dialog.dismiss() }
        buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        //buttonPositive.setOnClickListener { validateCostumer() }

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
}