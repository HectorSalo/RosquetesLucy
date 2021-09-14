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
import com.skysam.hchirinos.rosqueteslucy.databinding.DialogViewDetailsCostumerBinding
import com.skysam.hchirinos.rosqueteslucy.ui.sales.addSale.AddSaleActivity

/**
 * Created by Hector Chirinos (Home) on 19/8/2021.
 */
class ViewDetailsCostumerFragment(private val costumer: Costumer): DialogFragment() {
    private var _binding: DialogViewDetailsCostumerBinding? = null
    private val binding get() = _binding!!
    private lateinit var buttonPositive: Button
    private lateinit var buttonNeutral: Button

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogViewDetailsCostumerBinding.inflate(layoutInflater)

        binding.tvRif.text = costumer.identifier
        binding.tvAddress.text = costumer.address

        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_details_costumer))
            .setView(binding.root)
            .setPositiveButton(R.string.btn_sale, null)
            .setNeutralButton(R.string.btn_note_sale, null)

        val dialog = builder.create()
        dialog.show()

        buttonNeutral = dialog.getButton(DialogInterface.BUTTON_NEUTRAL)
        buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        buttonNeutral.setOnClickListener {
            val intent = Intent(requireContext(), AddSaleActivity::class.java)
            intent.putExtra(Constants.ID_COSTUMER, costumer)
            intent.putExtra(Constants.IS_SALE, false)
            startActivity(intent)
            dismiss()
        }
        buttonPositive.setOnClickListener {
            val intent = Intent(requireContext(), AddSaleActivity::class.java)
            intent.putExtra(Constants.ID_COSTUMER, costumer)
            intent.putExtra(Constants.IS_SALE, true)
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