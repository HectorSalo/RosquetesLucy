package com.skysam.hchirinos.rosqueteslucy.ui.graphs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.skysam.hchirinos.rosqueteslucy.databinding.FragmentGraphsBinding

class GraphsFragment : Fragment() {

    private lateinit var graphsViewModel: GraphsViewModel
    private var _binding: FragmentGraphsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        graphsViewModel =
            ViewModelProvider(this).get(GraphsViewModel::class.java)

        _binding = FragmentGraphsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textListEmpty
        graphsViewModel.text.observe(viewLifecycleOwner, {

        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}