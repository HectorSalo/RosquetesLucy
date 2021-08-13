package com.skysam.hchirinos.rosqueteslucy.ui.sales.addSale

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.classView.ExitDialog
import com.skysam.hchirinos.rosqueteslucy.common.classView.OnClickExit
import com.skysam.hchirinos.rosqueteslucy.databinding.FragmentFirstAddSaleBinding
import com.skysam.hchirinos.rosqueteslucy.ui.sales.SalesViewModel

class FirstAddSaleFragment : Fragment(), OnClickExit {

    private var _binding: FragmentFirstAddSaleBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SalesViewModel by activityViewModels()
    private val costumersLocation = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstAddSaleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                getOut()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        binding.btnExit.setOnClickListener { getOut() }
        binding.btnTotal.setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        loadViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getOut() {
        val exitDialog = ExitDialog(this)
        exitDialog.show(requireActivity().supportFragmentManager, tag)
    }

    override fun onClickExit() {
        requireActivity().finish()
    }

    private fun loadViewModel() {
        viewModel.costumers.observe(viewLifecycleOwner, {
            if (_binding != null) {
                if (it.isNotEmpty()) {
                    for (cos in it) {
                        for (i in cos.locations.indices) {
                            val location = "${cos.name} - ${cos.locations[i].name}"
                            costumersLocation.add(location)
                        }
                    }
                    val adapterSearchProduct = ArrayAdapter(requireContext(), R.layout.list_autocomplete_text, costumersLocation.sorted())
                    binding.etNameCostumer.setAdapter(adapterSearchProduct)
                }
            }
        })
    }

    private fun validateData() {

    }
}