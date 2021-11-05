package com.skysam.hchirinos.rosqueteslucy.ui.costumers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.Keyboard
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Costumer
import com.skysam.hchirinos.rosqueteslucy.databinding.DialogAddCostumerBinding


/**
 * Created by Hector Chirinos (Home) on 1/8/2021.
 */
class AddCustomerDialog: DialogFragment() {
    private var _binding: DialogAddCostumerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CostumersViewModel by activityViewModels()
    private val costumers = mutableListOf<Costumer>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ShapeAppearanceOverlay_MaterialComponents_MaterialCalendar_Window_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddCostumerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etNameCostumer.doAfterTextChanged { binding.tfNameCostumer.error = null }
        binding.etIdCostumer.doAfterTextChanged { binding.tfIdCostumer.error = null }
        binding.etLocationCostumer.doAfterTextChanged { binding.tfLocationCostumer.error = null }
        binding.etAddressCostumer.doAfterTextChanged { binding.tfAddressCostumer.error = null }
        val listUnits = listOf(*resources.getStringArray(R.array.identificador))
        val adapterUnits = ArrayAdapter(requireContext(), R.layout.layout_spinner, listUnits)
        binding.spinner.adapter = adapterUnits
        binding.btnSave.setOnClickListener { validateCostumer() }
        binding.btnCancel.setOnClickListener { dialog?.dismiss() }

        loadViewModel()
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
        binding.tfLocationCostumer.error = null
        binding.etAddressCostumer.error = null

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
        val address = binding.etAddressCostumer.text.toString()
        if (address.isEmpty()) {
            binding.tfAddressCostumer.error = getString(R.string.error_field_empty)
            binding.etAddressCostumer.requestFocus()
            return
        }
        val location = binding.etLocationCostumer.text.toString()
        if (location.isEmpty()) {
            binding.tfLocationCostumer.error = getString(R.string.error_field_empty)
            binding.etLocationCostumer.requestFocus()
            return
        }
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
        val locations = mutableListOf<String>()
        locations.add(location)

        Keyboard.close(binding.root)
        val costumer = Costumer("", name, identifier, address, locations)
        viewModel.addCostumer(costumer)
        Toast.makeText(requireContext(), getString(R.string.text_saving), Toast.LENGTH_SHORT).show()
        dialog?.dismiss()
    }
}