package com.skysam.hchirinos.rosqueteslucy.ui.graphs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.ClassesCommon
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Expense
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.NoteSale
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Refund
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Sale
import com.skysam.hchirinos.rosqueteslucy.databinding.DialogReportDayBinding
import java.util.*

/**
 * Created by Hector Chirinos (Home) on 10/10/2021.
 */
class DialogReportDay: DialogFragment() {
    private var _binding: DialogReportDayBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GraphsViewModel by activityViewModels()
    private val salesNotPaid = mutableListOf<Sale>()
    private val salesPaid = mutableListOf<Sale>()
    private val expenses = mutableListOf<Expense>()
    private val notesSalePaid = mutableListOf<NoteSale>()
    private val notesSaleNotPaid = mutableListOf<NoteSale>()
    private val refunds = mutableListOf<Refund>()
    private lateinit var dateStart: Date
    private lateinit var dateFinal: Date

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ShapeAppearanceOverlay_MaterialComponents_MaterialCalendar_Window_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogReportDayBinding.inflate(inflater, container, false)
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
        binding.textView.text = getString(R.string.title_amount_total, "($)")
        loadViewModel()
    }

    private fun loadViewModel() {
        viewModel.sales.observe(viewLifecycleOwner, {
            if (_binding != null) {
                salesNotPaid.clear()
                salesPaid.clear()
                for (sale in it) {
                    if (sale.isPaid) salesPaid.add(sale) else salesNotPaid.add(sale)
                }
                loadData()
            }
        })
        viewModel.expenses.observe(viewLifecycleOwner, {
            if (_binding != null) {
                expenses.clear()
                expenses.addAll(it)
                loadData()
            }
        })
        viewModel.notesSale.observe(viewLifecycleOwner, {
            if (_binding != null) {
                notesSalePaid.clear()
                notesSaleNotPaid.clear()
                for (noteSale in it) {
                    if (noteSale.isPaid) notesSalePaid.add(noteSale) else notesSaleNotPaid.add(noteSale)
                }
                loadData()
            }
        })
        viewModel.refunds.observe(viewLifecycleOwner, {
            if (_binding != null) {
                refunds.clear()
                refunds.addAll(it)
                loadData()
            }
        })
    }

    private fun loadData() {
        val calendarStartRange = Calendar.getInstance()
        val calendarFinalRange = Calendar.getInstance()
        calendarStartRange.time = dateStart
        calendarFinalRange.time = dateFinal
        var totalSalesPaid = 0.0
        for (sale in salesPaid) {
            val dateSale = Date(sale.datePaid)
            if (dateSale.after(calendarStartRange.time) && dateSale.before(calendarFinalRange.time)) {
                val total = sale.quantity * sale.price
                totalSalesPaid += if (sale.isDolar) {
                    total
                } else {
                    total / sale.ratePaid
                }
            }
        }
        var totalSalesNotPaid = 0.0
        for (sale in salesNotPaid) {
            val dateSale = Date(sale.datePaid)
            if (dateSale.after(calendarStartRange.time) && dateSale.before(calendarFinalRange.time)) {
                totalSalesNotPaid += if (sale.isDolar) {
                    (sale.quantity * sale.price)
                } else {
                    (sale.quantity * sale.price) / sale.ratePaid
                }
            }
        }
        var totalExpenses = 0.0
        for (expense in expenses) {
            val dateExpense = Date(expense.dateCreated)
            if (dateExpense.after(calendarStartRange.time) && dateExpense.before(calendarFinalRange.time)) {
                totalExpenses += expense.total
            }
        }
        var totalNotesSalePaid = 0.0
        for (noteSale in notesSalePaid) {
            val dateNoteSale = Date(noteSale.datePaid)
            if (dateNoteSale.after(calendarStartRange.time) && dateNoteSale.before(calendarFinalRange.time)) {
                totalNotesSalePaid += if (noteSale.isDolar) {
                    noteSale.quantity * noteSale.price
                } else {
                    (noteSale.quantity * noteSale.price) / noteSale.rateDelivery
                }
            }
        }

        var totalNotesSaleNotPaid = 0.0
        for (noteSale in notesSaleNotPaid) {
            val dateNoteSale = Date(noteSale.datePaid)
            if (dateNoteSale.after(calendarStartRange.time) && dateNoteSale.before(calendarFinalRange.time)) {
                totalNotesSaleNotPaid += if (noteSale.isDolar) {
                    noteSale.quantity * noteSale.price
                } else {
                    (noteSale.quantity * noteSale.price) / noteSale.rateDelivery
                }
            }
        }

        var totalRefunds = 0.0
        for (refund in refunds) {
            val dateRefund = Date(refund.date)
            if (dateRefund.after(calendarStartRange.time) && dateRefund.before(calendarFinalRange.time)) {
                totalRefunds += if (refund.isDolar) {
                    (refund.quantity * refund.price)
                } else {
                    (refund.quantity * refund.price) / refund.rate
                }
            }
        }

        binding.tvSalePaid.text = getString(R.string.text_amount_add_graph,
            ClassesCommon.convertDoubleToString(totalSalesPaid))

        binding.tvSaleNotPaid.text = getString(R.string.text_amount_add_graph,
            ClassesCommon.convertDoubleToString(totalSalesNotPaid))

        binding.tvNoteSalePaid.text = getString(R.string.text_amount_add_graph,
            ClassesCommon.convertDoubleToString(totalNotesSalePaid))

        binding.tvNoteSaleNotPaid.text = getString(R.string.text_amount_add_graph,
            ClassesCommon.convertDoubleToString(totalNotesSaleNotPaid))

        binding.tvRefunds.text = getString(R.string.text_amount_rest_graph,
            ClassesCommon.convertDoubleToString(totalRefunds))

        binding.tvExpenses.text = getString(R.string.text_amount_rest_graph,
            ClassesCommon.convertDoubleToString(totalExpenses))

        val total = totalSalesPaid + totalSalesNotPaid + totalNotesSalePaid + totalNotesSaleNotPaid -
                totalExpenses - totalRefunds
        if (total >= 0.0) {
            binding.tvTotal.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_second_base_dark))
        }  else {
            binding.tvTotal.setTextColor(ContextCompat.getColor(requireContext(), R.color.red))
        }
        binding.tvTotal.text = getString(R.string.text_total_dolar_expense,
            ClassesCommon.convertDoubleToString(total))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}