package com.skysam.hchirinos.rosqueteslucy.ui.expenses

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.ClassesCommon
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Expense
import com.skysam.hchirinos.rosqueteslucy.databinding.DialogViewExpenseBinding
import java.text.DateFormat
import java.util.*

/**
 * Created by Hector Chirinos (Home) on 3/10/2021.
 */
class ViewExpenseDialog(private val expense: Expense): DialogFragment() {
    private var _binding: DialogViewExpenseBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ViewExpenseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ShapeAppearanceOverlay_MaterialComponents_MaterialCalendar_Window_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogViewExpenseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.progressBar.visibility = View.VISIBLE
        loadData()
    }

    private fun loadData() {
        binding.tvNameList.text = expense.nameSupplier
        binding.tvDate.text = DateFormat.getDateInstance().format(Date(expense.dateCreated))
        binding.tvRate.text = getString(R.string.text_rate_view,
            ClassesCommon.convertDoubleToString(expense.rate))
        binding.tvTotal.text = getString(R.string.text_total_dolar_expense,
            ClassesCommon.convertDoubleToString(expense.total))
        adapter = ViewExpenseAdapter(expense.listProducts.sortedWith(compareBy { it.name }).toMutableList())
        binding.rvList.adapter = adapter
        binding.rvList.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        binding.progressBar.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}