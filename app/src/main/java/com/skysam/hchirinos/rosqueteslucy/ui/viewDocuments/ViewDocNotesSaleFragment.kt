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
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.NoteSale
import com.skysam.hchirinos.rosqueteslucy.databinding.FragmentViewDocumentNoteSaleBinding
import com.skysam.hchirinos.rosqueteslucy.ui.notesSale.ViewDetailsNoteSaleDialog
import com.skysam.hchirinos.rosqueteslucy.ui.notesSale.pages.NoteSaleAdapter
import com.skysam.hchirinos.rosqueteslucy.ui.notesSale.pages.OnClick


class ViewDocNotesSaleFragment : Fragment(), OnClick {

  private var _binding: FragmentViewDocumentNoteSaleBinding? = null
  private val binding get() = _binding!!
  private val viewModel: ViewDocumentsViewModel by activityViewModels()
  private lateinit var adapaterNoteSale: NoteSaleAdapter
  private val allNotesSale = mutableListOf<NoteSale>()
  private val notesSale = mutableListOf<NoteSale>()
  private lateinit var costumer: Costumer
  private var location = Constants.ALL_LOCATIONS

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentViewDocumentNoteSaleBinding.inflate(inflater, container, false)
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
    adapaterNoteSale = NoteSaleAdapter(notesSale, this)
    binding.rvNoteSales.apply {
      setHasFixedSize(true)
      adapter = adapaterNoteSale
    }
    loadViewModel()
  }

  private fun loadViewModel() {
    viewModel.costumer.observe(viewLifecycleOwner, {
      if (_binding != null) {
        costumer = it
      }
    })
    viewModel.allNotesSales.observe(viewLifecycleOwner, {
      if (_binding != null) {
        allNotesSale.clear()
        allNotesSale.addAll(it)
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
    notesSale.clear()
    when(location) {
      Constants.ALL_LOCATIONS -> {
        for (noteSale in allNotesSale) {
          if (noteSale.idCostumer == costumer.id) notesSale.add(noteSale)
        }
      }
      else -> {
        for (noteSale in allNotesSale) {
          if (noteSale.idCostumer == costumer.id && noteSale.location == location) notesSale.add(noteSale)
        }
      }
    }
    if (notesSale.isNotEmpty()) {
      adapaterNoteSale.updateList(notesSale)
      binding.rvNoteSales.visibility = View.VISIBLE
      binding.textListEmpty.visibility = View.GONE
    } else {
      binding.rvNoteSales.visibility = View.GONE
      binding.textListEmpty.visibility = View.VISIBLE
    }
    binding.progressBar.visibility = View.GONE
  }

  override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

  override fun viewNoteSale(noteSale: NoteSale) {
    noteSale.idCostumer = costumer.identifier
    val viewDetailsNoteSaleDialog = ViewDetailsNoteSaleDialog(noteSale)
    viewDetailsNoteSaleDialog.show(requireActivity().supportFragmentManager, tag)
  }

  override fun deleteNoteSale(noteSale: NoteSale) {
    val builder = AlertDialog.Builder(requireActivity())
    builder.setTitle(getString(R.string.title_confirmation_dialog))
      .setMessage(getString(R.string.msg_delete_dialog))
      .setPositiveButton(R.string.text_delete) { _, _ ->
        Toast.makeText(requireContext(), R.string.text_deleting, Toast.LENGTH_SHORT).show()
        viewModel.deleteNoteSale(noteSale)
      }
      .setNegativeButton(R.string.btn_cancel, null)

    val dialog = builder.create()
    dialog.show()
  }
}