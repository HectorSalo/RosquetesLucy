package com.skysam.hchirinos.rosqueteslucy.ui.sales

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Costumer
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Sale
import com.skysam.hchirinos.rosqueteslucy.databinding.FragmentSalesBinding

class SalesFragment : Fragment(), OnClick {

    private lateinit var viewModel: SalesViewModel
    private var _binding: FragmentSalesBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapaterSales: SalesAdapter
    private val sales = mutableListOf<Sale>()
    private val costumers = mutableListOf<Costumer>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel =
            ViewModelProvider(this).get(SalesViewModel::class.java)
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
                if (it.isEmpty()) {
                    binding.rvSales.visibility = View.GONE
                    binding.textListEmpty.visibility = View.VISIBLE
                } else {
                    sales.clear()
                    sales.addAll(it)
                    adapaterSales.updateList(sales)
                    binding.rvSales.visibility = View.VISIBLE
                    binding.textListEmpty.visibility = View.GONE
                }
                binding.progressBar.visibility = View.GONE
            }
        })
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
}