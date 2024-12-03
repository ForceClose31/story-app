package com.example.storyapp.presentation.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.storyapp.MainActivity
import com.example.storyapp.data.api.RetrofitClient
import com.example.storyapp.data.api.LoginRequest
import com.example.storyapp.databinding.FragmentLoginBinding
import com.example.storyapp.utils.DataStoreManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var dataStore: DataStoreManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        dataStore = DataStoreManager(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnLogin.setOnClickListener {
            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()

            CoroutineScope(Dispatchers.IO).launch {
                val response = RetrofitClient.instance.login(LoginRequest(email, password)).execute()
                activity?.runOnUiThread {
                    if (response.isSuccessful) {
                        val token = response.body()?.token
                        if (token != null) {
                            dataStore.saveToken(token)
                            navigateToMainActivity()
                        } else {
                            showToast("Login failed: Token is null")
                        }
                    } else {
                        showToast("Login failed: ${response.message()}")
                    }
                }
            }
        }
    }

    private fun navigateToMainActivity() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
