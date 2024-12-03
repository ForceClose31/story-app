package com.example.storyapp.presentation.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.storyapp.R
import com.example.storyapp.databinding.FragmentHomeBinding
import com.example.storyapp.data.model.Story
import com.example.storyapp.presentation.story.StoryAdapter
import com.example.storyapp.presentation.story.StoryViewModel
import com.example.storyapp.utils.DataStoreManager
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var storyViewModel: StoryViewModel
    private lateinit var dataStoreManager: DataStoreManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        storyViewModel = ViewModelProvider(this)[StoryViewModel::class.java]
        dataStoreManager = DataStoreManager(requireContext())

        // Setup RecyclerView Adapter
        val adapter = StoryAdapter { story -> onStoryClick(story) }
        binding.rvStoryList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvStoryList.adapter = adapter

        // Get token and load stories
        viewLifecycleOwner.lifecycleScope.launch {
            val token = dataStoreManager.getToken()

            if (!token.isNullOrEmpty()) {
                storyViewModel.getStories("Bearer $token")
            } else {
                // Navigate to Login screen if no token found
                findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
            }
        }

        // Observe live data for stories
        storyViewModel.storiesLiveData.observe(viewLifecycleOwner) { stories ->
            if (stories != null) {
                adapter.submitList(stories)
            }
        }

        // Observe error messages
        storyViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

        // Handle back button press to show exit dialog
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitDialog()
            }
        })

        // Logout button
        binding.btnLogout.setOnClickListener {
            logout()
        }

        // Add Story button
        binding.btnAddStory.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addStoryFragment)
        }
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

    private fun onStoryClick(story: Story) {
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
