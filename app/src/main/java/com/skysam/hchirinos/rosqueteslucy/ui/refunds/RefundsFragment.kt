package com.skysam.hchirinos.rosqueteslucy.ui.refunds

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.ClassesCommon
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Refund
import com.skysam.hchirinos.rosqueteslucy.databinding.FragmentRefundsBinding
import java.util.*


class RefundsFragment : Fragment(), OnClick, SearchView.OnQueryTextListener {

    private var _binding: FragmentRefundsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RefundsViewModel by activityViewModels()
    private lateinit var refundsAdapter: RefundsAdapter
    private val refunds = mutableListOf<Refund>()
    private val refundsFilter = mutableListOf<Refund>()
    private lateinit var search: SearchView
    private var dateStart: Date? = null
    private var dateFinal: Date? = null

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
        binding.fabClear?.setOnClickListener {
            binding.fabClear?.hide()
            binding.lottieAnimationView.visibility = View.GONE
            if (refunds.isNotEmpty()) {
                refundsAdapter.updateList(refunds)
                binding.rvRefunds.visibility = View.VISIBLE
                binding.textListEmpty.visibility = View.GONE
            } else {
                binding.rvRefunds.visibility = View.GONE
                binding.textListEmpty.visibility = View.VISIBLE
            }
            binding.tvRange.visibility = View.GONE
            binding.tvUnits.visibility = View.GONE
            binding.tvTotal.visibility = View.GONE
        }
        loadViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        requireActivity().menuInflater.inflate(R.menu.menu_top_bar_expense, menu)
        val itemFilter = menu.findItem(R.id.action_filter)
        itemFilter.setOnMenuItemClickListener {
            selecDate()
            true
        }
        val item = menu.findItem(R.id.action_search)
        search = item.actionView as SearchView
        search.setOnQueryTextListener(this)
    }

    private fun loadViewModel() {
        viewModel.refunds.observe(viewLifecycleOwner) {
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
                binding.tvRange.visibility = View.GONE
                binding.tvUnits.visibility = View.GONE
                binding.tvTotal.visibility = View.GONE
            }
        }
    }

    private fun selecDate() {
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        val calendar = Calendar.getInstance()

        val picker = builder.build()
        picker.addOnPositiveButtonClickListener { selection: Pair<Long, Long> ->
            val timeZone = TimeZone.getDefault()
            val offset = timeZone.getOffset(Date().time) * -1
            calendar.timeInMillis = selection.first
            calendar.timeInMillis = calendar.timeInMillis + offset
            calendar[Calendar.HOUR_OF_DAY] = 0
            calendar[Calendar.MINUTE] = 0
            dateStart = calendar.time
            calendar.timeInMillis = selection.second
            calendar.timeInMillis = calendar.timeInMillis + offset
            calendar[Calendar.HOUR_OF_DAY] = 23
            calendar[Calendar.MINUTE] = 59
            dateFinal = calendar.time
            filterList()
        }
        picker.show(requireActivity().supportFragmentManager, picker.toString())
    }

    private fun filterList() {
        binding.fabClear?.show()
        val calendarStartRange = Calendar.getInstance()
        val calendarFinalRange = Calendar.getInstance()
        calendarStartRange.time = dateStart!!
        calendarFinalRange.time = dateFinal!!
        refundsFilter.clear()
        for (refund in refunds) {
            val dateRefund = Date(refund.date)
            if (dateRefund.after(calendarStartRange.time) && dateRefund.before(calendarFinalRange.time)) {
                refundsFilter.add(refund)
            }
        }
        if (refundsFilter.isEmpty()) {
            binding.lottieAnimationView.visibility = View.VISIBLE
            binding.tvUnits.visibility = View.GONE
            binding.tvRange.visibility = View.GONE
            binding.tvTotal.visibility = View.GONE
            binding.lottieAnimationView.playAnimation()
        } else {
            var units = 0
            var totalBs = 0.0
            var totalDl = 0.0
            for (ref in refundsFilter) {
                units += ref.quantity
                if (ref.isDolar) {
                    totalBs += (ref.quantity * ref.price) * ref.rate
                    totalDl += ref.quantity * ref.price
                } else {
                    totalBs += ref.quantity * ref.price
                    totalDl += (ref.quantity * ref.price) / ref.rate
                }
            }
            val convert = ClassesCommon.convertDoubleToString(totalDl)
            binding.tvUnits.text = getString(R.string.text_quantity_refund_item, units.toString())
            binding.tvTotal.text = getString(R.string.text_price_convert_item, "Bs.",
                String.format(Locale.GERMANY, "%,.2f", totalBs), convert)
            binding.tvRange.text = getString(R.string.text_date_range,
                ClassesCommon.convertDateToString(dateStart!!),
                ClassesCommon.convertDateToString(dateFinal!!))
            binding.lottieAnimationView.visibility = View.GONE
            binding.tvUnits.visibility = View.VISIBLE
            binding.tvRange.visibility = View.VISIBLE
            binding.tvTotal.visibility = View.VISIBLE
        }
        refundsAdapter.updateList(refundsFilter)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun viewDetails(refund: Refund) {
        viewModel.viewDetailsRefund(refund)
        val viewDetailsRefundDialog = ViewDetailsRefundDialog()
        viewDetailsRefundDialog.show(requireActivity().supportFragmentManager, tag)
    }

    override fun delete(refund: Refund) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_confirmation_dialog))
            .setMessage(getString(R.string.msg_delete_dialog))
            .setPositiveButton(R.string.text_delete) { _, _ ->
                Toast.makeText(requireContext(), R.string.text_deleting, Toast.LENGTH_SHORT).show()
                viewModel.deleteRefund(refund)
                binding.fabClear?.hide()
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