package com.example.storyapp.presentation.auth

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.transition.TransitionInflater
import com.example.storyapp.presentation.auth.custom.ValidationButton
import com.example.storyapp.presentation.auth.custom.ValidationEditText
import com.example.storyapp.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private lateinit var authViewModel: AuthViewModel

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

        val nameField = binding.edRegisterName
        val emailField = binding.edRegisterEmail
        val passwordField = binding.edRegisterPassword
        val registerButton = binding.btnRegister

        registerButton.monitorFields(nameField, emailField, passwordField)

        val fadeTransition = ObjectAnimator.ofFloat(nameField, "alpha", 0f, 1f)
        fadeTransition.duration = 500
        fadeTransition.start()

        val transition = TransitionInflater.from(requireContext())
            .inflateTransition(android.R.transition.move)
        sharedElementEnterTransition = transition
        sharedElementReturnTransition = transition

        registerButton.setOnClickListener {
            val name = nameField.text.toString()
            val email = emailField.text.toString()
            val password = passwordField.text.toString()

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
        binding.progressBar.visibility = View.VISIBLE
        binding.btnRegister.isEnabled = false
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.btnRegister.isEnabled = true
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showNoDataMessage() {
        Toast.makeText(requireContext(), "Email telah terpakai", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
