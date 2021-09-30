package com.skysam.hchirinos.rosqueteslucy.ui.refunds

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.ClassesCommon
import com.skysam.hchirinos.rosqueteslucy.common.Keyboard
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Refund
import com.skysam.hchirinos.rosqueteslucy.databinding.DialogAddRefundBinding
import java.text.DateFormat
import java.util.*

/**
 * Created by Hector Chirinos (Home) on 30/9/2021.
 */
class ViewDetailsRefundDialog(private val refund: Refund): DialogFragment(), TextWatcher {
    private var _binding: DialogAddRefundBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RefundsViewModel by activityViewModels()
    private lateinit var buttonPositive: Button
    private lateinit var buttonNegative: Button
    private var dateSelected: Long = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddRefundBinding.inflate(layoutInflater)

        dateSelected = refund.date
        binding.etDate.setText(DateFormat.getDateInstance().format(Date(refund.date)))
        binding.tvNameCostumer.text = refund.nameCostumer
        binding.etDate.setOnClickListener { selecDate() }
        binding.etPrice.addTextChangedListener(this)
        binding.etRate.addTextChangedListener(this)
        binding.etQuantity.doAfterTextChanged { binding.tfQuantity.error = null }
        binding.spinner.visibility = View.GONE

        binding.etPrice.setText(ClassesCommon.convertDoubleToString(refund.price))
        if (refund.isDolar) {
            binding.rbDolar.isChecked = true
            binding.tfRate.visibility = View.GONE
        } else {
            binding.rbBolivar.isChecked = true
        }
        binding.etQuantity.setText(refund.quantity.toString())

        binding.rgMoneda.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_dolar) {
                binding.tfRate.visibility = View.GONE
            } else {
                binding.tfRate.visibility = View.VISIBLE
            }
        }
        binding.etRate.setText(refund.rate.toString())

        val builder = AlertDialog.Builder(requireActivity())
        builder.setView(binding.root)
            .setPositiveButton(R.string.text_update, null)
            .setNegativeButton(R.string.btn_exit, null)

        val dialog = builder.create()
        dialog.show()

        buttonNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        buttonNegative.setOnClickListener { dialog.dismiss() }
        buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        buttonPositive.setOnClickListener { validateData() }

        return dialog
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

    private fun validateData() {
        binding.tfPrice.error = null
        binding.tfQuantity.error = null
        binding.tfDate.error = null
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

        Keyboard.close(binding.root)
        val refundToUpdate = Refund(
            refund.id,
            refund.idCostumer,
            refund.nameCostumer,
            refund.location,
            price.toDouble(),
            binding.rbDolar.isChecked,
            quantity.toInt(),
            dateSelected,
            rate.toDouble()
        )
        viewModel.editRefund(refundToUpdate)
        Toast.makeText(requireContext(), getString(R.string.text_saving), Toast.LENGTH_SHORT).show()
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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