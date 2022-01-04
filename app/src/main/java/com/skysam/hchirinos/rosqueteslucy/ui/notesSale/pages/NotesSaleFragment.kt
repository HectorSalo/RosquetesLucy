package com.skysam.hchirinos.rosqueteslucy.ui.notesSale.pages

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Costumer
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.NoteSale
import com.skysam.hchirinos.rosqueteslucy.databinding.FragmentNotesSaleBinding
import com.skysam.hchirinos.rosqueteslucy.ui.notesSale.NotesSaleViewModel
import com.skysam.hchirinos.rosqueteslucy.ui.notesSale.ViewDetailsNoteSaleDialog

class NotesSaleFragment : Fragment(), OnClick {

    private var _binding: FragmentNotesSaleBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NotesSaleViewModel by activityViewModels()
    private lateinit var adapaterNoteSale: NoteSaleAdapter
    private val notesSale = mutableListOf<NoteSale>()
    private val notesSalePaid = mutableListOf<NoteSale>()
    private val notesSaleNotPaid = mutableListOf<NoteSale>()
    private val costumers = mutableListOf<Costumer>()
    private var index = 0

    companion object {
        private const val ARG_SECTION_NUMBER = "section_number"

        @JvmStatic
        fun newInstance(sectionNumber: Int): NotesSaleFragment {
            return NotesSaleFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesSaleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapaterNoteSale = NoteSaleAdapter(notesSale, this)
        binding.rvNoteSales.apply {
            setHasFixedSize(true)
            adapter = adapaterNoteSale
        }
        loadViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun loadViewModel() {
        viewModel.costumers.observe(viewLifecycleOwner, {
            costumers.clear()
            costumers.addAll(it)
        })
        viewModel.notesSales.observe(viewLifecycleOwner, {
            if (_binding != null) {
                notesSale.clear()
                notesSaleNotPaid.clear()
                notesSalePaid.clear()
                if (it.isNotEmpty()) {
                    notesSale.addAll(it)
                    for (noteSale in notesSale) {
                        if (noteSale.isPaid) notesSalePaid.add(noteSale) else notesSaleNotPaid.add(noteSale)
                    }
                    loadList(index)
                } else {
                    binding.rvNoteSales.visibility = View.GONE
                    binding.textListEmpty.visibility = View.VISIBLE
                }
                binding.progressBar.visibility = View.GONE
            }
        })
        viewModel.indexPage.observe(viewLifecycleOwner, {
            index = it
            loadList(it)
        })
        viewModel.textSearch.observe(viewLifecycleOwner, {
            searchFromText(it)
        })
    }

    private fun loadList(index: Int) {
        when(index) {
            0 -> {
                if (notesSaleNotPaid.isNotEmpty()) {
                    adapaterNoteSale.updateList(notesSaleNotPaid)
                    binding.rvNoteSales.visibility = View.VISIBLE
                    binding.textListEmpty.visibility = View.GONE
                } else {
                    binding.rvNoteSales.visibility = View.GONE
                    binding.textListEmpty.visibility = View.VISIBLE
                    binding.textListEmpty.text = getString(R.string.list_notes_sale_not_paid_empty)
                }
            }
            1 -> {
                if (notesSalePaid.isNotEmpty()) {
                    adapaterNoteSale.updateList(notesSalePaid)
                    binding.rvNoteSales.visibility = View.VISIBLE
                    binding.textListEmpty.visibility = View.GONE
                } else {
                    binding.rvNoteSales.visibility = View.GONE
                    binding.textListEmpty.visibility = View.VISIBLE
                    binding.textListEmpty.text = getString(R.string.list_notes_sales_paid_empty)
                }
            }
            2 -> {
                if (notesSale.isNotEmpty()) {
                    adapaterNoteSale.updateList(notesSale)
                    binding.rvNoteSales.visibility = View.VISIBLE
                    binding.textListEmpty.visibility = View.GONE
                } else {
                    binding.rvNoteSales.visibility = View.GONE
                    binding.textListEmpty.visibility = View.VISIBLE
                    binding.textListEmpty.text = getString(R.string.list_note_sale_empty)
                }
            }
        }
    }

    override fun viewNoteSale(noteSale: NoteSale) {
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

    private fun searchFromText(text: String) {
        val listSearch = mutableListOf<NoteSale>()
        if (notesSale.isNotEmpty()) {
            val userInput: String = text.lowercase()
            listSearch.clear()

            when(index) {
                0-> {
                    for (noteSale in notesSaleNotPaid) {
                        if (noteSale.nameCostumer.lowercase().contains(userInput) || noteSale.location.lowercase().contains(userInput)) {
                            listSearch.add(noteSale)
                        }
                    }
                }
                1-> {
                    for (noteSale in notesSalePaid) {
                        if (noteSale.nameCostumer.lowercase().contains(userInput) || noteSale.location.lowercase().contains(userInput)) {
                            listSearch.add(noteSale)
                        }
                    }
                }
                2-> {
                    for (noteSale in notesSale) {
                        if (noteSale.nameCostumer.lowercase().contains(userInput) || noteSale.location.lowercase().contains(userInput)) {
                            listSearch.add(noteSale)
                        }
                    }
                }
            }

            if (listSearch.isEmpty()) {
                binding.lottieAnimationView.visibility = View.VISIBLE
                binding.lottieAnimationView.playAnimation()
            } else {
                binding.lottieAnimationView.visibility = View.GONE
            }
            adapaterNoteSale.updateList(listSearch)
        }
    }
}