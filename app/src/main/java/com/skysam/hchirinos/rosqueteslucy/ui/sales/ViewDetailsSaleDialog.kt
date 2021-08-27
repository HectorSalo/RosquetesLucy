package com.skysam.hchirinos.rosqueteslucy.ui.sales

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Sale
import com.skysam.hchirinos.rosqueteslucy.databinding.FragmentSecondAddSaleBinding
import com.skysam.hchirinos.rosqueteslucy.ui.sales.pages.PaidDialog
import java.text.DateFormat
import java.util.*

/**
 * Created by Hector Chirinos (Home) on 15/8/2021.
 */
class ViewDetailsSaleDialog(private val sale: Sale): DialogFragment() {
    private var _binding: FragmentSecondAddSaleBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SalesViewModel by activityViewModels()

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

        if (sale.isPaid) {
            binding.btnSale.visibility = View.GONE
        }
        binding.btnSale.text = getString(R.string.btn_paid)
        binding.tvNameCostumer.text = sale.nameCostumer
        binding.tvRif.text = sale.idCostumer
        binding.tvLocationCostumer.text = sale.location
        binding.tvDate.text = DateFormat.getDateInstance().format(sale.dateDelivery)
        binding.tvInvoice.text = getString(R.string.text_invoice_item, sale.invoice.toString())
        binding.tvRate.text = getString(R.string.text_rate_view, convertFormatNumber(sale.ratePaid))
        binding.tvQuantity.text = sale.quantity.toString()
        binding.tvPriceUnit.text = convertFormatNumber(sale.price)
        if (sale.isDolar) {
            binding.tvTitleAmount.text = getString(R.string.title_amount_total, "$")
            binding.tvTotalMontoBs.visibility = View.GONE
            binding.tvTotalIvaBs.visibility = View.GONE
            binding.tvTextIvaBs.visibility = View.GONE
            binding.tvTextTotalBs.visibility = View.GONE
            binding.tvRate.visibility = View.GONE
        } else {
            binding.tvTitleAmount.text = getString(R.string.title_amount_total, "Bs.")
            binding.tvTextIvaDolar.visibility = View.GONE
            binding.tvTotalIvaDolar.visibility = View.GONE
        }
        showTotal()
        binding.btnSale.setOnClickListener { paidSale() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showTotal() {
        val total = sale.quantity * sale.price
        binding.tvAmount.text = getString(R.string.text_total_amount, convertFormatNumber(total))
        if (!sale.isDolar) {
            val ivaBs = total * 0.16
            binding.tvTotalIvaBs.text = getString(R.string.text_total_amount, convertFormatNumber(ivaBs))
            val totalAmountBs = total + ivaBs
            binding.tvTotalMontoBs.text = getString(R.string.text_total_amount, convertFormatNumber(totalAmountBs))
            val totalAmountDolar = total / sale.ratePaid
            binding.tvTotalMontoDolar.text = getString(R.string.text_total_amount, convertFormatNumber(totalAmountDolar))
        } else {
            val ivaDolar = total * 0.16
            binding.tvTotalIvaDolar.text = getString(R.string.text_total_amount, convertFormatNumber(ivaDolar))
            val totalAmountDolar = total + ivaDolar
            binding.tvTotalMontoDolar.text = getString(R.string.text_total_amount, convertFormatNumber(totalAmountDolar))
        }
    }

    private fun paidSale() {
        if (sale.isDolar) {
            sale.datePaid = Date().time
            sale.isPaid = true
            viewModel.paidSale(sale)
            Toast.makeText(requireContext(), getString(R.string.text_editing), Toast.LENGTH_SHORT).show()
            dialog?.dismiss()
        } else {
            val paidDialog = PaidDialog(sale)
            paidDialog.show(requireActivity().supportFragmentManager, tag)
        }
    }

    private fun convertFormatNumber(amount: Double): String {
        return String.format(Locale.GERMANY, "%,.2f", amount)
    }
}