package com.skysam.hchirinos.rosqueteslucy.ui.sales

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.dataClass.Sale
import com.skysam.hchirinos.rosqueteslucy.databinding.FragmentSecondAddSaleBinding

/**
 * Created by Hector Chirinos (Home) on 15/8/2021.
 */
class ViewDetailsSaleDialog(private val sale: Sale): DialogFragment() {
    private var _binding: FragmentSecondAddSaleBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ShapeAppearanceOverlay_MaterialComponents_MaterialCalendar_Window_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondAddSaleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSale.visibility = View.GONE
        binding.tvNameCostumer.text = sale.nameCostumer
        binding.tvRif.text = sale.idCostumer
        binding.tvLocationCostumer.text = sale.location
        val total = sale.quantity * sale.price
        binding.tvTotalMonto.text = getString(R.string.text_total_amount, total.toString())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}