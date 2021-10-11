package com.skysam.hchirinos.rosqueteslucy.ui.expenses

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.Constants
import com.skysam.hchirinos.rosqueteslucy.common.Keyboard
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.PrimaryProducts
import com.skysam.hchirinos.rosqueteslucy.databinding.DialogAddProductBinding

/**
 * Created by Hector Chirinos (Home) on 1/10/2021.
 */
class AddProductDialog(private val name: String?,
                       private val products: MutableList<String>):
    DialogFragment() {
    private var _binding: DialogAddProductBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ExpensesViewModel by activityViewModels()
    private lateinit var buttonPositive: Button
    private lateinit var buttonNegative: Button

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddProductBinding.inflate(layoutInflater)

        binding.etName.doAfterTextChanged { binding.tfName.error = null }

        if (!name.isNullOrEmpty()) binding.etName.setText(name)

        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_add_producto_dialog))
            .setView(binding.root)
            .setPositiveButton(R.string.btn_save, null)
            .setNegativeButton(R.string.btn_cancel, null)

        val dialog = builder.create()
        dialog.show()

        buttonNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        buttonNegative.setOnClickListener { dialog.dismiss() }
        buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        buttonPositive.setOnClickListener { validateProduct() }
        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun validateProduct() {
        binding.tfName.error = null
        val name = binding.etName.text.toString().trim()
        if (name.isEmpty()) {
            binding.tfName.error = getString(R.string.error_field_empty)
            return
        }
        for (i in products) {
            if (i.equals(name, true)) {
                binding.tfName.error = getString(R.string.error_name_product_exists)
                return
            }
        }
        Keyboard.close(binding.root)
        val product = PrimaryProducts(
            name,
            Constants.PRODUCT_UNIT_INITIAL,
            0.0,
            1.0
        )
        viewModel.addPrimaryProduct(product)
        dialog?.dismiss()
    }
}