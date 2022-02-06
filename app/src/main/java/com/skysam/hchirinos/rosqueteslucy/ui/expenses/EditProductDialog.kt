package com.skysam.hchirinos.rosqueteslucy.ui.expenses

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.ClassesCommon
import com.skysam.hchirinos.rosqueteslucy.common.Keyboard
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.PrimaryProducts
import com.skysam.hchirinos.rosqueteslucy.databinding.DialogEditProductBinding
import java.lang.Exception

/**
 * Created by Hector Chirinos (Home) on 1/10/2021.
 */
class EditProductDialog(private var product: PrimaryProducts,
                        private val position: Int, private val rateChange: Double):
    DialogFragment(), TextWatcher {

    private var _binding: DialogEditProductBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ExpensesViewModel by activityViewModels()
    private lateinit var buttonPositive: Button
    private lateinit var buttonNegative: Button
    private var quantityTotal: Double = 1.0
    private var priceTotal: Double = 1.0
    private lateinit var unit: String
    private lateinit var name: String

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogEditProductBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_edit_producto_dialog, product.name))
            .setView(binding.root)
            .setPositiveButton(R.string.text_update, null)
            .setNegativeButton(R.string.btn_cancel, null)

        val dialog = builder.create()
        dialog.show()

        binding.etPrice.addTextChangedListener(this)
        binding.etQuantity.doAfterTextChanged { text ->
            if (!text.isNullOrEmpty()) {
                try {
                    var number = text.toString()
                    number = number.replace(",", ".")
                    if (number.toDouble() > 0) {
                        quantityTotal = number.toDouble()
                    }
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Error en el número ingresado", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.ibAddQuantity.setOnClickListener { addQuantity() }
        binding.ibRestQuantity.setOnClickListener { restQuantity() }

        buttonNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        buttonNegative.setOnClickListener { dialog.dismiss() }
        buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        buttonPositive.setOnClickListener { validateEdit() }

        loadData()

        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadData() {
        val listUnits = listOf(*resources.getStringArray(R.array.units))
        val adapterUnits = ArrayAdapter(requireContext(), R.layout.layout_spinner, listUnits)
        binding.spinner.adapter = adapterUnits
        name = product.name
        quantityTotal = product.quantity
        priceTotal = product.price
        unit = product.unit

        binding.etPrice.setText(ClassesCommon.convertDoubleToString(product.price))
        binding.etQuantity.setText(ClassesCommon.convertDoubleToString(product.quantity))
        binding.spinner.setSelection(listUnits.indexOf(product.unit))
    }

    private fun restQuantity() {
        if (quantityTotal > 1) {
            quantityTotal -= 1
            binding.etQuantity.setText(ClassesCommon.convertDoubleToString(quantityTotal))
        }
    }

    private fun addQuantity() {
        quantityTotal += 1
        binding.etQuantity.setText(ClassesCommon.convertDoubleToString(quantityTotal))
    }

    private fun validateEdit() {
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
        priceTotal = price.toDouble()
        if (binding.spinner.selectedItemPosition > 0) {
            unit = binding.spinner.selectedItem.toString()
        }
        if (binding.rbBolivar.isChecked)  priceTotal /= rateChange
        val productResult = PrimaryProducts(
            name,
            unit,
            priceTotal,
            quantityTotal
        )
        Keyboard.close(binding.root)
        viewModel.updateProduct(productResult, position)
        dismiss()
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun afterTextChanged(s: Editable?) {
        var cadena = s.toString()
        cadena = cadena.replace(",", "").replace(".", "")
        val cantidad: Double = cadena.toDouble() / 100
        cadena = ClassesCommon.convertDoubleToString(cantidad)

        if (s.toString() == binding.etPrice.text.toString()) {
            binding.etPrice.removeTextChangedListener(this)
            binding.etPrice.setText(cadena)
            binding.etPrice.setSelection(cadena.length)
            binding.etPrice.addTextChangedListener(this)
        }
    }
}