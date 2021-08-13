package com.skysam.hchirinos.rosqueteslucy.ui.sales

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Sale
import com.skysam.hchirinos.rosqueteslucy.databinding.FragmentSalesBinding
import com.skysam.hchirinos.rosqueteslucy.ui.sales.addSale.AddSaleActivity

class SalesFragment : Fragment() {

    private lateinit var viewModel: SalesViewModel
    private var _binding: FragmentSalesBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapaterSales: SalesAdapter
    private val sales = mutableListOf<Sale>()

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
        adapaterSales = SalesAdapter(sales)
        binding.rvSales.apply {
            setHasFixedSize(true)
            adapter = adapaterSales
        }
        binding.floatingActionButton.setOnClickListener {
            startActivity(Intent(requireContext(), AddSaleActivity::class.java))
        }

        loadViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        binding.floatingActionButton.show()
    }

    override fun onPause() {
        super.onPause()
        binding.floatingActionButton.hide()
    }

    private fun loadViewModel() {
        viewModel.sale.observe(viewLifecycleOwner, {
            viewModel.addSaleToList(it)
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
}