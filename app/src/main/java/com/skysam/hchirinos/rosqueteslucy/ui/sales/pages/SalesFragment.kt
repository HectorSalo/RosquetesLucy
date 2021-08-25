package com.skysam.hchirinos.rosqueteslucy.ui.sales.pages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Costumer
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Sale
import com.skysam.hchirinos.rosqueteslucy.databinding.FragmentSalesBinding
import com.skysam.hchirinos.rosqueteslucy.ui.sales.SalesViewModel
import com.skysam.hchirinos.rosqueteslucy.ui.sales.ViewDetailsSaleDialog
import java.util.*

class SalesFragment : Fragment(), OnClick {

    private lateinit var viewModel: SalesViewModel
    private var _binding: FragmentSalesBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapaterSales: SalesAdapter
    private val sales = mutableListOf<Sale>()
    private val salesPaid = mutableListOf<Sale>()
    private val salesNotPaid = mutableListOf<Sale>()
    private val costumers = mutableListOf<Costumer>()
    private val listExpiredSevenDays = mutableListOf<Sale>()
    private var index = 0

    companion object {
        private const val ARG_SECTION_NUMBER = "section_number"

        @JvmStatic
        fun newInstance(sectionNumber: Int): SalesFragment {
            return SalesFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel =
            ViewModelProvider(this).get(SalesViewModel::class.java).apply {
                changePage(arguments?.getInt(ARG_SECTION_NUMBER) ?: 0)
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSalesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapaterSales = SalesAdapter(sales, this)
        binding.rvSales.apply {
            setHasFixedSize(true)
            adapter = adapaterSales
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
        viewModel.sales.observe(viewLifecycleOwner, {
            if (_binding != null) {
                if (it.isNotEmpty()) {
                    listExpiredSevenDays.clear()
                    salesNotPaid.clear()
                    salesPaid.clear()
                    sales.clear()
                    sales.addAll(it)
                    for (sale in sales) {
                        val daysBetween = adapaterSales.getTimeDistance(Date(sale.date), Date())
                        if (sale.isPaid) salesPaid.add(sale) else salesNotPaid.add(sale)
                        if (!sale.isPaid && daysBetween >= 7) listExpiredSevenDays.add(sale)
                    }
                    loadList(index)
                    updateBadge(listExpiredSevenDays.size)
                } else {
                    binding.rvSales.visibility = View.GONE
                    binding.textListEmpty.visibility = View.VISIBLE
                }
                binding.progressBar.visibility = View.GONE
            }
        })
        viewModel.indexPage.observe(viewLifecycleOwner, {
            index = it
            loadList(it)
        })
    }

    private fun loadList(index: Int) {
        when(index) {
            0 -> adapaterSales.updateList(salesNotPaid)
            1 -> adapaterSales.updateList(salesPaid)
            2 -> adapaterSales.updateList(sales)
        }
        binding.rvSales.visibility = View.VISIBLE
        binding.textListEmpty.visibility = View.GONE
    }

    override fun viewSale(sale: Sale) {
        for (cos in costumers) {
            if (cos.id == sale.idCostumer) {
                sale.idCostumer = cos.identifier
            }
        }
        val viewDetailsSale = ViewDetailsSaleDialog(sale)
        viewDetailsSale.show(requireActivity().supportFragmentManager, tag)
    }

    private fun updateBadge(number: Int) {
        if (number > 0) viewModel.updateBadge(number)
    }
}