package com.skysam.hchirinos.rosqueteslucy.ui.graphs

import android.content.Context
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.datepicker.MaterialDatePicker
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Expense
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Sale
import com.skysam.hchirinos.rosqueteslucy.databinding.FragmentGraphsBinding
import java.text.DateFormat
import java.util.*


class GraphsFragment : Fragment() {

    private val viewModel: GraphsViewModel by activityViewModels()
    private var _binding: FragmentGraphsBinding? = null
    private val binding get() = _binding!!
    private val sales = mutableListOf<Sale>()
    private val salesNotPaid = mutableListOf<Sale>()
    private val salesPaid = mutableListOf<Sale>()
    private val expenses = mutableListOf<Expense>()
    private var isByRange = true
    private var monthByDefault = 0
    private lateinit var dateStart: Date
    private lateinit var dateFinal: Date

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
        val calendar = Calendar.getInstance()
        dateStart = calendar.time
        dateFinal = calendar.time
        monthByDefault = calendar[Calendar.MONTH]
        loadViewModel()

        binding.etDate.setText(getString(R.string.text_date_range,
            formatDate(dateStart), formatDate(dateFinal)))
        binding.etDate.setOnClickListener { selecDate() }
        binding.chipMonth.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                isByRange = false
                configAdapter()
                binding.spinner.visibility = View.VISIBLE
                binding.tfDate.visibility = View.INVISIBLE
            }
        }
        binding.chipWeek.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                isByRange = true
                binding.spinner.visibility = View.GONE
                binding.tfDate.visibility = View.VISIBLE
                loadChart(true, -1)
            }
        }
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                loadChart(false, position)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
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
                loadChart(true, -1)
            }
        })
        viewModel.expenses.observe(viewLifecycleOwner, {
            if (_binding != null) {
                expenses.clear()
                expenses.addAll(it)
                loadChart(true, -1)
            }
        })
    }

    private fun selecDate() {
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        val calendar = Calendar.getInstance()

        val picker = builder.build()
        picker.addOnPositiveButtonClickListener { selection: Pair<Long, Long> ->
            val timeZone = TimeZone.getDefault()
            val offset = timeZone.getOffset(Date().time) * -1
            calendar.timeInMillis = selection.first
            calendar.timeInMillis = calendar.timeInMillis + offset
            dateStart = calendar.time
            calendar.timeInMillis = selection.second
            calendar[Calendar.HOUR_OF_DAY] = 23
            calendar[Calendar.MINUTE] = 59
            calendar.timeInMillis = calendar.timeInMillis + offset
            dateFinal = calendar.time
            binding.etDate.setText(getString(R.string.text_date_range,
                    formatDate(dateStart), formatDate(dateFinal)))
            loadChart(true, -1)
        }
        picker.show(requireActivity().supportFragmentManager, picker.toString())
    }

    private fun loadChart(isByRange: Boolean, selection: Int) {
        val calendarStartRange = Calendar.getInstance()
        val calendarFinalRange = Calendar.getInstance()
        calendarStartRange.time = dateStart
        calendarFinalRange.time = dateFinal
        calendarStartRange.add(Calendar.DAY_OF_YEAR, -1)
        calendarFinalRange.add(Calendar.DAY_OF_YEAR, 1)
        val pieBalance = binding.pieBalance
        pieBalance.description = null
        pieBalance.centerText = getString(R.string.title_chart)
        pieBalance.setCenterTextSize(24f)
        pieBalance.setDrawEntryLabels(false)
        pieBalance.isRotationEnabled = false

        var totalSalesPaid = 0.0
        var totalWaste = 0.0
        for (sale in salesPaid) {
            val calendar = Calendar.getInstance()
            calendar.time = Date(sale.datePaid)
            val dateSale = Date(sale.datePaid)
            if (isByRange) {
                if (dateSale.after(calendarStartRange.time) && dateSale.before(calendarFinalRange.time)) {
                    val total = sale.quantity * sale.price
                    totalSalesPaid += if (sale.isDolar) {
                        total
                    } else {
                        total / sale.ratePaid
                    }
                    if (!sale.isDolar) totalWaste += (total / sale.rateDelivery) - (total / sale.ratePaid)
                }
            } else {
                if (calendar[Calendar.MONTH] == selection) {
                    val total = sale.quantity * sale.price
                    totalSalesPaid += if (sale.isDolar) {
                        total
                    } else {
                        total / sale.ratePaid
                    }
                    if (!sale.isDolar) totalWaste += (total / sale.rateDelivery) - (total / sale.ratePaid)
                }
            }
        }
        var totalSalesNotPaid = 0.0
        for (sale in salesNotPaid) {
            val calendar = Calendar.getInstance()
            calendar.time = Date(sale.datePaid)
            val dateSale = Date(sale.datePaid)
            if (isByRange) {
                if (dateSale.after(calendarStartRange.time) && dateSale.before(calendarFinalRange.time)) {
                    totalSalesNotPaid += if (sale.isDolar) {
                        (sale.quantity * sale.price)
                    } else {
                        (sale.quantity * sale.price) / sale.ratePaid
                    }
                }
            } else {
                if (calendar[Calendar.MONTH] == selection) {
                    totalSalesNotPaid += if (sale.isDolar) {
                        (sale.quantity * sale.price)
                    } else {
                        (sale.quantity * sale.price) / sale.ratePaid
                    }
                }
            }
        }
        var totalExpenses = 0.0
        for (expense in expenses) {
            val calendar = Calendar.getInstance()
            calendar.time = Date(expense.date)
            val dateExpense = Date(expense.date)
            if (isByRange) {
                if (dateExpense.after(calendarStartRange.time) && dateExpense.before(calendarFinalRange.time)) {
                    totalExpenses += if (expense.isDolar) {
                        (expense.quantity * expense.price)
                    } else {
                        (expense.quantity * expense.price) / expense.rate
                    }
                }
            } else {
                if (calendar[Calendar.MONTH] == selection) {
                    totalExpenses += if (expense.isDolar) {
                        (expense.quantity * expense.price)
                    } else {
                        (expense.quantity * expense.price) / expense.rate
                    }
                }
            }
        }

        val pieEntries = mutableListOf<PieEntry>()
        pieEntries.add(PieEntry(totalSalesPaid.toFloat(), getString(R.string.text_sales_paid)))
        pieEntries.add(PieEntry(totalSalesNotPaid.toFloat(), getString(R.string.text_sales_not_paid)))
        pieEntries.add(PieEntry(totalWaste.toFloat(), getString(R.string.text_waste)))
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
        val pieData = PieData(pieDataSet)
        pieData.setValueFormatter { value, _, _, _ ->
            if (value == 0.0f) {
                String.format("", value)
            } else {
                String.format(Locale.GERMANY, "%,.2f", value)
            }
        }

        pieBalance.data = pieData
        pieBalance.legend.textColor = requireContext().resolveColorAttr(android.R.attr.textColorSecondary)
        pieBalance.legend.textSize = 14f
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

    private fun formatDate(date: Date): String {
        return DateFormat.getDateInstance().format(date)
    }

    private fun configAdapter() {
        val selectionSpinner = monthByDefault
        val listSpinner = listOf(*resources.getStringArray(R.array.months))

        val adapterUnits = ArrayAdapter(requireContext(), R.layout.layout_spinner, listSpinner)
        binding.spinner.apply {
            adapter = adapterUnits
            setSelection(selectionSpinner)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}