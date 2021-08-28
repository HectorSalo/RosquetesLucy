package com.skysam.hchirinos.rosqueteslucy.ui.graphs

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Expense
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Sale
import com.skysam.hchirinos.rosqueteslucy.databinding.FragmentGraphsBinding
import java.util.*


class GraphsFragment : Fragment() {

    private val viewModel: GraphsViewModel by activityViewModels()
    private var _binding: FragmentGraphsBinding? = null
    private val binding get() = _binding!!
    private val sales = mutableListOf<Sale>()
    private val salesNotPaid = mutableListOf<Sale>()
    private val salesPaid = mutableListOf<Sale>()
    private val expenses = mutableListOf<Expense>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGraphsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadViewModel()
    }

    private fun loadViewModel() {
        viewModel.sales.observe(viewLifecycleOwner, {
            if (_binding != null) {
                salesNotPaid.clear()
                salesPaid.clear()
                sales.clear()
                sales.addAll(it)
                for (sale in sales) {
                    if (sale.isPaid) salesPaid.add(sale) else salesNotPaid.add(sale)
                }
                loadChart()
            }
        })
        viewModel.expenses.observe(viewLifecycleOwner, {
            if (_binding != null) {
                expenses.clear()
                expenses.addAll(it)
                loadChart()
            }
        })
    }

    private fun loadChart() {
        val pieBalance = binding.pieBalance
        pieBalance.description = null
        pieBalance.centerText = "Balance Mensual\n($)"
        pieBalance.setCenterTextSize(24f)
        pieBalance.setDrawEntryLabels(false)
        pieBalance.isRotationEnabled = false

        var totalSalesPaid = 0.0
        for (sale in salesPaid) {
            totalSalesPaid += (sale.quantity * sale.price)
        }
        var totalSalesNotPaid = 0.0
        for (sale in salesNotPaid) {
            totalSalesNotPaid += (sale.quantity * sale.price)
        }
        var totalExpenses = 0.0
        for (expense in expenses) {
            totalExpenses += (expense.quantity * expense.price)
        }

        val pieEntries = mutableListOf<PieEntry>()
        pieEntries.add(PieEntry(totalSalesPaid.toFloat(), getString(R.string.text_sales_paid)))
        pieEntries.add(PieEntry(totalSalesNotPaid.toFloat(), getString(R.string.text_sales_not_paid)))
        pieEntries.add(PieEntry(totalExpenses.toFloat(), getString(R.string.text_waste)))
        pieEntries.add(PieEntry(totalExpenses.toFloat(), getString(R.string.title_expenses)))

        val pieDataSet = PieDataSet(pieEntries, "")
        pieDataSet.valueTextSize = 18f
        pieDataSet.setColors(
            ContextCompat.getColor(requireContext(), R.color.green_light),
            ContextCompat.getColor(requireContext(), R.color.red_light),
            ContextCompat.getColor(requireContext(), R.color.yellow),
            ContextCompat.getColor(requireContext(), R.color.blue)
        )
        pieDataSet.formSize = 16f
        pieDataSet.values[0].data = convertNumber(totalSalesPaid.toString())
        val pieData = PieData(pieDataSet)

        pieBalance.data = pieData
        pieBalance.legend.textColor = requireContext().resolveColorAttr(android.R.attr.textColorSecondary)
        pieBalance.invalidate()

        pieBalance.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
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

    private fun convertNumber(number: String): String {
        val cadena = number.replace(",", "").replace(".", "")
        val cantidad: Double = cadena.toDouble() / 100
        return String.format(Locale.GERMANY, "%,.2f", cantidad)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}