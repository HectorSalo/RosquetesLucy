package com.skysam.hchirinos.rosqueteslucy.ui.refunds

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Refund
import com.skysam.hchirinos.rosqueteslucy.databinding.FragmentRefundsBinding

class RefundsFragment : Fragment(), OnClick, SearchView.OnQueryTextListener {

    private var _binding: FragmentRefundsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RefundsViewModel by activityViewModels()
    private lateinit var refundsAdapter: RefundsAdapter
    private val refunds = mutableListOf<Refund>()
    private lateinit var search: SearchView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRefundsBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refundsAdapter = RefundsAdapter(refunds, this)
        binding.rvRefunds.apply {
            setHasFixedSize(true)
            adapter = refundsAdapter
        }
        loadViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        requireActivity().menuInflater.inflate(R.menu.menu_top_bar_expense, menu)
        val item = menu.findItem(R.id.action_search)
        search = item.actionView as SearchView
        search.setOnQueryTextListener(this)
    }

    private fun loadViewModel() {
        viewModel.refunds.observe(viewLifecycleOwner, {
            if (_binding != null) {
                refunds.clear()
                if (it.isNotEmpty()) {
                    refunds.addAll(it)
                    refundsAdapter.updateList(refunds)
                    binding.rvRefunds.visibility = View.VISIBLE
                    binding.textListEmpty.visibility = View.GONE
                } else {
                    binding.rvRefunds.visibility = View.GONE
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

    override fun viewDetails(refund: Refund) {
        val viewDetailsRefundDialog = ViewDetailsRefundDialog(refund)
        viewDetailsRefundDialog.show(requireActivity().supportFragmentManager, tag)
    }

    override fun edit(refund: Refund) {

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

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        val listSearch = mutableListOf<Refund>()
        if (refunds.isEmpty()) {
            Toast.makeText(context, getString(R.string.list_refunds_empty), Toast.LENGTH_SHORT).show()
        } else {
            val userInput: String = newText!!.lowercase()
            listSearch.clear()

            for (refund in refunds) {
                if (refund.nameCostumer.lowercase().contains(userInput) ||
                    refund.location.lowercase().contains(userInput)) {
                    listSearch.add(refund)
                }
            }
            if (listSearch.isEmpty()) {
                binding.lottieAnimationView.visibility = View.VISIBLE
                binding.lottieAnimationView.playAnimation()
            } else {
                binding.lottieAnimationView.visibility = View.GONE
            }
            refundsAdapter.updateList(listSearch)
        }
        return true
    }


}