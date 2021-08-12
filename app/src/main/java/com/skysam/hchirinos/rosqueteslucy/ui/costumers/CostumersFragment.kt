package com.skysam.hchirinos.rosqueteslucy.ui.costumers

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Costumer
import com.skysam.hchirinos.rosqueteslucy.databinding.FragmentCostumersBinding
import java.util.*

class CostumersFragment : Fragment(), OnClick {

    private lateinit var viewModel: CostumersViewModel
    private var _binding: FragmentCostumersBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapterCostumer: CostumersAdapter
    private val costumers = mutableListOf<Costumer>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel =
            ViewModelProvider(this).get(CostumersViewModel::class.java)

        _binding = FragmentCostumersBinding.inflate(inflater, container, false)
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

    override fun addLocation(costumer: Costumer) {
        val addLocationDialog = AddLocationDialog(costumer)
        addLocationDialog.show(requireActivity().supportFragmentManager, tag)
    }

    override fun deleteLocation(costumer: Costumer) {
        val locations = mutableListOf<String>()
        val arrayLocations = costumer.locations.toTypedArray()
        val arrayChecked = BooleanArray(costumer.locations.size)
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_delete_locations))
            .setMultiChoiceItems(arrayLocations, arrayChecked) { _, which, isChecked ->
                if (isChecked) {
                    locations.add(arrayLocations[which])
                } else {
                    locations.remove(arrayLocations[which])
                }
            }
            .setPositiveButton(R.string.text_delete, null)
            .setNegativeButton(R.string.btn_cancel, null)

        val dialog = builder.create()
        dialog.show()
        val buttonPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        buttonPositive.setOnClickListener {
            if (locations.size == costumer.locations.size) {
                Toast.makeText(requireContext(), getString(R.string.error_delete_all_locations), Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            Toast.makeText(requireContext(), getString(R.string.text_deleting), Toast.LENGTH_LONG).show()
            dialog.dismiss()
            for (loc in locations) {
                costumer.locations.remove(loc)
            }
            viewModel.deleteLocations(costumer.id, locations)
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
}