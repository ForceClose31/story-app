package com.example.storyapp.presentation.story

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.storyapp.databinding.FragmentAddStoryBinding
import com.example.storyapp.utils.DataStoreManager
import com.example.storyapp.utils.FileUtil
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class AddStoryFragment : Fragment() {

    private var _binding: FragmentAddStoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var addStoryViewModel: AddStoryViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var photoFile: File? = null
    private var userLatitude: Double? = null
    private var userLongitude: Double? = null

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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        binding.switchUseLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                getLocation()
            } else {
                userLatitude = null
                userLongitude = null
                binding.tvLocationStatus.text = "Lokasi: Tidak ada"
            }
        }

        binding.btnSelectPhotoGallery.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (checkAndRequestGalleryPermission()) {
                    openGallery()
                }
            } else {
                openGallery()
            }
        }

        binding.btnSelectPhotoCamera.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (checkAndRequestCameraPermission()) {
                    openCamera()
                }
            } else {
                openCamera()
            }
        }

        binding.buttonAdd.setOnClickListener {
            val description = binding.edAddDescription.text.toString().trim()
            if (description.isEmpty()) {
                Toast.makeText(requireContext(), "Deskripsi tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (photoFile == null || !photoFile!!.exists() || photoFile!!.length() == 0L) {
                Toast.makeText(requireContext(), "Foto tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dataStoreManager = DataStoreManager(requireContext())
            viewLifecycleOwner.lifecycleScope.launch {
                val token = dataStoreManager.getToken()
                val requestBodyDescription = RequestBody.create("text/plain".toMediaTypeOrNull(), description)
                val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), photoFile!!)
                val photoPart = MultipartBody.Part.createFormData("photo", photoFile!!.name, requestFile)

                val lat = userLatitude?.toString()?.let { RequestBody.create("text/plain".toMediaTypeOrNull(), it) }
                val lon = userLongitude?.toString()?.let { RequestBody.create("text/plain".toMediaTypeOrNull(), it) }

                if (token != null) {
                    addStoryViewModel.addStory(token, requestBodyDescription, photoPart, lat, lon)
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

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                userLatitude = location.latitude
                userLongitude = location.longitude
                binding.tvLocationStatus.text = "Lokasi: $userLatitude, $userLongitude"
            } else {
                binding.tvLocationStatus.text = "Gagal mendapatkan lokasi."
            }
        }.addOnFailureListener {
            binding.tvLocationStatus.text = "Gagal mendapatkan lokasi."
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                GALLERY_REQUEST_CODE -> {
                    val selectedImage: Uri = data?.data ?: return
                    val file = FileUtil.getFile(requireContext(), selectedImage)
                    photoFile = file
                    Glide.with(this)
                        .load(selectedImage)
                        .into(binding.ivPreview)
                }

                CAMERA_REQUEST_CODE -> {
                    if (photoFile != null && photoFile!!.exists() && photoFile!!.length() > 0) {
                        photoFile = FileUtil.compressImage(photoFile!!, 1000000)
                        val photoUri = Uri.fromFile(photoFile)
                        Glide.with(this)
                            .load(photoUri)
                            .into(binding.ivPreview)
                    } else {
                        Toast.makeText(requireContext(), "Gagal mengambil foto. Coba lagi.", Toast.LENGTH_SHORT).show()
                        photoFile = null
                    }
                }
            }
        } else if (requestCode == CAMERA_REQUEST_CODE) {
            photoFile = null
            Toast.makeText(requireContext(), "Tidak ada foto yang diambil.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, GALLERY_REQUEST_CODE)
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun openCamera() {
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

    private fun checkAndRequestCameraPermission(): Boolean {
        val permissionsNeeded = mutableListOf<String>()
        if (requireContext().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.CAMERA)
        }
        if (permissionsNeeded.isNotEmpty()) {
            requestPermissions(permissionsNeeded.toTypedArray(), CAMERA_PERMISSION_REQUEST_CODE)
            return false
        }
        return true
    }

    @SuppressLint("InlinedApi")
    private fun checkAndRequestGalleryPermission(): Boolean {
        val permissionsNeeded = mutableListOf<String>()
        if (requireContext().checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.READ_MEDIA_IMAGES)
        }
        if (permissionsNeeded.isNotEmpty()) {
            requestPermissions(permissionsNeeded.toTypedArray(), GALLERY_PERMISSION_REQUEST_CODE)
            return false
        }
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val GALLERY_REQUEST_CODE = 100
        private const val CAMERA_REQUEST_CODE = 101
        private const val CAMERA_PERMISSION_REQUEST_CODE = 102
        private const val GALLERY_PERMISSION_REQUEST_CODE = 103
        private const val LOCATION_PERMISSION_REQUEST_CODE = 104
    }
}
