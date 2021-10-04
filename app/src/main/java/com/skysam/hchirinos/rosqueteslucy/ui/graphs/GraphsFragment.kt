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
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.*
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
    private val notesSale = mutableListOf<NoteSale>()
    private val notesSalePaid = mutableListOf<NoteSale>()
    private val notesSaleNotPaid = mutableListOf<NoteSale>()
    private val refunds = mutableListOf<Refund>()
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
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        dateStart = calendar.time
        calendar[Calendar.HOUR_OF_DAY] = 23
        calendar[Calendar.MINUTE] = 59
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
        viewModel.notesSale.observe(viewLifecycleOwner, {
            if (_binding != null) {
                notesSalePaid.clear()
                notesSaleNotPaid.clear()
                notesSale.clear()
                notesSale.addAll(it)
                for (noteSale in notesSale) {
                    if (noteSale.isPaid) notesSalePaid.add(noteSale) else notesSaleNotPaid.add(noteSale)
                }
                loadChart(true, -1)
            }
        })
        viewModel.refunds.observe(viewLifecycleOwner, {
            if (_binding != null) {
                refunds.clear()
                refunds.addAll(it)
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
            calendar[Calendar.HOUR_OF_DAY] = 0
            calendar[Calendar.MINUTE] = 0
            dateStart = calendar.time
            calendar.timeInMillis = selection.second
            calendar.timeInMillis = calendar.timeInMillis + offset
            calendar[Calendar.HOUR_OF_DAY] = 23
            calendar[Calendar.MINUTE] = 59
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

        val pieBalance = binding.pieBalance
        pieBalance.description = null
        pieBalance.centerText = getString(R.string.title_chart)
        pieBalance.setCenterTextSize(24f)
        pieBalance.setDrawEntryLabels(false)
        pieBalance.isRotationEnabled = false

        var totalSalesPaid = 0.0
        var totalWaste = 0.0
        for (sale in salesPaid) {
            if (isByRange) {
                val dateSale = Date(sale.datePaid)
                if (dateSale.after(calendarStartRange.time) && dateSale.before(calendarFinalRange.time)) {
                    val total = sale.quantity * sale.price
                    totalSalesPaid += if (sale.isDolar) {
                        total
                    } else {
                        total / sale.ratePaid
                    }
                    val rest = (total / sale.rateDelivery) - (total / sale.ratePaid)
                    if (!sale.isDolar) totalWaste += if (rest > 0) rest else 0.0
                }
            } else {
                val calendar = Calendar.getInstance()
                calendar.time = Date(sale.datePaid)
                if (calendar[Calendar.MONTH] == selection) {
                    val total = sale.quantity * sale.price
                    totalSalesPaid += if (sale.isDolar) {
                        total
                    } else {
                        total / sale.ratePaid
                    }
                    val rest = (total / sale.rateDelivery) - (total / sale.ratePaid)
                    if (!sale.isDolar) totalWaste += if (rest > 0) rest else 0.0
                }
            }
        }
        var totalSalesNotPaid = 0.0
        for (sale in salesNotPaid) {
            if (isByRange) {
                val dateSale = Date(sale.datePaid)
                if (dateSale.after(calendarStartRange.time) && dateSale.before(calendarFinalRange.time)) {
                    totalSalesNotPaid += if (sale.isDolar) {
                        (sale.quantity * sale.price)
                    } else {
                        (sale.quantity * sale.price) / sale.ratePaid
                    }
                }
            } else {
                val calendar = Calendar.getInstance()
                calendar.time = Date(sale.datePaid)
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
            if (isByRange) {
                val dateExpense = Date(expense.dateCreated)
                if (dateExpense.after(calendarStartRange.time) && dateExpense.before(calendarFinalRange.time)) {
                    totalExpenses += expense.total
                }
            } else {
                val calendar = Calendar.getInstance()
                calendar.time = Date(expense.dateCreated)
                if (calendar[Calendar.MONTH] == selection) {
                    totalExpenses += expense.total
                }
            }
        }
        var totalNotesSalePaid = 0.0
        for (noteSale in notesSalePaid) {
            if (isByRange) {
                val dateNoteSale = Date(noteSale.datePaid)
                if (dateNoteSale.after(calendarStartRange.time) && dateNoteSale.before(calendarFinalRange.time)) {
                    totalNotesSalePaid += if (noteSale.isDolar) {
                        noteSale.quantity * noteSale.price
                    } else {
                        (noteSale.quantity * noteSale.price) / noteSale.rateDelivery
                    }
                }
            } else {
                val calendar = Calendar.getInstance()
                calendar.time = Date(noteSale.datePaid)
                if (calendar[Calendar.MONTH] == selection) {
                    totalNotesSalePaid += if (noteSale.isDolar) {
                        noteSale.quantity * noteSale.price
                    } else {
                        (noteSale.quantity * noteSale.price) / noteSale.rateDelivery
                    }
                }
            }
        }

        var totalNotesSaleNotPaid = 0.0
        for (noteSale in notesSaleNotPaid) {
            if (isByRange) {
                val dateNoteSale = Date(noteSale.datePaid)
                if (dateNoteSale.after(calendarStartRange.time) && dateNoteSale.before(calendarFinalRange.time)) {
                    totalNotesSaleNotPaid += if (noteSale.isDolar) {
                        noteSale.quantity * noteSale.price
                    } else {
                        (noteSale.quantity * noteSale.price) / noteSale.rateDelivery
                    }
                }
            } else {
                val calendar = Calendar.getInstance()
                calendar.time = Date(noteSale.datePaid)
                if (calendar[Calendar.MONTH] == selection) {
                    totalNotesSaleNotPaid += if (noteSale.isDolar) {
                        noteSale.quantity * noteSale.price
                    } else {
                        (noteSale.quantity * noteSale.price) / noteSale.rateDelivery
                    }
                }
            }
        }

        var totalRefunds = 0.0
        for (refund in refunds) {
            if (isByRange) {
                val dateRefund = Date(refund.date)
                if (dateRefund.after(calendarStartRange.time) && dateRefund.before(calendarFinalRange.time)) {
                    totalRefunds += if (refund.isDolar) {
                        (refund.quantity * refund.price)
                    } else {
                        (refund.quantity * refund.price) / refund.rate
                    }
                }
            } else {
                val calendar = Calendar.getInstance()
                calendar.time = Date(refund.date)
                if (calendar[Calendar.MONTH] == selection) {
                    totalRefunds += if (refund.isDolar) {
                        (refund.quantity * refund.price)
                    } else {
                        (refund.quantity * refund.price) / refund.rate
                    }
                }
            }
        }

        val pieEntries = mutableListOf<PieEntry>()
        pieEntries.add(PieEntry(totalSalesPaid.toFloat(), getString(R.string.text_sales_paid)))
        pieEntries.add(PieEntry(totalNotesSalePaid.toFloat(), getString(R.string.text_note_sale_paid_graph)))
        pieEntries.add(PieEntry(totalSalesNotPaid.toFloat(), getString(R.string.text_sales_not_paid)))
        pieEntries.add(PieEntry(totalNotesSaleNotPaid.toFloat(), getString(R.string.text_note_sale_not_paid_graph)))
        //pieEntries.add(PieEntry(totalWaste.toFloat(), getString(R.string.text_waste)))
        pieEntries.add(PieEntry(totalExpenses.toFloat(), getString(R.string.title_expenses)))
        pieEntries.add(PieEntry(totalRefunds.toFloat(), getString(R.string.title_refunds)))

        val pieDataSet = PieDataSet(pieEntries, "")
        pieDataSet.valueTextSize = 18f
        pieDataSet.setColors(
            ContextCompat.getColor(requireContext(), R.color.green_light),
            ContextCompat.getColor(requireContext(), R.color.purple),
            ContextCompat.getColor(requireContext(), R.color.red_light),
            ContextCompat.getColor(requireContext(), R.color.yellow),
            ContextCompat.getColor(requireContext(), R.color.blue),
            ContextCompat.getColor(requireContext(), R.color.mostaza)
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
        pieBalance.legend.isWordWrapEnabled = true
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