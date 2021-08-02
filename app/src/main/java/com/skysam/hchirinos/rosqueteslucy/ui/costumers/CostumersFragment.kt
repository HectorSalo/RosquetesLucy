package com.skysam.hchirinos.rosqueteslucy.ui.costumers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.skysam.hchirinos.rosqueteslucy.databinding.FragmentCostumersBinding

class CostumersFragment : Fragment() {

    private lateinit var viewModel: CostumersViewModel
    private var _binding: FragmentCostumersBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel =
            ViewModelProvider(this).get(CostumersViewModel::class.java)

        _binding = FragmentCostumersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.floatingActionButton.setOnClickListener {
            val dialog = AddCostumerDialog()
            dialog.show(requireActivity().supportFragmentManager, tag)
        }
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
}