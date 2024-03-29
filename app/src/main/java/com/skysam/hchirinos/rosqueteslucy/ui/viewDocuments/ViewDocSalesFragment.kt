package com.skysam.hchirinos.rosqueteslucy.ui.viewDocuments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.Constants
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Costumer
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Sale
import com.skysam.hchirinos.rosqueteslucy.databinding.FragmentViewDocumentSaleBinding
import com.skysam.hchirinos.rosqueteslucy.ui.sales.pages.OnClick
import com.skysam.hchirinos.rosqueteslucy.ui.sales.pages.SalesAdapter


class ViewDocSalesFragment: Fragment(), OnClick {

  private var _binding: FragmentViewDocumentSaleBinding? = null
  private val binding get() = _binding!!
  private val viewModel: ViewDocumentsViewModel by activityViewModels()
  private lateinit var adapaterSales: SalesAdapter
  private val allSales = mutableListOf<Sale>()
  private val sales = mutableListOf<Sale>()
  private lateinit var costumer: Costumer
  private var location = Constants.ALL_LOCATIONS

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentViewDocumentSaleBinding.inflate(inflater, container, false)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val callback = object : OnBackPressedCallback(true) {
      override fun handleOnBackPressed() {
        requireActivity().finish()
      }
    }
    requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

    adapaterSales = SalesAdapter(sales, this)
    binding.rvSales.apply {
      setHasFixedSize(true)
      adapter = adapaterSales
    }
    loadViewModel()
  }

  private fun loadViewModel() {
    viewModel.costumer.observe(viewLifecycleOwner, {
      if (_binding != null) {
        costumer = it
      }
    })
    viewModel.allSales.observe(viewLifecycleOwner, {
      if (_binding != null) {
        allSales.clear()
        allSales.addAll(it)
        fillList()
      }
    })
    viewModel.location.observe(viewLifecycleOwner, {
      if (_binding != null) {
        location = it
        fillList()
      }
    })
  }

  private fun fillList() {
    sales.clear()
    when(location) {
      Constants.ALL_LOCATIONS -> {
        for (sale in allSales) {
          if (sale.idCostumer == costumer.id) sales.add(sale)
        }
      }
      else -> {
        for (sale in allSales) {
          if (sale.idCostumer == costumer.id && sale.location == location) sales.add(sale)
        }
      }
    }
    if (sales.isNotEmpty()) {
      adapaterSales.updateList(sales)
      binding.rvSales.visibility = View.VISIBLE
      binding.textListEmpty.visibility = View.GONE
    } else {
      binding.rvSales.visibility = View.GONE
      binding.textListEmpty.visibility = View.VISIBLE
    }
    binding.progressBar.visibility = View.GONE
  }

  override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

  override fun viewSale(sale: Sale) {
    val viewDetailsSale = ViewDetailsSaleCostumerDialog(sale, costumer)
    viewDetailsSale.show(requireActivity().supportFragmentManager, tag)
  }

  override fun optionsSale(sale: Sale) {
    val builder = AlertDialog.Builder(requireActivity())
    builder.setTitle(getString(R.string.title_dialog_options_sale))
      .setMessage(getString(R.string.message_dialog_delete_sale))
      .setPositiveButton(R.string.text_delete_sale) { _, _ ->
        Toast.makeText(requireContext(), R.string.text_deleting, Toast.LENGTH_SHORT).show()
        viewModel.deleteSale(sale)
      }
      .setNeutralButton(R.string.text_annul_sale) {_, _ ->
        if (!sale.isAnnuled) viewModel.annulSale(sale)
      }

    val dialog = builder.create()
    dialog.show()
  }
}