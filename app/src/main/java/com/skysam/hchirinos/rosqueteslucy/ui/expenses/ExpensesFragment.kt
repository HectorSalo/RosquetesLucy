package com.skysam.hchirinos.rosqueteslucy.ui.expenses

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.util.Pair
import androidx.fragment.app.activityViewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Expense
import com.skysam.hchirinos.rosqueteslucy.databinding.FragmentExpensesBinding
import java.util.*

class ExpensesFragment : Fragment(), OnClick, SearchView.OnQueryTextListener {

    private var _binding: FragmentExpensesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ExpensesViewModel by activityViewModels()
    private lateinit var adapterExpense: ExpensesAdapter
    private val expenses = mutableListOf<Expense>()
    private val expensesFilter = mutableListOf<Expense>()
    private lateinit var search: SearchView
    private var dateStart: Date? = null
    private var dateFinal: Date? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExpensesBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapterExpense = ExpensesAdapter(expenses, this)
        binding.rvExpenses.apply {
            setHasFixedSize(true)
            adapter = adapterExpense
        }
        binding.fabClear?.setOnClickListener {
            binding.fabClear?.hide()
            binding.lottieAnimationView.visibility = View.GONE
            if (expenses.isNotEmpty()) {
                adapterExpense.updateList(expenses)
                binding.rvExpenses.visibility = View.VISIBLE
                binding.textListEmpty.visibility = View.GONE
            } else {
                binding.rvExpenses.visibility = View.GONE
                binding.textListEmpty.visibility = View.VISIBLE
            }
        }
        loadViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        requireActivity().menuInflater.inflate(R.menu.menu_top_bar_expense, menu)
        val itemFilter = menu.findItem(R.id.action_filter)
        itemFilter.setOnMenuItemClickListener {
            selecDate()
            true
        }
        val item = menu.findItem(R.id.action_search)
        search = item.actionView as SearchView
        search.setOnQueryTextListener(this)
    }

    private fun loadViewModel() {
        viewModel.expenses.observe(viewLifecycleOwner, {
            if (_binding != null) {
                expenses.clear()
                if (it.isNotEmpty()) {
                    expenses.addAll(it)
                    adapterExpense.updateList(expenses)
                    binding.rvExpenses.visibility = View.VISIBLE
                    binding.textListEmpty.visibility = View.GONE
                } else {
                    binding.rvExpenses.visibility = View.GONE
                    binding.textListEmpty.visibility = View.VISIBLE
                }
                binding.progressBar.visibility = View.GONE
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
            filterList()
        }
        picker.show(requireActivity().supportFragmentManager, picker.toString())
    }

    private fun filterList() {
        binding.fabClear?.show()
        val calendarStartRange = Calendar.getInstance()
        val calendarFinalRange = Calendar.getInstance()
        calendarStartRange.time = dateStart!!
        calendarFinalRange.time = dateFinal!!
        expensesFilter.clear()
        for (expense in expenses) {
            val dateExpense = Date(expense.dateCreated)
            if (dateExpense.after(calendarStartRange.time) && dateExpense.before(calendarFinalRange.time)) {
                expensesFilter.add(expense)
            }
        }
        if (expensesFilter.isEmpty()) {
            binding.lottieAnimationView.visibility = View.VISIBLE
            binding.lottieAnimationView.playAnimation()
        } else {
            binding.lottieAnimationView.visibility = View.GONE
        }
        adapterExpense.updateList(expensesFilter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun viewExpense(expense: Expense) {
        val viewExpenseDialog = ViewExpenseDialog(expense)
        viewExpenseDialog.show(requireActivity().supportFragmentManager, tag)
    }

    override fun edit(expense: Expense) {
        /*val editExpenseDialog = EditExpenseDialog(expense)
        editExpenseDialog.show(requireActivity().supportFragmentManager, tag)*/
    }

    override fun delete(expense: Expense) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_confirmation_dialog))
            .setMessage(getString(R.string.msg_delete_dialog))
            .setPositiveButton(R.string.text_delete) { _, _ ->
                Toast.makeText(requireContext(), R.string.text_deleting, Toast.LENGTH_SHORT).show()
                viewModel.deleteExpense(expense)
                binding.fabClear?.hide()
            }
            .setNegativeButton(R.string.btn_cancel, null)

        val dialog = builder.create()
        dialog.show()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        val listSearch = mutableListOf<Expense>()
        if (expenses.isEmpty()) {
            Toast.makeText(context, getString(R.string.list_expenses_empty), Toast.LENGTH_SHORT).show()
        } else {
            val userInput: String = newText!!.lowercase()
            listSearch.clear()

            for (expense in expenses) {
                if (expense.nameSupplier.lowercase().contains(userInput)) {
                    listSearch.add(expense)
                }
            }
            if (listSearch.isEmpty()) {
                binding.lottieAnimationView.visibility = View.VISIBLE
                binding.lottieAnimationView.playAnimation()
            } else {
                binding.lottieAnimationView.visibility = View.GONE
            }
            adapterExpense.updateList(listSearch)
        }
        return true
    }
}