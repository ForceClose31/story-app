package com.example.storyapp.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.databinding.FragmentHomeBinding
import com.example.storyapp.presentation.story.StoryAdapter
import com.example.storyapp.presentation.story.StoryViewModel
import com.example.storyapp.utils.DataStoreManager
import com.example.storyapp.data.repository.StoryRepository
import kotlinx.coroutines.launch
import com.example.storyapp.data.local.entity.StoryEntity
import com.example.storyapp.data.local.StoryDatabase
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import com.example.storyapp.data.api.RetrofitClient
import com.example.storyapp.presentation.story.StoryViewModelFactory

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var dataStoreManager: DataStoreManager
    private lateinit var storyAdapter: StoryAdapter
    private var storyViewModel: StoryViewModel? = null

    private fun getStoryViewModel(token: String): StoryViewModel {
        val database = StoryDatabase.getDatabase(requireContext())
        val apiService = RetrofitClient.instance
        val repository = StoryRepository(database, apiService,  "Bearer $token")
        val factory = StoryViewModelFactory(requireActivity().application, repository)
        return ViewModelProvider(this, factory).get(StoryViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataStoreManager = DataStoreManager(requireContext())
        storyAdapter = StoryAdapter { story -> onStoryClick(story) }

        binding.rvStoryList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvStoryList.adapter = storyAdapter

        binding.toolbar.findViewById<ImageButton>(R.id.btn_logout).setOnClickListener {
            logout()
        }

        binding.toolbar.findViewById<ImageButton>(R.id.btn_add_story).setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addStoryFragment)
        }

        binding.toolbar.findViewById<ImageButton>(R.id.btn_map).setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_mapFragment)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val token = dataStoreManager.getToken()

            if (!token.isNullOrEmpty()) {
                showLoading()
                storyViewModel = getStoryViewModel(token)
                storyViewModel!!.stories.collect { pagingData ->
                    Log.d("PagingData", "PagingData size: ${pagingData.toString()}")
                    storyAdapter.submitData(viewLifecycleOwner.lifecycle, pagingData)
                    hideLoading()
                }
            } else {
                findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
            }
        }

        storyViewModel?.errorMessage?.observe(viewLifecycleOwner) { message ->
            hideLoading()
            showErrorMessage(message)
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitDialog()
            }
        })
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.rvStoryList.visibility = View.GONE
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.rvStoryList.visibility = View.VISIBLE
    }

    private fun showErrorMessage(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun logout() {
        lifecycleScope.launch {
            dataStoreManager.clearToken()
            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
        }
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

    private fun onStoryClick(story: StoryEntity) {
        val bundle = Bundle().apply {
            putParcelable("story", story)
        }
        findNavController().navigate(R.id.action_homeFragment_to_storyDetailFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
