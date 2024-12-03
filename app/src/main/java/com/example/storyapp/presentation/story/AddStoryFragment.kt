package com.example.storyapp.presentation.story

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.storyapp.databinding.FragmentAddStoryBinding
import com.example.storyapp.utils.DataStoreManager
import com.example.storyapp.utils.FileUtil
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class AddStoryFragment : Fragment() {

    private var _binding: FragmentAddStoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var addStoryViewModel: AddStoryViewModel
    private var photoFile: File? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddStoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("QueryPermissionsNeeded")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        addStoryViewModel = ViewModelProvider(this)[AddStoryViewModel::class.java]

        binding.btnSelectPhotoGallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, GALLERY_REQUEST_CODE)
        }

        binding.btnSelectPhotoCamera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (intent.resolveActivity(requireContext().packageManager) != null) {
                photoFile = FileUtil.createImageFile(requireContext())

                val photoUri: Uri = FileProvider.getUriForFile(
                    requireContext(),
                    "${requireContext().packageName}.provider",
                    photoFile!!
                )

                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(intent, CAMERA_REQUEST_CODE)
            }
        }



        binding.buttonAdd.setOnClickListener {
            val description = binding.edAddDescription.text.toString().trim()
            if (description.isEmpty() || photoFile == null) {
                Toast.makeText(requireContext(), "Fill all fields!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dataStoreManager = DataStoreManager(requireContext())
            viewLifecycleOwner.lifecycleScope.launch {
                val token = dataStoreManager.getToken()
                val requestBodyDescription = RequestBody.create("text/plain".toMediaTypeOrNull(), description)
                val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), photoFile!!)
                val photoPart = MultipartBody.Part.createFormData("photo", photoFile!!.name, requestFile)

                if (token != null) {
                    addStoryViewModel.addStory(token, requestBodyDescription, photoPart)
                }
            }
        }

        addStoryViewModel.uploadResult.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            if (message.contains("success", ignoreCase = true)) {
                activity?.onBackPressed()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                GALLERY_REQUEST_CODE -> {
                    val selectedImage: Uri = data?.data ?: return
                    val file = FileUtil.getFile(requireContext(), selectedImage)
                    photoFile = file
                    binding.ivPreview.setImageURI(selectedImage)
                }

                CAMERA_REQUEST_CODE -> {
                    val photoUri = Uri.fromFile(photoFile)
                    binding.ivPreview.setImageURI(photoUri)
                }
            }
        }
    }




    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val GALLERY_REQUEST_CODE = 100
        private const val CAMERA_REQUEST_CODE = 101
    }
}
