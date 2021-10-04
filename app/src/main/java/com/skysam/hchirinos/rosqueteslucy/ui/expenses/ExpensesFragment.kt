package com.skysam.hchirinos.rosqueteslucy.ui.expenses

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Expense
import com.skysam.hchirinos.rosqueteslucy.databinding.FragmentExpensesBinding

class ExpensesFragment : Fragment(), OnClick, SearchView.OnQueryTextListener {

    private var _binding: FragmentExpensesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ExpensesViewModel by activityViewModels()
    private lateinit var adapterExpense: ExpensesAdapter
    private val expenses = mutableListOf<Expense>()
    private lateinit var search: SearchView

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
        loadViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        requireActivity().menuInflater.inflate(R.menu.menu_top_bar_main, menu)
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