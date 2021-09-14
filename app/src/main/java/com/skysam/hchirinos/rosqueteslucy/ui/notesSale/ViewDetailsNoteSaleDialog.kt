package com.skysam.hchirinos.rosqueteslucy.ui.notesSale

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.NoteSale
import com.skysam.hchirinos.rosqueteslucy.databinding.FragmentSecondAddSaleBinding
import java.text.DateFormat
import java.util.*

/**
 * Created by Hector Chirinos on 14/09/2021.
 */
class ViewDetailsNoteSaleDialog(private val noteSale: NoteSale): DialogFragment() {
    private var _binding: FragmentSecondAddSaleBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ShapeAppearanceOverlay_MaterialComponents_MaterialCalendar_Window_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondAddSaleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSale.visibility = View.GONE
        binding.tvTextIvaDolar.visibility = View.GONE
        binding.tvTotalIvaDolar.visibility = View.GONE
        binding.tvTotalIvaBs.visibility = View.GONE
        binding.tvTextIvaBs.visibility = View.GONE
        binding.tvNameCostumer.text = noteSale.nameCostumer
        binding.tvRif.text = noteSale.idCostumer
        binding.tvLocationCostumer.text = noteSale.location
        binding.tvDate.text = DateFormat.getDateInstance().format(noteSale.date)
        binding.tvInvoice.text = getString(R.string.text_note_sale_item, noteSale.noteNumber.toString())
        binding.tvRate.text = getString(R.string.text_rate_view, convertFormatNumber(noteSale.rate))
        binding.tvQuantity.text = noteSale.quantity.toString()
        binding.tvPriceUnit.text = convertFormatNumber(noteSale.price)
        if (noteSale.isDolar) {
            binding.tvTitleAmount.text = getString(R.string.title_amount_total, "$")
            binding.tvTotalMontoBs.visibility = View.GONE
            binding.tvTextTotalBs.visibility = View.GONE
            binding.tvRate.visibility = View.GONE
        } else {
            binding.tvTitleAmount.text = getString(R.string.title_amount_total, "Bs.")
        }
        showTotal()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showTotal() {
        val total = noteSale.quantity * noteSale.price
        binding.tvAmount.text = getString(R.string.text_total_amount, convertFormatNumber(total))
        if (!noteSale.isDolar) {
            binding.tvTotalMontoBs.text = getString(R.string.text_total_amount, convertFormatNumber(total))
            val totalAmountDolar = total / noteSale.rate
            binding.tvTotalMontoDolar.text = getString(R.string.text_total_amount, convertFormatNumber(totalAmountDolar))
        } else {
            binding.tvTotalMontoDolar.text = getString(R.string.text_total_amount, convertFormatNumber(total))
        }
    }

    private fun convertFormatNumber(amount: Double): String {
        return String.format(Locale.GERMANY, "%,.2f", amount)
    }
}