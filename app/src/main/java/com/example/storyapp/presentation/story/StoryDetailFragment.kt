package com.example.storyapp.presentation.story

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.storyapp.databinding.FragmentStoryDetailBinding
import com.example.storyapp.data.model.Story

class StoryDetailFragment : Fragment() {

    private var _binding: FragmentStoryDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStoryDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val story = arguments?.getParcelable<Story>("story")

        if (story == null) {
            showError("Data tidak tersedia.")
        } else {
            loadStoryDetails(story)
        }
    }

    private fun loadStoryDetails(story: Story) {
        showLoading()

        try {
            binding.tvDetailName.text = story.name
            binding.tvDetailDescription.text = story.description
            Glide.with(binding.ivDetailPhoto.context).load(story.photoUrl).into(binding.ivDetailPhoto)

            hideLoading()
        } catch (e: Exception) {
            showError("Gagal memuat data: ${e.message}")
        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.tvErrorMessage.visibility = View.GONE
        binding.ivDetailPhoto.visibility = View.GONE
        binding.tvDetailName.visibility = View.GONE
        binding.tvDetailDescription.visibility = View.GONE
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.ivDetailPhoto.visibility = View.VISIBLE
        binding.tvDetailName.visibility = View.VISIBLE
        binding.tvDetailDescription.visibility = View.VISIBLE
    }

    private fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.tvErrorMessage.text = message
        binding.tvErrorMessage.visibility = View.VISIBLE
        binding.ivDetailPhoto.visibility = View.GONE
        binding.tvDetailName.visibility = View.GONE
        binding.tvDetailDescription.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
