package com.skysam.hchirinos.rosqueteslucy.ui.costumers

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.Constants
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Costumer
import com.skysam.hchirinos.rosqueteslucy.databinding.FragmentCostumersBinding
import com.skysam.hchirinos.rosqueteslucy.ui.refunds.AddRefundDialog
import com.skysam.hchirinos.rosqueteslucy.ui.viewDocuments.ViewDocumentsActivity
import java.util.*

class CostumersFragment : Fragment(), OnClick, SearchView.OnQueryTextListener{

    private lateinit var viewModel: CostumersViewModel
    private var _binding: FragmentCostumersBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapterCostumer: CostumersAdapter
    private val costumers = mutableListOf<Costumer>()
    private val listSearch = mutableListOf<Costumer>()
    private lateinit var search: SearchView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel =
            ViewModelProvider(this).get(CostumersViewModel::class.java)
        _binding = FragmentCostumersBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapterCostumer = CostumersAdapter(costumers, this)
        binding.rvCostumers.apply {
            setHasFixedSize(true)
            adapter = adapterCostumer
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
            val dialog = AddCostumerDialog()
            dialog.show(requireActivity().supportFragmentManager, tag)
        }
        loadViewModel()
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
        val itemSearch = menu.findItem(R.id.action_search)
        search = itemSearch.actionView as SearchView
        search.setOnQueryTextListener(this)
    }

    private fun loadViewModel() {
        viewModel.costumers.observe(viewLifecycleOwner, {
            if (_binding != null) {
                binding.progressBar.visibility = View.GONE
                if (it.isEmpty()) {
                    binding.textListEmpty.visibility = View.VISIBLE
                    binding.rvCostumers.visibility = View.GONE
                } else {
                    costumers.clear()
                    costumers.addAll(it)
                    adapterCostumer.updateList(costumers)
                    binding.textListEmpty.visibility = View.GONE
                    binding.rvCostumers.visibility = View.VISIBLE
                }
            }
        })
    }

    override fun viewCostumer(costumer: Costumer) {
        val viewDetailsCostumerFragment = ViewDetailsCostumerFragment(costumer)
        viewDetailsCostumerFragment.show(requireActivity().supportFragmentManager, tag)
    }

    override fun deleteLocation(costumer: Costumer) {
        val locationsToDelete = mutableListOf<String>()
        val arrayLocations = costumer.locations.toTypedArray()
        val arrayChecked = BooleanArray(costumer.locations.size)
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_delete_locations))
            .setMultiChoiceItems(arrayLocations, arrayChecked) { _, which, isChecked ->
                if (isChecked) {
                    locationsToDelete.add(arrayLocations[which])
                } else {
                    locationsToDelete.remove(arrayLocations[which])
                }
            }
            .setPositiveButton(R.string.text_delete, null)
            .setNegativeButton(R.string.btn_cancel, null)

        val dialog = builder.create()
        dialog.show()
        val buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        buttonPositive.setOnClickListener {
            if (locationsToDelete.size == costumer.locations.size) {
                Toast.makeText(requireContext(), getString(R.string.error_delete_all_locations), Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            if (locationsToDelete.isEmpty()) {
                dialog.dismiss()
                return@setOnClickListener
            }
            viewModel.deleteLocations(costumer.id, locationsToDelete)
            Toast.makeText(requireContext(), getString(R.string.text_deleting), Toast.LENGTH_LONG).show()
            dialog.dismiss()
        }
    }

    override fun edit(costumer: Costumer) {
        val editCostumerDialog = EditCostumerDialog(costumer)
        editCostumerDialog.show(requireActivity().supportFragmentManager, tag)
    }

    override fun delete(costumer: Costumer) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_confirmation_dialog))
            .setMessage(getString(R.string.msg_delete_dialog))
            .setPositiveButton(R.string.text_delete) { _, _ ->
                Toast.makeText(requireContext(), R.string.text_deleting, Toast.LENGTH_SHORT).show()
                viewModel.deleteCostumer(costumer)
            }
            .setNegativeButton(R.string.btn_cancel, null)

        val dialog = builder.create()
        dialog.show()
    }

    override fun addRefund(costumer: Costumer) {
        val addRefundDialog = AddRefundDialog(costumer)
        addRefundDialog.show(requireActivity().supportFragmentManager, tag)
    }

    override fun viewDocuments(costumer: Costumer) {
        val intent = Intent(requireContext(), ViewDocumentsActivity::class.java)
        intent.putExtra(Constants.ID_COSTUMER, costumer)
        startActivity(intent)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (costumers.isEmpty()) {
            Toast.makeText(context, getString(R.string.list_costumer_empty), Toast.LENGTH_SHORT).show()
        } else {
            val userInput: String = newText!!.lowercase()
            listSearch.clear()

            for (costumer in costumers) {
                if (costumer.name.lowercase().contains(userInput)) {
                    listSearch.add(costumer)
                }
            }
            if (listSearch.isEmpty()) {
                binding.lottieAnimationView.visibility = View.VISIBLE
                binding.lottieAnimationView.playAnimation()
            } else {
                binding.lottieAnimationView.visibility = View.GONE
            }
            adapterCostumer.updateList(listSearch)
        }
        return true
    }
}