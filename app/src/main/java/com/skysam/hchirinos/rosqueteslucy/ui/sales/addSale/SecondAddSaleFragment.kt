package com.skysam.hchirinos.rosqueteslucy.ui.sales.addSale

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.Constants
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Costumer
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Sale
import com.skysam.hchirinos.rosqueteslucy.databinding.FragmentSecondAddSaleBinding
import com.skysam.hchirinos.rosqueteslucy.ui.sales.SalesViewModel

class SecondAddSaleFragment : Fragment(){

    private var _binding: FragmentSecondAddSaleBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SalesViewModel by activityViewModels()
    private var price = 0.0
    private var quantity = 0
    private var isDolar = false
    private var isPaid = false
    private var invoice = 0
    private var date: Long = 0
    private lateinit var costumer: Costumer
    private lateinit var location: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondAddSaleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSale.setOnClickListener {
            val sale = Sale(
                Constants.SALES,
                costumer.id,
                costumer.name,
                location,
                price,
                quantity,
                isDolar,
                invoice,
                isPaid,
                date
            )
            viewModel.addSale(sale)
            Toast.makeText(requireContext(), getString(R.string.text_saving), Toast.LENGTH_SHORT).show()
            requireActivity().finish()
        }

        loadViewModel()
    }

    private fun loadViewModel() {
        viewModel.costumer.observe(viewLifecycleOwner, {
            costumer = it
            binding.tvNameCostumer.text = it.name
            binding.tvRif.text = it.identifier
        })
        viewModel.location.observe(viewLifecycleOwner, {
            location = it
            binding.tvLocationCostumer.text = it
        })
        viewModel.price.observe(viewLifecycleOwner, {
            price = it
            showTotal()
        })
        viewModel.quantity.observe(viewLifecycleOwner, {
            quantity = it
            showTotal()
        })
        viewModel.isDolar.observe(viewLifecycleOwner, {
            isDolar = it
        })
        viewModel.isPaid.observe(viewLifecycleOwner, {
            isPaid = it
        })
        viewModel.invoice.observe(viewLifecycleOwner, {
            invoice = it
        })
        viewModel.date.observe(viewLifecycleOwner, {
            date = it
        })
    }

    private fun showTotal() {
        binding.tvQuantityPrice.text = getString(R.string.text_quantity_price,
            price.toString(), quantity.toString())
        val total = quantity * price
        binding.tvTotalMonto.text = getString(R.string.text_total_amount, total.toString())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}