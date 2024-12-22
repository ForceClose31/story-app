package com.example.storyapp.presentation.auth

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.transition.TransitionInflater
import com.example.storyapp.R
import com.example.storyapp.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var authViewModel: AuthViewModel

    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        val fadeTransition = ObjectAnimator.ofFloat(binding.tvRegister, "alpha", 0f, 1f)
        fadeTransition.duration = 500
        fadeTransition.start()

        binding.btnLogin.monitorFields(binding.edLoginEmail, binding.edLoginPassword)

        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString().trim()
            val password = binding.edLoginPassword.text.toString().trim()

            if (!isValidEmail(email)) {
                setError(binding.edLoginEmail, "Format email salah")
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                setError(binding.edLoginPassword, "Password tidak boleh kosong")
                return@setOnClickListener
            }

            clearError(binding.edLoginEmail)
            clearError(binding.edLoginPassword)
            showLoading()

            authViewModel.login(email, password)
        }

        authViewModel.loginResult.observe(viewLifecycleOwner, { result ->
            hideLoading()

            if (result != null) {
                if (!result.error) {
                    result.loginResult.token.let { token ->
                        authViewModel.saveToken(requireContext(), token)
                    }
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                } else {
                    setError(binding.edLoginEmail, null)
                    setError(binding.edLoginPassword, result.message)
                }
            } else {
                showNoDataMessage()
            }
        })

        binding.tvRegister.setOnClickListener {
            val transition = TransitionInflater.from(requireContext())
                .inflateTransition(android.R.transition.move)
            sharedElementEnterTransition = transition
            sharedElementReturnTransition = transition

            val extras = FragmentNavigatorExtras(
                binding.edLoginEmail to "emailTransition",
                binding.edLoginPassword to "passwordTransition",
                binding.btnLogin to "ButtonTransition"
            )
            findNavController().navigate(
                R.id.action_loginFragment_to_registerFragment,
                null,
                null,
                extras
            )
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showExitDialog()
                }
            })
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun setError(view: View, message: String?) {
        if (view is android.widget.EditText) {
            view.error = message
        }
    }

    private fun clearError(view: View) {
        if (view is android.widget.EditText) {
            view.error = null
        }
    }

    private fun showLoading() {
        isLoading = true
        binding.progressBar.visibility = View.VISIBLE
        binding.btnLogin.isEnabled = false
    }

    private fun hideLoading() {
        isLoading = false
        binding.progressBar.visibility = View.GONE
        binding.btnLogin.isEnabled = true
    }

    private fun showNoDataMessage() {
        Toast.makeText(requireContext(), "Password atau email anda salah", Toast.LENGTH_SHORT)
            .show()
    }

    private fun showExitDialog() {
        val dialog = AlertDialog.Builder(requireContext())
            .setMessage("Apakah Anda yakin ingin keluar dari aplikasi?")
            .setCancelable(false)
            .setPositiveButton("Ya") { _, _ ->
                requireActivity().finishAffinity()
            }
            .setNegativeButton("Tidak") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
