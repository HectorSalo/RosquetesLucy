package com.skysam.hchirinos.rosqueteslucy.ui.sales.pages

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Sale
import com.skysam.hchirinos.rosqueteslucy.databinding.DialogPaidBinding
import com.skysam.hchirinos.rosqueteslucy.ui.sales.SalesViewModel
import java.text.DateFormat
import java.util.*

/**
 * Created by Hector Chirinos (Home) on 26/8/2021.
 */
class PaidDialog(private val sale: Sale): DialogFragment() {
    private var _binding: DialogPaidBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SalesViewModel by activityViewModels()
    private lateinit var buttonPositive: Button
    private lateinit var buttonNegative: Button
    private var dateSelected: Long = 0

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogPaidBinding.inflate(layoutInflater)

        dateSelected = Date().time
        binding.etDate.setText(DateFormat.getDateInstance().format(Date()))
        binding.etDate.setOnClickListener { selecDate() }

        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_dialog_paid))
            .setView(binding.root)
            .setPositiveButton(R.string.btn_sale, null)
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
            binding.tfRate.hint = getString(R.string.text_rate)
            binding.etRate.setText(String.format(Locale.GERMANY, "%,.2f", it.toDouble()))
        })
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

    private fun validateData() {
        val rate = binding.etRate.text.toString()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}