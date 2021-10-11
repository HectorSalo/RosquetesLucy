package com.skysam.hchirinos.rosqueteslucy.ui.initSession

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import com.google.android.material.snackbar.Snackbar
import com.skysam.hchirinos.rosqueteslucy.MainActivity
import com.skysam.hchirinos.rosqueteslucy.R
import com.skysam.hchirinos.rosqueteslucy.databinding.FragmentSecondLoginBinding
import java.util.concurrent.Executor

class SecondFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentSecondLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecondLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                requireActivity().finishAffinity()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)


        binding.button0.setOnClickListener(this)
        binding.button1.setOnClickListener(this)
        binding.button2.setOnClickListener(this)
        binding.button3.setOnClickListener(this)
        binding.button4.setOnClickListener(this)
        binding.button5.setOnClickListener(this)
        binding.button6.setOnClickListener(this)
        binding.button7.setOnClickListener(this)
        binding.button8.setOnClickListener(this)
        binding.button9.setOnClickListener(this)
        binding.buttonDelete.setOnClickListener(this)
        binding.buttonFingerprint.setOnClickListener(this)

        loadViewModel()

        showPrint()
    }

    private fun loadViewModel() {
        viewModel.numberPass.observe(viewLifecycleOwner, {
            if (_binding != null) {
                binding.tvPin.text = it
            }
        })
        viewModel.passAccept.observe(viewLifecycleOwner, {
            if (_binding != null) {
                if (it) {
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                    requireActivity().finish()
                } else {
                    Snackbar.make(binding.root, R.string.error_pin_code, Snackbar.LENGTH_LONG).show()
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(component: View?) {
        when(component?.id) {
            R.id.button0 -> {viewModel.addNewNumber(0)}
            R.id.button1 -> {viewModel.addNewNumber(1)}
            R.id.button2 -> {viewModel.addNewNumber(2)}
            R.id.button3 -> {viewModel.addNewNumber(3)}
            R.id.button4 -> {viewModel.addNewNumber(4)}
            R.id.button5 -> {viewModel.addNewNumber(5)}
            R.id.button6 -> {viewModel.addNewNumber(6)}
            R.id.button7 -> {viewModel.addNewNumber(7)}
            R.id.button8 -> {viewModel.addNewNumber(8)}
            R.id.button9 -> {viewModel.addNewNumber(9)}
            R.id.button_delete -> {viewModel.deleteNumber()}
            R.id.button_fingerprint -> showPrint()
        }
    }

    private fun showPrint() {
        val biometricPrompt: BiometricPrompt

        val executor: Executor = ContextCompat.getMainExecutor(requireContext())
        biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int,
                                                   errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    when (errorCode) {
                        BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {

                        }
                        BiometricPrompt.ERROR_LOCKOUT -> {

                        }
                        BiometricPrompt.ERROR_CANCELED -> {

                        }
                        BiometricPrompt.ERROR_HW_NOT_PRESENT -> {

                        }
                        BiometricPrompt.ERROR_HW_UNAVAILABLE -> {

                        }
                        BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> {

                        }
                        BiometricPrompt.ERROR_NO_BIOMETRICS -> {
                            Snackbar.make(binding.root, R.string.error_biometric, Snackbar.LENGTH_LONG).show()
                        }
                        BiometricPrompt.ERROR_NO_DEVICE_CREDENTIAL -> {

                        }
                        BiometricPrompt.ERROR_NO_SPACE -> {

                        }
                        BiometricPrompt.ERROR_TIMEOUT -> {

                        }
                        BiometricPrompt.ERROR_UNABLE_TO_PROCESS -> {

                        }
                        BiometricPrompt.ERROR_USER_CANCELED -> {

                        }
                        BiometricPrompt.ERROR_VENDOR -> {

                        }
                    }
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    startActivity(Intent(requireContext(), MainActivity::class.java))
                    requireActivity().finish()
                }

            })

        val promptInfo: BiometricPrompt.PromptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(getString(R.string.title_dialog_fingerprint))
            .setNegativeButtonText(getString(R.string.btn_dialog_fingerprint))
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}