package com.skysam.hchirinos.rosqueteslucy.ui.sales.pages

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Costumer
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Sale
import com.skysam.hchirinos.rosqueteslucy.databinding.FragmentSalesBinding
import com.skysam.hchirinos.rosqueteslucy.ui.sales.SalesViewModel
import com.skysam.hchirinos.rosqueteslucy.ui.sales.ViewDetailsSaleDialog
import java.util.*

class SalesFragment : Fragment(), OnClick {

    private val viewModel: SalesViewModel by activityViewModels()
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
                listExpiredSevenDays.clear()
                salesNotPaid.clear()
                salesPaid.clear()
                sales.clear()
                if (it.isNotEmpty()) {
                    sales.addAll(it)
                    for (sale in sales) {
                        val daysBetween = adapaterSales.getTimeDistance(Date(sale.dateDelivery), Date())
                        if (sale.isPaid) salesPaid.add(sale) else salesNotPaid.add(sale)
                        if (!sale.isPaid && daysBetween >= 7) listExpiredSevenDays.add(sale)
                    }
                    loadList(index)
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
        viewModel.textSearch.observe(viewLifecycleOwner, {
            searchFromText(it)
        })
    }

    private fun loadList(index: Int) {
        viewModel.updateBadge(listExpiredSevenDays.size)
        when(index) {
            0 -> {
                if (salesNotPaid.isNotEmpty()) {
                    adapaterSales.updateList(salesNotPaid)
                    binding.rvSales.visibility = View.VISIBLE
                    binding.textListEmpty.visibility = View.GONE
                } else {
                    binding.rvSales.visibility = View.GONE
                    binding.textListEmpty.visibility = View.VISIBLE
                    binding.textListEmpty.text = getString(R.string.list_sales_not_paid_empty)
                }
            }
            1 -> {
                if (salesPaid.isNotEmpty()) {
                    adapaterSales.updateList(salesPaid)
                    binding.rvSales.visibility = View.VISIBLE
                    binding.textListEmpty.visibility = View.GONE
                } else {
                    binding.rvSales.visibility = View.GONE
                    binding.textListEmpty.visibility = View.VISIBLE
                    binding.textListEmpty.text = getString(R.string.list_sales_paid_empty)
                }
            }
            2 -> {
                if (sales.isNotEmpty()) {
                    adapaterSales.updateList(sales)
                    binding.rvSales.visibility = View.VISIBLE
                    binding.textListEmpty.visibility = View.GONE
                } else {
                    binding.rvSales.visibility = View.GONE
                    binding.textListEmpty.visibility = View.VISIBLE
                    binding.textListEmpty.text = getString(R.string.list_sales_empty)
                }
            }
        }
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

    override fun deleteSale(sale: Sale) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(getString(R.string.title_confirmation_dialog))
            .setMessage(getString(R.string.msg_delete_dialog))
            .setPositiveButton(R.string.text_delete) { _, _ ->
                Toast.makeText(requireContext(), R.string.text_deleting, Toast.LENGTH_SHORT).show()
                viewModel.deleteSale(sale)
            }
            .setNegativeButton(R.string.btn_cancel, null)

        val dialog = builder.create()
        dialog.show()
    }

    private fun searchFromText(text: String) {
        val listSearch = mutableListOf<Sale>()
        if (sales.isEmpty()) {
            Toast.makeText(context, getString(R.string.list_sales_empty), Toast.LENGTH_SHORT).show()
        } else {
            val userInput: String = text.lowercase()
            listSearch.clear()

            when(index) {
                0-> {
                    for (sale in salesNotPaid) {
                        if (sale.nameCostumer.lowercase().contains(userInput) || sale.location.lowercase().contains(userInput)) {
                            listSearch.add(sale)
                        }
                    }
                }
                1-> {
                    for (sale in salesPaid) {
                        if (sale.nameCostumer.lowercase().contains(userInput) || sale.location.lowercase().contains(userInput)) {
                            listSearch.add(sale)
                        }
                    }
                }
                2-> {
                    for (sale in sales) {
                        if (sale.nameCostumer.lowercase().contains(userInput) || sale.location.lowercase().contains(userInput)) {
                            listSearch.add(sale)
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
            adapaterSales.updateList(listSearch)
        }
    }
}