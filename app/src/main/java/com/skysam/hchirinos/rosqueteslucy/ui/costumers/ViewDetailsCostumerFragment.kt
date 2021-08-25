package com.skysam.hchirinos.rosqueteslucy.ui.costumers

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.Constants
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Costumer
import com.skysam.hchirinos.rosqueteslucy.databinding.ViewDetailsCostumerDialogBinding
import com.skysam.hchirinos.rosqueteslucy.ui.sales.addSale.AddSaleActivity

/**
 * Created by Hector Chirinos (Home) on 19/8/2021.
 */
class ViewDetailsCostumerFragment(private val costumer: Costumer): DialogFragment() {
    private var _binding: ViewDetailsCostumerDialogBinding? = null
    private val binding get() = _binding!!
    private lateinit var buttonPositive: Button
    private lateinit var buttonNegative: Button

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = ViewDetailsCostumerDialogBinding.inflate(layoutInflater)

        binding.tvRif.text = costumer.identifier
        binding.tvAddress.text = costumer.address

        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_details_costumer))
            .setView(binding.root)
            .setPositiveButton(R.string.btn_sale, null)
            .setNegativeButton(R.string.btn_close, null)

        val dialog = builder.create()
        dialog.show()

        buttonNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        buttonNegative.setOnClickListener { dialog.dismiss() }
        buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        buttonPositive.setOnClickListener {
            val intent = Intent(requireContext(), AddSaleActivity::class.java)
            intent.putExtra(Constants.ID_COSTUMER, costumer)
            startActivity(intent)
            dismiss()
        }

        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}