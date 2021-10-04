package com.skysam.hchirinos.rosqueteslucy.ui.suppliers

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Supplier
import com.skysam.hchirinos.rosqueteslucy.databinding.FragmentSuppliersBinding
import com.skysam.hchirinos.rosqueteslucy.ui.expenses.AddExpenseDialog

class SuppliersFragment : Fragment(), OnClick, SearchView.OnQueryTextListener {

    private var _binding: FragmentSuppliersBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SuppliersViewModel by activityViewModels()
    private lateinit var adaterSupplier: SuppliersAdapter
    private lateinit var search: SearchView
    private val suppliers = mutableListOf<Supplier>()
    private val listSearch = mutableListOf<Supplier>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSuppliersBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adaterSupplier = SuppliersAdapter(suppliers, this)
        binding.rvSuppliers.apply {
            setHasFixedSize(true)
            adapter = adaterSupplier
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
            addOnScrollListener(object: RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    if (dy > 0) {
                        binding.floatingActionButton.hide()
                    } else {
                        binding.floatingActionButton.show()
                    }
                    super.onScrolled(recyclerView, dx, dy)
                }
            })
        }
        binding.floatingActionButton.setOnClickListener {
            val addSupplierDialog = AddSupplierDialog()
            addSupplierDialog.show(requireActivity().supportFragmentManager, tag)
        }
        loadViewModel()
    }

    private fun loadViewModel() {
        viewModel.suppliers.observe(viewLifecycleOwner, {
            if (_binding != null) {
                binding.progressBar.visibility = View.GONE
                if (it.isEmpty()) {
                    binding.textListEmpty.visibility = View.VISIBLE
                    binding.rvSuppliers.visibility = View.GONE
                } else {
                    suppliers.clear()
                    suppliers.addAll(it)
                    adaterSupplier.updateList(suppliers)
                    binding.textListEmpty.visibility = View.GONE
                    binding.rvSuppliers.visibility = View.VISIBLE
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        binding.floatingActionButton.show()
    }

    override fun onPause() {
        super.onPause()
        binding.floatingActionButton.hide()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_top_bar_main, menu)
        val item = menu.findItem(R.id.action_search)
        search = item.actionView as SearchView
        search.setOnQueryTextListener(this)
    }

    override fun addExpense(supplier: Supplier) {
        val addExpenseDialog = AddExpenseDialog(supplier)
        addExpenseDialog.show(requireActivity().supportFragmentManager, tag)
    }

    override fun deleteExpense(supplier: Supplier) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_confirmation_dialog))
            .setMessage(getString(R.string.msg_delete_dialog))
            .setPositiveButton(R.string.text_delete) { _, _ ->
                Toast.makeText(requireContext(), R.string.text_deleting, Toast.LENGTH_SHORT).show()
                viewModel.deleteSupplier(supplier)
            }
            .setNegativeButton(R.string.btn_cancel, null)

        val dialog = builder.create()
        dialog.show()
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (suppliers.isEmpty()) {
            Toast.makeText(context, getString(R.string.list_supplier_empty), Toast.LENGTH_SHORT).show()
        } else {
            val userInput: String = newText!!.lowercase()
            listSearch.clear()

            for (supplier in suppliers) {
                if (supplier.name.lowercase().contains(userInput)) {
                    listSearch.add(supplier)
                }
            }
            if (listSearch.isEmpty()) {
                binding.lottieAnimationView.visibility = View.VISIBLE
                binding.lottieAnimationView.playAnimation()
            } else {
                binding.lottieAnimationView.visibility = View.GONE
            }
            adaterSupplier.updateList(listSearch)
        }
        return true
    }

}