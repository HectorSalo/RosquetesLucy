package com.skysam.hchirinos.rosqueteslucy.ui.notesSale.pages

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.NoteSale
import com.skysam.hchirinos.rosqueteslucy.databinding.DialogPaidBinding
import com.skysam.hchirinos.rosqueteslucy.ui.notesSale.NotesSaleViewModel
import com.skysam.hchirinos.rosqueteslucy.ui.sales.pages.CloseDialog
import java.text.DateFormat
import java.util.*

/**
 * Created by Hector Chirinos (Home) on 29/9/2021.
 */
class PaidNoteSaleDialog(private val noteSale: NoteSale, private val closeDialog: CloseDialog):
    DialogFragment(), TextWatcher {
    private var _binding: DialogPaidBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NotesSaleViewModel by activityViewModels()
    private lateinit var buttonPositive: Button
    private lateinit var buttonNegative: Button
    private var dateSelected: Long = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogPaidBinding.inflate(layoutInflater)

        dateSelected = Date().time
        binding.etDate.setText(DateFormat.getDateInstance().format(Date()))
        binding.etDate.setOnClickListener { selecDate() }
        binding.etRate.addTextChangedListener(this)

        if (noteSale.isDolar) binding.tfRate.visibility = View.GONE

        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_dialog_paid))
            .setView(binding.root)
            .setPositiveButton(R.string.btn_paid_sale, null)
            .setNegativeButton(R.string.btn_cancel, null)

        val dialog = builder.create()
        dialog.show()

        loadViewModel()

        buttonNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        buttonNegative.setOnClickListener { dialog.dismiss() }
        buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        buttonPositive.setOnClickListener { validateData() }

        return dialog
    }

    private fun loadViewModel() {
        viewModel.valueWeb.observe(this.requireActivity(), {
            if (_binding != null) {
                binding.tfRate.hint = getString(R.string.text_rate)
                binding.etRate.setText(it)
                if (it == "1,00") {
                    binding.tfRate.error = getString(R.string.error_rate)
                    binding.etRate.doAfterTextChanged { binding.tfRate.error = null }
                }
            }
        })
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
        binding.tfDate.error = null
        binding.tfRate.error = null

        if (!noteSale.isDolar) {
            var rate = binding.etRate.text.toString()
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
            rate = rate.replace(".", "").replace(",", ".")
            noteSale.ratePaid = rate.toDouble()
        }
        noteSale.datePaid = dateSelected
        noteSale.isPaid = true
        Toast.makeText(requireContext(), getString(R.string.text_editing), Toast.LENGTH_SHORT).show()
        viewModel.paidNoteSale(noteSale)
        closeDialog.close()
        dialog?.dismiss()
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

        binding.etRate.removeTextChangedListener(this)
        binding.etRate.setText(cadena)
        binding.etRate.setSelection(cadena.length)
        binding.etRate.addTextChangedListener(this)
    }
}