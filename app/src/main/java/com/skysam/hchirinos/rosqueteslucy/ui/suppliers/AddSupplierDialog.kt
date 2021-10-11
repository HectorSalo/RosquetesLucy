package com.skysam.hchirinos.rosqueteslucy.ui.suppliers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.Keyboard
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Supplier
import com.skysam.hchirinos.rosqueteslucy.databinding.DialogAddCostumerBinding

/**
 * Created by Hector Chirinos (Home) on 30/9/2021.
 */
class AddSupplierDialog: DialogFragment() {
    private var _binding: DialogAddCostumerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SuppliersViewModel by activityViewModels()
    private val suppliers = mutableListOf<Supplier>()

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
        binding.tfIdCostumer.visibility = View.GONE
        binding.etLocationCostumer.doAfterTextChanged { binding.tfLocationCostumer.error = null }
        binding.tfAddressCostumer.visibility = View.GONE
        binding.spinner.visibility =View.GONE
        binding.btnSave.setOnClickListener { validateCostumer() }
        binding.btnCancel.setOnClickListener { dialog?.dismiss() }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun validateCostumer() {
        binding.tfNameCostumer.error = null
        binding.tfLocationCostumer.error = null

        val name = binding.etNameCostumer.text.toString()
        if (name.isEmpty()) {
            binding.tfNameCostumer.error = getString(R.string.error_field_empty)
            binding.etNameCostumer.requestFocus()
            return
        }
        val location = binding.etLocationCostumer.text.toString()
        if (location.isEmpty()) {
            binding.tfLocationCostumer.error = getString(R.string.error_field_empty)
            binding.etLocationCostumer.requestFocus()
            return
        }
        var supplierExists = false
        for (supplier in suppliers) {
            if (supplier.name == name) {
                binding.tfNameCostumer.error = getString(R.string.error_costumer_exists)
                binding.etNameCostumer.requestFocus()
                supplierExists = true
                break
            }
        }
        if (supplierExists) return

        val locations = mutableListOf<String>()
        locations.add(location)

        Keyboard.close(binding.root)
        val supplier = Supplier("", name, locations)
        viewModel.addSupplier(supplier)
        Toast.makeText(requireContext(), getString(R.string.text_saving), Toast.LENGTH_SHORT).show()
        dialog?.dismiss()
    }
}