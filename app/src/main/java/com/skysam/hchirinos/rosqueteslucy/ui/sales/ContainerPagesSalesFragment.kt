package com.skysam.hchirinos.rosqueteslucy.ui.sales

import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.databinding.FragmentContainerPagesSalesBinding
import com.skysam.hchirinos.rosqueteslucy.ui.sales.pages.SectionsPagerAdapter

class ContainerPagesSalesFragment : Fragment(), SearchView.OnQueryTextListener {

    private var _binding: FragmentContainerPagesSalesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SalesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContainerPagesSalesBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sectionsPagerAdapter = SectionsPagerAdapter(requireActivity())
        binding.viewPager.adapter = sectionsPagerAdapter
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = when(position) {
                0 -> "Por Cobrar"
                1 -> "Cobradas"
                2 -> "Todas"
                else -> "Facturas"
            }
        }.attach()

        viewModel.badge.observe(viewLifecycleOwner, {
            if (it > 0) {
                val badge = binding.tabs.getTabAt(0)?.orCreateBadge
                badge?.number = it
            } else {
                binding.tabs.getTabAt(0)?.removeBadge()
            }
        })
        binding.viewPager.registerOnPageChangeCallback(callback)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        menu.clear()
        inflater.inflate(R.menu.menu_top_bar_main, menu)
        val item = menu.findItem(R.id.action_search)
        val search = item.actionView as SearchView
        search.setOnQueryTextListener(this)
    }

    private val callback: ViewPager2.OnPageChangeCallback = object: ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            viewModel.changePage(position)
        }
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        viewModel.newTextSearch(newText!!)
        return true
    }
}