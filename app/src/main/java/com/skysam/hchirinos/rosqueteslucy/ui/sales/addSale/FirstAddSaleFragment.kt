package com.skysam.hchirinos.rosqueteslucy.ui.sales.addSale

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.classView.ExitDialog
import com.skysam.hchirinos.rosqueteslucy.common.classView.OnClickExit
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Costumer
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Sale
import com.skysam.hchirinos.rosqueteslucy.databinding.FragmentFirstAddSaleBinding
import com.skysam.hchirinos.rosqueteslucy.ui.costumers.AddLocationDialog
import com.skysam.hchirinos.rosqueteslucy.ui.sales.SalesViewModel
import java.text.DateFormat
import java.util.*

class FirstAddSaleFragment : Fragment(), OnClickExit, TextWatcher {

    private var _binding: FragmentFirstAddSaleBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SalesViewModel by activityViewModels()
    private var dateSelected: Long = 0
    private lateinit var costumer: Costumer
    private var isSale = true
    private val sales = mutableListOf<Sale>()
    private val listSorted = mutableListOf<String>()


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

        binding.etPrice.addTextChangedListener(this)
        binding.etRate.addTextChangedListener(this)
        dateSelected = Date().time
        binding.etDate.setText(DateFormat.getDateInstance().format(Date()))
        binding.etQuantity.doAfterTextChanged { binding.tfQuantity.error = null }
        binding.etInvoice.doAfterTextChanged { binding.tfInvoice.error = null }
        binding.etDate.setOnClickListener { selecDate() }
        binding.btnExit.setOnClickListener { getOut() }
        binding.btnTotal.setOnClickListener { validateData() }

        binding.rgMoneda.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_dolar) {
                binding.tfRate.visibility = View.GONE
            } else {
                binding.tfRate.visibility = View.VISIBLE
            }
        }

        binding.extendedFab.setOnClickListener {
            val addLocationDialog = AddLocationDialog(costumer)
            addLocationDialog.show(requireActivity().supportFragmentManager, tag)
        }

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
        viewModel.costumer.observe(viewLifecycleOwner, {
            if (_binding != null) {
                costumer = it
                listSorted.clear()
                listSorted.addAll(costumer.locations.sorted())
                binding.tvNameCostumer.text = it.name
                val adapterLocations = ArrayAdapter(requireContext(), R.layout.layout_spinner, listSorted)
                binding.spinner.adapter = adapterLocations
            }
        })
        viewModel.valueWeb.observe(viewLifecycleOwner, {
            if (_binding != null) {
                binding.tfRate.hint = getString(R.string.text_rate)
                binding.etRate.setText(it)
            }
        })
        viewModel.addLocation.observe(viewLifecycleOwner, {
            if (_binding != null) {
                if (it != null) {
                    binding.spinner.setSelection(listSorted.indexOf(it))
                }
            }
        })
        viewModel.isSale.observe(viewLifecycleOwner, {
            if (_binding != null) {
                if (!it) {
                    binding.tfInvoice.hint = getString(R.string.text_note_sale_number)
                    binding.tvInvoicePaid.text = getString(R.string.text_rg_note_sale_paid)
                    isSale = false
                }
            }
        })
        viewModel.notesSales.observe(viewLifecycleOwner, {
            if (_binding != null) {
                if (!isSale) {
                    var number = 0
                    for (noteSale in it) {
                        if (noteSale.noteNumber > number) {
                            number = noteSale.noteNumber
                        }
                    }
                    binding.etInvoice.setText((number + 1).toString())
                }
            }
        })
        viewModel.sales.observe(viewLifecycleOwner, {
            if (_binding != null) {
                if (isSale) {
                    sales.clear()
                    sales.addAll(it)
                    var number = 0
                    for (sale in it) {
                        if (sale.invoice > number) number = sale.invoice
                    }
                    binding.etInvoice.setText((number + 1).toString())
                }
            }
        })
    }

    private fun validateData() {
        binding.tfPrice.error = null
        binding.tfQuantity.error = null
        binding.tfDate.error = null
        binding.tfInvoice.error = null
        binding.tfRate.error = null

        var price = binding.etPrice.text.toString()
        if (price.isEmpty()) {
            binding.tfPrice.error = getString(R.string.error_field_empty)
            binding.etPrice.requestFocus()
            return
        }
        if (price == "0,00") {
            binding.tfPrice.error = getString(R.string.error_price_zero)
            binding.etPrice.requestFocus()
            return
        }
        price = price.replace(".", "").replace(",", ".")
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
        for (sale in sales) {
            if (invoice.toInt() == sale.invoice) {
                binding.tfInvoice.error = getString(R.string.error_invoice_exists)
                binding.etInvoice.requestFocus()
                return
            }
        }
        var rate: String
        if (binding.rbDolar.isChecked) {
            rate = "1,00"
        } else {
            rate = binding.etRate.text.toString()
            if (rate.isEmpty()) {
                binding.tfRate.error = getString(R.string.error_field_empty)
                binding.etRate.requestFocus()
                return
            }
            if (rate == "0,00") {
                binding.tfRate.error = getString(R.string.error_price_zero)
                binding.etRate.requestFocus()
                return
            }
        }
        rate = rate.replace(".", "").replace(",", ".")

        viewModel.reviewInvoice(binding.spinner.selectedItem.toString(),
            price.toDouble(), rate.toDouble(), quantity.toInt(),
            binding.rbDolar.isChecked, invoice.toInt(), binding.rbPaidYes.isChecked, dateSelected)
        findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
    }

    private fun selecDate() {
        val builder = MaterialDatePicker.Builder.datePicker()
        val calendar = Calendar.getInstance()
        val picker = builder.build()
        picker.addOnPositiveButtonClickListener { selection: Long? ->
            calendar.timeInMillis = selection!!
            val timeZone = TimeZone.getDefault()
            val offset = timeZone.getOffset(Date().time) * -1
            calendar.timeInMillis = calendar.timeInMillis + offset
            val dateSelec = calendar.time
            dateSelected = dateSelec.time
            binding.etDate.setText(DateFormat.getDateInstance().format(dateSelected))
        }
        picker.show(requireActivity().supportFragmentManager, picker.toString())
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(s: Editable?) {
        var cadena = s.toString()
        cadena = cadena.replace(",", "").replace(".", "")
        val cantidad: Double = cadena.toDouble() / 100
        cadena = String.format(Locale.GERMANY, "%,.2f", cantidad)

        if (s.toString() == binding.etPrice.text.toString()) {
            binding.etPrice.removeTextChangedListener(this)
            binding.etPrice.setText(cadena)
            binding.etPrice.setSelection(cadena.length)
            binding.etPrice.addTextChangedListener(this)
        }
        if (s.toString() == binding.etRate.text.toString()) {
            binding.etRate.removeTextChangedListener(this)
            binding.etRate.setText(cadena)
            binding.etRate.setSelection(cadena.length)
            binding.etRate.addTextChangedListener(this)
        }
    }
}