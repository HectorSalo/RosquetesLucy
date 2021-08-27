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
import java.text.DateFormat
import java.util.*

class SecondAddSaleFragment : Fragment(){

    private var _binding: FragmentSecondAddSaleBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SalesViewModel by activityViewModels()
    private var price = 0.0
    private var rate = 0.0
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
                rate,
                rate,
                quantity,
                isDolar,
                invoice,
                isPaid,
                date,
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
            binding.tvPriceUnit.text = convertFormatNumber(it)
            showTotal()
        })
        viewModel.rate.observe(viewLifecycleOwner, {
            rate = it
            binding.tvRate.text = getString(R.string.text_rate_view, convertFormatNumber(it))
            showTotal()
        })
        viewModel.quantity.observe(viewLifecycleOwner, {
            quantity = it
            binding.tvQuantity.text = it.toString()
            showTotal()
        })
        viewModel.isDolar.observe(viewLifecycleOwner, {
            isDolar = it
            if (it) {
                binding.tvTitleAmount.text = getString(R.string.title_amount_total, "$")
                binding.tvTotalMontoBs.visibility = View.GONE
                binding.tvTotalIvaBs.visibility = View.GONE
                binding.tvTextIvaBs.visibility = View.GONE
                binding.tvTextTotalBs.visibility = View.GONE
                binding.tvRate.visibility = View.GONE
            } else {
                binding.tvTitleAmount.text = getString(R.string.title_amount_total, "Bs.")
                binding.tvTextIvaDolar.visibility = View.GONE
                binding.tvTotalIvaDolar.visibility = View.GONE
            }
            showTotal()
        })
        viewModel.isPaid.observe(viewLifecycleOwner, {
            isPaid = it
        })
        viewModel.invoice.observe(viewLifecycleOwner, {
            invoice = it
            binding.tvInvoice.text = getString(R.string.text_invoice_item, it.toString())
        })
        viewModel.date.observe(viewLifecycleOwner, {
            date = it
            binding.tvDate.text = DateFormat.getDateInstance().format(it)
        })
    }

    private fun showTotal() {
        val total = quantity * price
        binding.tvAmount.text = getString(R.string.text_total_amount, convertFormatNumber(total))
        if (!isDolar) {
            val ivaBs = total * 0.16
            binding.tvTotalIvaBs.text = getString(R.string.text_total_amount, convertFormatNumber(ivaBs))
            val totalAmountBs = total + ivaBs
            binding.tvTotalMontoBs.text = getString(R.string.text_total_amount, convertFormatNumber(totalAmountBs))
            val totalAmountDolar = total / rate
            binding.tvTotalMontoDolar.text = getString(R.string.text_total_amount, convertFormatNumber(totalAmountDolar))
        } else {
            val ivaDolar = total * 0.16
            binding.tvTotalIvaDolar.text = getString(R.string.text_total_amount, convertFormatNumber(ivaDolar))
            val totalAmountDolar = total + ivaDolar
            binding.tvTotalMontoDolar.text = getString(R.string.text_total_amount, convertFormatNumber(totalAmountDolar))
        }
    }

    private fun convertFormatNumber(amount: Double): String {
        return String.format(Locale.GERMANY, "%,.2f", amount)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}