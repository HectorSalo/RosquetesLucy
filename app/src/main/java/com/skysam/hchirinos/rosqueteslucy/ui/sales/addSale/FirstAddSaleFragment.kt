package com.skysam.hchirinos.rosqueteslucy.ui.sales.addSale

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.Keyboard
import com.skysam.hchirinos.rosqueteslucy.common.classView.ExitDialog
import com.skysam.hchirinos.rosqueteslucy.common.classView.OnClickExit
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Costumer
import com.skysam.hchirinos.rosqueteslucy.databinding.FragmentFirstAddSaleBinding
import com.skysam.hchirinos.rosqueteslucy.ui.sales.SalesViewModel
import java.text.DateFormat
import java.util.*

class FirstAddSaleFragment : Fragment(), OnClickExit {

    private var _binding: FragmentFirstAddSaleBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SalesViewModel by activityViewModels()
    private val costumersLocation = mutableListOf<String>()
    private val costumers = mutableListOf<Costumer>()
    private lateinit var costumer: Costumer
    private lateinit var location: String
    private var dateSelected: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstAddSaleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                getOut()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        binding.etNameCostumer.onItemClickListener = AdapterView.OnItemClickListener { parent, _, position, _ ->
            Keyboard.close(binding.root)
            val costumerSelected = parent.getItemAtPosition(position).toString()
            val values: List<String> = costumerSelected.split("-")
            val name = values[0]
            val locationS = values[1]

            for (cos in costumers) {
                if (cos.name == name) {
                    costumer = cos
                    for (loc in cos.locations) {
                        if (loc == locationS) {
                            location = loc
                        }
                    }
                }
            }
        }

        binding.etDate.setOnClickListener { selecDate() }
        binding.btnExit.setOnClickListener { getOut() }
        binding.btnTotal.setOnClickListener { validateData() }

        loadViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getOut() {
        val exitDialog = ExitDialog(this)
        exitDialog.show(requireActivity().supportFragmentManager, tag)
    }

    override fun onClickExit() {
        requireActivity().finish()
    }

    private fun loadViewModel() {
        viewModel.costumers.observe(viewLifecycleOwner, {
            if (_binding != null) {
                if (it.isNotEmpty()) {
                    costumersLocation.clear()
                    costumers.clear()
                    costumers.addAll(it)
                    for (cos in it) {
                        for (i in cos.locations.indices) {
                            val location = "${cos.name}-${cos.locations[i]}"
                            costumersLocation.add(location)
                        }
                    }
                    val adapterSearchProduct = ArrayAdapter(requireContext(), R.layout.list_autocomplete_text, costumersLocation.sorted())
                    binding.etNameCostumer.setAdapter(adapterSearchProduct)
                }
            }
        })
    }

    private fun validateData() {
        binding.tfNameCostumer.error = null
        binding.tfPrice.error = null
        binding.tfQuantity.error = null
        binding.tfDate.error = null
        binding.tfInvoice.error = null

        val costumerName = binding.etNameCostumer.text.toString()
        if (costumerName.isEmpty()) {
            binding.tfNameCostumer.error = getString(R.string.error_field_empty)
            binding.etNameCostumer.requestFocus()
            return
        }
        val price = binding.etPrice.text.toString()
        if (price.isEmpty()) {
            binding.tfPrice.error = getString(R.string.error_field_empty)
            binding.etPrice.requestFocus()
            return
        }
        val quantity = binding.etQuantity.text.toString()
        if (quantity.isEmpty()) {
            binding.tfQuantity.error = getString(R.string.error_field_empty)
            binding.etQuantity.requestFocus()
            return
        }
        val dateSelectedS = binding.etDate.text.toString()
        if (dateSelectedS.isEmpty()) {
            binding.tfDate.error = getString(R.string.error_field_empty)
            binding.etDate.requestFocus()
            return
        }
        val invoice = binding.etInvoice.text.toString()
        if (invoice.isEmpty()) {
            binding.tfInvoice.error = getString(R.string.error_field_empty)
            binding.etInvoice.requestFocus()
            return
        }

        viewModel.reviewInvoice(costumer, location, price.toDouble(), quantity.toInt(),
            binding.rbDolar.isChecked, invoice.toInt(), binding.rbPaidYes.isChecked, dateSelected)
        findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
    }

    private fun selecDate() {
        val builder = MaterialDatePicker.Builder.datePicker()
        val calendar = Calendar.getInstance()
        val picker = builder.build()
        picker.addOnPositiveButtonClickListener { selection: Long? ->
            calendar.timeInMillis = selection!!
            val dateSelec = calendar.time
            dateSelected = dateSelec.time
            binding.etDate.setText(DateFormat.getDateInstance().format(dateSelected))
        }
        picker.show(requireActivity().supportFragmentManager, picker.toString())
    }
}