package com.example.storyapp.presentation.auth

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.transition.TransitionInflater
import com.example.storyapp.R
import com.example.storyapp.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
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

        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            authViewModel.login(email, password)
        }

        authViewModel.loginResult.observe(viewLifecycleOwner, { result ->
            if (result != null && !result.error) {
                result.loginResult.token.let { token ->
                    authViewModel.saveToken(requireContext(), token)
                }

                findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
            } else {
                Toast.makeText(requireContext(), "Login Failed", Toast.LENGTH_SHORT).show()
            }
        })

        binding.tvRegister.setOnClickListener {
            val transition = TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
            sharedElementEnterTransition = transition
            sharedElementReturnTransition = transition

            val extras = FragmentNavigatorExtras(
                binding.edLoginEmail to "emailTransition",
                binding.edLoginPassword to "passwordTransition",
                binding.btnLogin to "ButtonTransition"
            )
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment, null, null, extras)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
