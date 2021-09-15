package com.skysam.hchirinos.rosqueteslucy.ui.initSession

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.skysam.hchirinos.rosqueteslucy.MainActivity
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.common.Keyboard
import com.skysam.hchirinos.rosqueteslucy.database.repositories.InitSession
import com.skysam.hchirinos.rosqueteslucy.databinding.FragmentFirstLoginBinding

class FirstFragment : Fragment() {

    private var _binding: FragmentFirstLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.messageSession.observe(viewLifecycleOwner, {
            if (_binding != null) {
                binding.progressBar.visibility = View.GONE
                if (it != "ok") {
                    Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG).show()
                    binding.buttonLogin.isEnabled = true
                    binding.tfUser.isEnabled = true
                    binding.tfPassword.isEnabled = true
                } else {
                    requireActivity().finish()
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                }
            }
        })
        binding.etUser.doAfterTextChanged { binding.tfUser.error = null }
        binding.etPassword.doAfterTextChanged { binding.tfPassword.error = null }

        binding.buttonLogin.setOnClickListener {
            validateUser()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onStart() {
        super.onStart()
        if (InitSession.getCurrentUser() != null) {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
            //FirebaseAuth.getInstance().signOut()
        }
    }

    private fun validateUser() {
        binding.tfUser.error = null
        binding.tfPassword.error = null

        val email = binding.etUser.text.toString().trim()
        if (email.isEmpty()) {
            binding.tfUser.error = getString(R.string.error_field_empty)
            binding.etUser.requestFocus()
            return
        }
        val password = binding.etPassword.text.toString().trim()
        if (password.isEmpty()) {
            binding.tfPassword.error = getString(R.string.error_field_empty)
            binding.etPassword.requestFocus()
            return
        }
        binding.progressBar.visibility = View.VISIBLE
        binding.buttonLogin.isEnabled = false
        binding.tfUser.isEnabled = false
        binding.tfPassword.isEnabled = false
        Keyboard.close(binding.root)
        viewModel.initSession(email, password)
    }
}