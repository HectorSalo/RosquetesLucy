package com.skysam.hchirinos.rosqueteslucy.ui.production

import android.os.Bundle
import android.view.*
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.datepicker.MaterialDatePicker
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.ClassesCommon
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Production
import com.skysam.hchirinos.rosqueteslucy.databinding.FragmentProductionBinding
import java.util.*

class ProductionFragment : Fragment() {

    private var _binding: FragmentProductionBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ProductionViewModel by activityViewModels()
    private lateinit var productionAdapter: ProductionAdapter
    private val productions = mutableListOf<Production>()
    private val productionsFilter = mutableListOf<Production>()
    private var dateStart: Date? = null
    private var dateFinal: Date? = null
    private lateinit var item: MenuItem

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductionBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        productionAdapter = ProductionAdapter(productions)
        binding.rvProductions.apply {
            setHasFixedSize(true)
            adapter = productionAdapter
        }
        binding.fab.setOnClickListener {
            val addProductionDialog = AddProductionDialog()
            addProductionDialog.show(requireActivity().supportFragmentManager, tag)
        }
        loadViewModel()
    }

    private fun loadViewModel() {
        viewModel.productions.observe(viewLifecycleOwner) {
            if (_binding != null) {
                productions.clear()
                productions.addAll(it)
                productionAdapter.updateList(productions)
                if (productions.isEmpty()) {
                    binding.rvProductions.visibility = View.GONE
                    binding.textListEmpty.visibility = View.VISIBLE
                } else {
                    binding.rvProductions.visibility = View.VISIBLE
                    binding.textListEmpty.visibility = View.GONE
                }
                binding.progressBar.visibility = View.GONE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        requireActivity().menuInflater.inflate(R.menu.menu_top_bar_production, menu)
        val itemFilter = menu.findItem(R.id.action_filter)
        itemFilter.setOnMenuItemClickListener {
            selecDate()
            true
        }
        item = menu.findItem(R.id.action_close)
        item.isVisible = false
        item.setOnMenuItemClickListener {
            item.isVisible = false
            binding.lottieAnimationView.visibility = View.GONE
            if (productions.isNotEmpty()) {
                productionAdapter.updateList(productions)
                binding.rvProductions.visibility = View.VISIBLE
                binding.textListEmpty.visibility = View.GONE
            } else {
                binding.rvProductions.visibility = View.GONE
                binding.textListEmpty.visibility = View.VISIBLE
            }
            binding.tvRange.visibility = View.GONE
            binding.tvUnits.visibility = View.GONE
            binding.tvTotal.visibility = View.GONE
            true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
        item.isVisible = true
        val calendarStartRange = Calendar.getInstance()
        val calendarFinalRange = Calendar.getInstance()
        calendarStartRange.time = dateStart!!
        calendarFinalRange.time = dateFinal!!
        productionsFilter.clear()
        for (production in productions) {
            if (production.date.after(calendarStartRange.time) && production.date.before(calendarFinalRange.time)) {
                productionsFilter.add(production)
            }
        }
        if (productionsFilter.isEmpty()) {
            binding.lottieAnimationView.visibility = View.VISIBLE
            binding.tvUnits.visibility = View.GONE
            binding.tvRange.visibility = View.GONE
            binding.tvTotal.visibility = View.GONE
            binding.textListEmpty.visibility = View.GONE
            binding.lottieAnimationView.playAnimation()
        } else {
            var units = 0
            var totalBs = 0.0
            var totalDl = 0.0
            for (prod in productionsFilter) {
                units += prod.quantity
                if (prod.isDolar) {
                    totalBs += (prod.quantity * prod.price) * prod.rate
                    totalDl += prod.quantity * prod.price
                } else {
                    totalBs += prod.quantity * prod.price
                    totalDl += (prod.quantity * prod.price) / prod.rate
                }
            }
            val convert = ClassesCommon.convertDoubleToString(totalDl)
            binding.tvUnits.text = getString(R.string.text_quantity_production_item, units.toString())
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
        productionAdapter.updateList(productionsFilter)
    }

}