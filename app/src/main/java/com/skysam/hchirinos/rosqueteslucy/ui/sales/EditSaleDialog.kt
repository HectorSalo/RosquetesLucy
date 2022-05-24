package com.skysam.hchirinos.rosqueteslucy.ui.sales

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.ClassesCommon
import com.skysam.hchirinos.rosqueteslucy.common.Keyboard
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Sale
import com.skysam.hchirinos.rosqueteslucy.databinding.FragmentFirstAddSaleBinding
import com.skysam.hchirinos.rosqueteslucy.ui.common.ExitDialog
import com.skysam.hchirinos.rosqueteslucy.ui.common.OnClickExit
import java.text.DateFormat
import java.util.*

/**
 * Created by Hector Chirinos (Home) on 30/10/2021.
 */
class EditSaleDialog(private val sale: Sale): DialogFragment(), TextWatcher, OnClickExit {
    private var _binding: FragmentFirstAddSaleBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SalesViewModel by activityViewModels()
    private var dateSelected: Long = 0
    private val sales = mutableListOf<Sale>()
    private val listSorted = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ShapeAppearanceOverlay_MaterialComponents_MaterialCalendar_Window_Fullscreen)
    }

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
        dateSelected = sale.dateDelivery
        binding.etDate.setText(DateFormat.getDateInstance().format(sale.dateDelivery))
        binding.etInvoice.setText(sale.invoice.toString())
        binding.etQuantity.setText(sale.quantity.toString())
        binding.etPrice.setText(ClassesCommon.convertDoubleToString(sale.price))
        binding.etQuantity.doAfterTextChanged { binding.tfQuantity.error = null }
        binding.etInvoice.doAfterTextChanged { binding.tfInvoice.error = null }
        binding.btnTotal.text = getString(R.string.text_update)
        binding.etDate.setOnClickListener { selecDate() }
        binding.btnExit.setOnClickListener { getOut() }
        binding.btnTotal.setOnClickListener { validateData() }


        if (sale.isDolar) {
            binding.rbDolar.isChecked = true
            binding.rbBolivar.visibility = View.GONE
        } else {
            binding.rbBolivar.isChecked = true
            binding.rbDolar.visibility = View.GONE
        }

        binding.extendedFab.visibility = View.GONE
        binding.rgInvoicePaid.visibility = View.GONE
        binding.tvInvoicePaid.visibility = View.GONE
        binding.tfRate.visibility = View.GONE

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
        dismiss()
    }

    private fun loadViewModel() {
        viewModel.costumers.observe(viewLifecycleOwner) {
            if (_binding != null) {
                for (cos in it) {
                    if (cos.id == sale.idCostumer) {
                        viewModel.addCostumer(cos)
                        break
                    }
                }
            }
        }
        viewModel.costumer.observe(viewLifecycleOwner) {
            if (_binding != null) {
                listSorted.clear()
                listSorted.addAll(it.locations.sorted())
                binding.tvNameCostumer.text = it.name
                val adapterLocations =
                    ArrayAdapter(requireContext(), R.layout.layout_spinner, listSorted)
                binding.spinner.adapter = adapterLocations
                binding.spinner.setSelection(listSorted.indexOf(sale.location))
            }
        }
        viewModel.sales.observe(viewLifecycleOwner) {
            if (_binding != null) {
                sales.clear()
                sales.addAll(it)
            }
        }
    }

    private fun validateData() {
        binding.tfPrice.error = null
        binding.tfQuantity.error = null
        binding.tfDate.error = null
        binding.tfInvoice.error = null

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
        val invoice = binding.etInvoice.text.toString()
        if (invoice.isEmpty()) {
            binding.tfInvoice.error = getString(R.string.error_field_empty)
            binding.etInvoice.requestFocus()
            return
        }
        for (sal in sales) {
            if (invoice.toInt() == sal.invoice && sal.id != sale.id) {
                binding.tfInvoice.error = getString(R.string.error_invoice_exists)
                binding.etInvoice.requestFocus()
                return
            }
        }
        sale.location = binding.spinner.selectedItem.toString()
        sale.price = price.toDouble()
        sale.quantity = quantity.toInt()
        sale.dateDelivery = dateSelected
        sale.datePaid = dateSelected
        sale.invoice = invoice.toInt()

        Keyboard.close(binding.root)
        viewModel.editSale(sale)
        Toast.makeText(requireContext(), getString(R.string.text_saving), Toast.LENGTH_SHORT).show()
        dialog?.dismiss()
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