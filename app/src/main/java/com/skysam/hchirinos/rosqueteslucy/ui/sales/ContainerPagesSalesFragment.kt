package com.skysam.hchirinos.rosqueteslucy.ui.sales

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.skysam.hchirinos.rosqueteslucy.databinding.FragmentContainerPagesSalesBinding
import com.skysam.hchirinos.rosqueteslucy.ui.sales.pages.SectionsPagerAdapter

class ContainerPagesSalesFragment : Fragment() {

    private var _binding: FragmentContainerPagesSalesBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SalesViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel =
            ViewModelProvider(this).get(SalesViewModel::class.java)
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
        val badge = binding.tabs.getTabAt(0)?.orCreateBadge
        viewModel.badge.observe(viewLifecycleOwner, {
            if (it > 0) {
                badge?.number = it
            }
        })
        viewModel.indexPage.observe(viewLifecycleOwner, {
            badge?.number = it
        })
        binding.viewPager.registerOnPageChangeCallback(callback)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val callback: ViewPager2.OnPageChangeCallback = object: ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            viewModel.changePage(position)
        }
    }
}