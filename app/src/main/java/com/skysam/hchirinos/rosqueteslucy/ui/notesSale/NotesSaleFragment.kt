package com.skysam.hchirinos.rosqueteslucy.ui.notesSale

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Costumer
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.NoteSale
import com.skysam.hchirinos.rosqueteslucy.databinding.FragmentNotesSaleBinding

class NotesSaleFragment : Fragment(), OnClick, SearchView.OnQueryTextListener {

    private var _binding: FragmentNotesSaleBinding? = null
    private val binding get() = _binding!!
    private val viewModel: NotesSaleViewModel by activityViewModels()
    private lateinit var adapaterNoteSale: NoteSaleAdapter
    private val notesSale = mutableListOf<NoteSale>()
    private val costumers = mutableListOf<Costumer>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotesSaleBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        requireActivity().menuInflater.inflate(R.menu.menu_top_bar_main, menu)
        val item = menu.findItem(R.id.action_search)
        val search = item.actionView as SearchView
        search.setOnQueryTextListener(this)
    }

    private fun loadViewModel() {
        viewModel.notesSales.observe(viewLifecycleOwner, {
            if (_binding != null) {
                if (it.isNotEmpty()) {
                    notesSale.clear()
                    notesSale.addAll(it)
                    adapaterNoteSale.updateList(notesSale)
                    binding.rvNoteSales.visibility = View.VISIBLE
                    binding.textListEmpty.visibility = View.GONE
                } else {
                    binding.rvNoteSales.visibility = View.GONE
                    binding.textListEmpty.visibility = View.VISIBLE
                }
                binding.progressBar.visibility = View.GONE
            }
        })
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

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        val listSearch = mutableListOf<NoteSale>()
        if (notesSale.isEmpty()) {
            Toast.makeText(context, getString(R.string.list_note_sale_empty), Toast.LENGTH_SHORT).show()
        } else {
            val userInput: String = newText!!.lowercase()
            listSearch.clear()

            for (noteSale in notesSale) {
                if (noteSale.nameCostumer.lowercase().contains(userInput) ||
                        noteSale.location.lowercase().contains(userInput)) {
                    listSearch.add(noteSale)
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
        return true
    }
}