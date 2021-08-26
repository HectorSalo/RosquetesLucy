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
        binding.tvQuantity.text = sale.quantity.toString()
        binding.tvPriceUnit.text = convertFormatNumber(sale.price)
        if (sale.isDolar) {
            binding.tvTitleAmount.text = getString(R.string.title_amount_total, "$")
        } else {
            binding.tvTitleAmount.text = getString(R.string.title_amount_total, "Bs.")
        }
        val total = sale.quantity * sale.price
        binding.tvAmount.text = getString(R.string.text_total_amount, convertFormatNumber(total))
        val iva = total * 0.16
        binding.tvTotalIva.text = getString(R.string.text_total_amount, convertFormatNumber(iva))
        val totalAmount = total + iva
        binding.tvTotalMonto.text = getString(R.string.text_total_amount, convertFormatNumber(totalAmount))

        binding.btnSale.setOnClickListener { paidSale() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun paidSale() {
        sale.datePaid = Date().time
        sale.isPaid = true

        viewModel.paidSale(sale)
        Toast.makeText(requireContext(), getString(R.string.text_editing), Toast.LENGTH_SHORT).show()
        dialog?.dismiss()
    }

    private fun convertFormatNumber(amount: Double): String {
        return String.format(Locale.GERMANY, "%,.2f", amount)
    }
}