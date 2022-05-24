package com.skysam.hchirinos.rosqueteslucy.ui.production

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.Constants
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Production
import com.skysam.hchirinos.rosqueteslucy.databinding.DialogAddProductionBinding
import java.text.DateFormat
import java.util.*

/**
 * Created by Hector Chirinos on 24/05/2022.
 */

class AddProductionDialog: DialogFragment(), TextWatcher {
    private var _binding: DialogAddProductionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProductionViewModel by activityViewModels()
    private var dateSelected: Long = 0
    private lateinit var rate: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ShapeAppearanceOverlay_MaterialComponents_MaterialCalendar_Window_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddProductionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etPrice.addTextChangedListener(this)
        binding.etRate.addTextChangedListener(this)
        dateSelected = Date().time
        binding.etDate.setText(DateFormat.getDateInstance().format(Date()))
        binding.etQuantity.doAfterTextChanged { binding.tfQuantity.error = null }
        binding.etDate.setOnClickListener { selecDate() }
        binding.btnExit.setOnClickListener { dismiss() }
        binding.btnTotal.setOnClickListener { validateData() }

        loadViewModels()
    }

    private fun loadViewModels() {
        viewModel.valueWeb.observe(viewLifecycleOwner) {
            if (_binding != null) {
                binding.tfRate.hint = getString(R.string.text_rate)
                binding.etRate.setText(it)
                rate = it
                if (it == "1,00") {
                    binding.tfRate.error = getString(R.string.error_rate)
                    binding.etRate.doAfterTextChanged { binding.tfRate.error = null }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

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
        var rateF: String
        if (binding.rbDolar.isChecked) {
            rateF = rate
        } else {
            rateF = binding.etRate.text.toString()
            if (rateF.isEmpty()) {
                binding.tfRate.error = getString(R.string.error_field_empty)
                binding.etRate.requestFocus()
                return
            }
            if (rateF == "0,00") {
                binding.tfRate.error = getString(R.string.error_price_zero)
                binding.etRate.requestFocus()
                return
            }
        }
        rateF = rateF.replace(".", "").replace(",", ".")

        val production = Production(
            Constants.ID_COSTUMER,
            quantity.toInt(),
            Date(dateSelected),
            price.toDouble(),
            binding.rbDolar.isChecked,
            rateF.toDouble()
        )

        viewModel.addProduction(production)
        dismiss()
    }
}