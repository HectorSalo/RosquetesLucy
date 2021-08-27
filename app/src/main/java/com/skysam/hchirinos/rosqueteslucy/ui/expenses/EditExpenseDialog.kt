package com.skysam.hchirinos.rosqueteslucy.ui.expenses

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Expense
import com.skysam.hchirinos.rosqueteslucy.databinding.DialogAddExpenseBinding
import java.text.DateFormat
import java.util.*

/**
 * Created by Hector Chirinos on 27/08/2021.
 */
class EditExpenseDialog(private val expense: Expense): DialogFragment(), TextWatcher {
    private var _binding: DialogAddExpenseBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ExpensesViewModel by activityViewModels()
    private var dateSelected: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ShapeAppearanceOverlay_MaterialComponents_MaterialCalendar_Window_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.etPrice.addTextChangedListener(this)
        binding.etRate.addTextChangedListener(this)
        binding.etName.setText(expense.name)
        binding.etPrice.setText(expense.price.toString())
        if (expense.isDolar) {
            binding.rbDolar.isChecked = true
            binding.tfRate.visibility = View.GONE
        } else {
            binding.rbBolivar.isChecked = true
        }
        val quantity = expense.quantity.toString().replace(".", ",")
        binding.etQuantity.setText(quantity)
        dateSelected = expense.date
        binding.etDate.setText(DateFormat.getDateInstance().format(expense.date))

        binding.etDate.setOnClickListener { selecDate() }
        binding.btnExpense.text = getString(R.string.text_edit)
        binding.btnExpense.setOnClickListener { validateData() }
        binding.btnExit.setOnClickListener { dialog?.dismiss() }
        binding.rgMoneda.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.rb_dolar) {
                binding.tfRate.visibility = View.GONE
            } else {
                binding.tfRate.visibility = View.VISIBLE
            }
        }
        binding.etRate.setText(expense.rate.toString())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun validateData() {
        binding.tfName.error = null
        binding.tfPrice.error = null
        binding.tfQuantity.error = null
        binding.tfDate.error = null
        binding.tfRate.error = null

        val name = binding.etName.text.toString()
        if (name.isEmpty()) {
            binding.tfName.error = getString(R.string.error_field_empty)
            binding.etName.requestFocus()
            return
        }
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
        var quantity = binding.etQuantity.text.toString()
        if (quantity.isEmpty()) {
            binding.tfQuantity.error = getString(R.string.error_field_empty)
            binding.etQuantity.requestFocus()
            return
        }
        quantity = quantity.replace(",", ".")
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

        val expenseUpdate = Expense(
            expense.id,
            name,
            price.toDouble(),
            rate.toDouble(),
            quantity.toDouble(),
            binding.rbDolar.isChecked,
            dateSelected
        )
        viewModel.editExpense(expenseUpdate)
        Toast.makeText(requireContext(), getString(R.string.text_editing), Toast.LENGTH_SHORT).show()
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