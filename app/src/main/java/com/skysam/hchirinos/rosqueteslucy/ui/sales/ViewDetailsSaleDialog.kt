package com.skysam.hchirinos.rosqueteslucy.ui.sales

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Customer
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Sale
import com.skysam.hchirinos.rosqueteslucy.databinding.FragmentSecondAddSaleBinding
import com.skysam.hchirinos.rosqueteslucy.ui.sales.pages.CloseDialog
import com.skysam.hchirinos.rosqueteslucy.ui.sales.pages.PaidSaleDialog
import java.text.DateFormat
import java.util.*

/**
 * Created by Hector Chirinos (Home) on 15/8/2021.
 */
class ViewDetailsSaleDialog(private var sale: Sale): DialogFragment(), CloseDialog {
    private var _binding: FragmentSecondAddSaleBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SalesViewModel by activityViewModels()
    private val allSales = mutableListOf<Sale>()
    private val costumers = mutableListOf<Customer>()

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

        viewModel.sales.observe(viewLifecycleOwner, {
            if (_binding != null) {
                allSales.clear()
                allSales.addAll(it)
                if (allSales.indexOf(sale) != allSales.lastIndex) binding.ibBack.visibility = View.VISIBLE
                if (allSales.indexOf(sale) != 0) binding.ibFoward.visibility = View.VISIBLE
            }
        })
        viewModel.costumers.observe(viewLifecycleOwner, {
            if (_binding != null) {
                costumers.clear()
                costumers.addAll(it)
                for (cos in costumers) {
                    if (cos.id == sale.idCostumer) {
                        binding.tvRif.text = cos.identifier
                    }
                }
            }
        })

        binding.btnSale.setOnClickListener { paidSale() }
        binding.ibBack.setOnClickListener {
            val position = allSales.indexOf(sale)
            sale = allSales[position + 1]
            loadData()
            if (allSales.indexOf(sale) == allSales.lastIndex) binding.ibBack.visibility = View.INVISIBLE
            if (allSales.indexOf(sale) == 1) binding.ibFoward.visibility = View.VISIBLE
        }
        binding.ibFoward.setOnClickListener {
            val position = allSales.indexOf(sale)
            sale = allSales[position - 1]
            loadData()
            if (allSales.indexOf(sale) == 0) binding.ibFoward.visibility = View.INVISIBLE
            if (allSales.indexOf(sale) == allSales.lastIndex - 1) binding.ibBack.visibility = View.VISIBLE
        }
        loadData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadData() {
        for (cos in costumers) {
            if (cos.id == sale.idCostumer) {
                binding.tvRif.text = cos.identifier
            }
        }
        if (sale.isPaid) binding.btnSale.visibility = View.GONE else binding.btnSale.visibility = View.VISIBLE
        if (sale.isAnnuled) {
            binding.tvSubtitle.text = getString(R.string.text_date_annul,
                DateFormat.getDateInstance()
                    .format(sale.datePaid))
            binding.tvSubtitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        } else {
            binding.tvSubtitle.text = getString(R.string.text_subtitle_details)
            binding.tvSubtitle.setTextColor(requireContext().resolveColorAttr(android.R.attr.textColorSecondary))
        }
        binding.btnSale.text = getString(R.string.btn_paid_sale)
        binding.tvNameCostumer.text = sale.nameCostumer
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
            if (sale.isPaid) {
                binding.tvTextIvaDolar.visibility = View.VISIBLE
                binding.tvTotalIvaDolar.visibility = View.VISIBLE
                binding.tvTextIvaDolar.text = getString(R.string.title_waste)
                val totalWaste = (total / sale.rateDelivery) - totalAmountDolar
                binding.tvTotalIvaDolar.text = getString(R.string.text_total_amount,
                    convertFormatNumber(totalWaste))
            }
        } else {
            val ivaDolar = total * 0.16
            binding.tvTotalIvaDolar.text = getString(R.string.text_total_amount, convertFormatNumber(ivaDolar))
            val totalAmountDolar = total + ivaDolar
            binding.tvTotalMontoDolar.text = getString(R.string.text_total_amount, convertFormatNumber(totalAmountDolar))
        }
    }

    private fun paidSale() {
        val paidSaleDialog = PaidSaleDialog(sale, this)
        paidSaleDialog.show(requireActivity().supportFragmentManager, tag)
    }

    private fun convertFormatNumber(amount: Double): String {
        return String.format(Locale.GERMANY, "%,.2f", amount)
    }

    override fun close() {
        dialog?.dismiss()
    }

    @ColorInt
    fun Context.resolveColorAttr(@AttrRes colorAttr: Int): Int {
        val resolvedAttr = resolveThemeAttr(colorAttr)
        // resourceId is used if it's a ColorStateList, and data if it's a color reference or a hex color
        val colorRes = if (resolvedAttr.resourceId != 0) resolvedAttr.resourceId else resolvedAttr.data
        return ContextCompat.getColor(requireContext(), colorRes)
    }

    private fun Context.resolveThemeAttr(@AttrRes attrRes: Int): TypedValue {
        val typedValue = TypedValue()
        theme.resolveAttribute(attrRes, typedValue, true)
        return typedValue
    }
}