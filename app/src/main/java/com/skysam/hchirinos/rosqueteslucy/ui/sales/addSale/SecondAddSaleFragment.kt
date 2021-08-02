package com.skysam.hchirinos.rosqueteslucy.ui.sales.addSale

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.classView.ExitDialog
import com.skysam.hchirinos.rosqueteslucy.common.classView.OnClickExit
import com.skysam.hchirinos.rosqueteslucy.databinding.FragmentSecondAddSaleBinding

/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class SecondAddSaleFragment : Fragment(){

    private var _binding: FragmentSecondAddSaleBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

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
            findNavController().navigate(R.id.action_SecondFragment_to_FirstFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}