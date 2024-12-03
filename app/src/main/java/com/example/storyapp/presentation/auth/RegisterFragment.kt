package com.example.storyapp.presentation.auth

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.transition.TransitionInflater
import com.example.storyapp.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var authViewModel: AuthViewModel

    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        binding.edRegisterPassword.addTextChangedListener { editable ->
            val password = editable.toString()
            if (password.length < 8) {
                binding.edRegisterPassword.error = "Password harus lebih dari 8 karakter"
            } else {
                binding.edRegisterPassword.error = null
            }
        }

        val fadeTransition = ObjectAnimator.ofFloat(binding.edRegisterName, "alpha", 0f, 1f)
        fadeTransition.duration = 500
        fadeTransition.start()

        val transition = TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = transition
        sharedElementReturnTransition = transition

        binding.btnRegister.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()

            if (name.isBlank() || email.isBlank() || password.isBlank()) {
                Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 8) {
                binding.edRegisterPassword.error = "Password must be at least 8 characters"
                return@setOnClickListener
            }

            showLoading()

            authViewModel.register(name, email, password)
        }

        authViewModel.registerResult.observe(viewLifecycleOwner) { result ->
            hideLoading()
            if (result != null) {
                if (!result.error) {
                    Toast.makeText(context, "Registrasi berhasil!", Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressed()
                } else {
                    showError(result.message ?: "Registrasi gagal")
                }
            } else {
                showNoDataMessage()
            }
        }
    }

    private fun showLoading() {
        isLoading = true
        binding.progressBar.visibility = View.VISIBLE
        binding.btnRegister.isEnabled = false
    }

    private fun hideLoading() {
        isLoading = false
        binding.progressBar.visibility = View.GONE
        binding.btnRegister.isEnabled = true
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showNoDataMessage() {
        Toast.makeText(requireContext(), "Data tidak tersedia", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
