package com.skysam.hchirinos.rosqueteslucy.ui.sales

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import com.skysam.hchirinos.rosqueteslucy.databinding.FragmentContainerPagesSalesBinding
import com.skysam.hchirinos.rosqueteslucy.ui.sales.pages.SectionsPagerAdapter

class ContainerPagesSalesFragment : Fragment() {

    private var _binding: FragmentContainerPagesSalesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContainerPagesSalesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sectionsPagerAdapter = SectionsPagerAdapter(requireActivity())
        binding.viewPager.adapter = sectionsPagerAdapter
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = when(position) {
                0 -> "Por Cobrar"
                1 -> "Pagadas"
                2 -> "Todas"
                else -> "Facturas"
            }
        }.attach()
        val badge = binding.tabs.getTabAt(1)?.orCreateBadge
        badge?.number = 5
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}