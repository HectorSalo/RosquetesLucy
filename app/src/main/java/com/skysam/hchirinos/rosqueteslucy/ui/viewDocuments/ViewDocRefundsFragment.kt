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
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Customer
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Refund
import com.skysam.hchirinos.rosqueteslucy.databinding.FragmentViewDocumentRefundBinding
import com.skysam.hchirinos.rosqueteslucy.ui.refunds.OnClick
import com.skysam.hchirinos.rosqueteslucy.ui.refunds.RefundsAdapter
import com.skysam.hchirinos.rosqueteslucy.ui.refunds.ViewDetailsRefundDialog

class ViewDocRefundsFragment : Fragment(), OnClick {
  private var _binding: FragmentViewDocumentRefundBinding? = null
  private val binding get() = _binding!!
  private val viewModel: ViewDocumentsViewModel by activityViewModels()
  private lateinit var refundsAdapter: RefundsAdapter
  private val allRefunds = mutableListOf<Refund>()
  private val refunds = mutableListOf<Refund>()
  private lateinit var customer: Customer
  private var location = Constants.ALL_LOCATIONS

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentViewDocumentRefundBinding.inflate(inflater, container, false)
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
    refundsAdapter = RefundsAdapter(refunds, this)
    binding.rvRefunds.apply {
      setHasFixedSize(true)
      adapter = refundsAdapter
    }
    loadViewModel()
  }

  private fun loadViewModel() {
    viewModel.customer.observe(viewLifecycleOwner, {
      if (_binding != null) {
        customer = it
      }
    })
    viewModel.allRefunds.observe(viewLifecycleOwner, {
      if (_binding != null) {
        allRefunds.clear()
        allRefunds.addAll(it)
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
    refunds.clear()
    when(location) {
      Constants.ALL_LOCATIONS -> {
        for (refund in allRefunds) {
          if (refund.idCostumer == customer.id) refunds.add(refund)
        }
      }
      else -> {
        for (refund in allRefunds) {
          if (refund.idCostumer == customer.id && refund.location == location) refunds.add(refund)
        }
      }
    }
    if (refunds.isNotEmpty()) {
      refundsAdapter.updateList(refunds)
      binding.rvRefunds.visibility = View.VISIBLE
      binding.textListEmpty.visibility = View.GONE
    } else {
      binding.rvRefunds.visibility = View.GONE
      binding.textListEmpty.visibility = View.VISIBLE
    }
    binding.progressBar.visibility = View.GONE
  }

  override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

  override fun viewDetails(refund: Refund) {
    val viewDetailsRefundDialog = ViewDetailsRefundDialog(refund)
    viewDetailsRefundDialog.show(requireActivity().supportFragmentManager, tag)
  }

  override fun delete(refund: Refund) {
    val builder = AlertDialog.Builder(requireActivity())
    builder.setTitle(getString(R.string.title_confirmation_dialog))
      .setMessage(getString(R.string.msg_delete_dialog))
      .setPositiveButton(R.string.text_delete) { _, _ ->
        Toast.makeText(requireContext(), R.string.text_deleting, Toast.LENGTH_SHORT).show()
        viewModel.deleteRefund(refund)
      }
      .setNegativeButton(R.string.btn_cancel, null)

    val dialog = builder.create()
    dialog.show()
  }
}