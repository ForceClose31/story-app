package com.example.storyapp.presentation.map

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.R
import com.example.storyapp.data.api.RetrofitClient
import com.example.storyapp.data.model.Story
import com.example.storyapp.databinding.FragmentMapBinding
import com.example.storyapp.utils.DataStoreManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.model.MapStyleOptions
import kotlinx.coroutines.launch

class MapFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var mMap: GoogleMap
    private lateinit var viewModel: MapViewModel
    private lateinit var dataStoreManager: DataStoreManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataStoreManager = DataStoreManager(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            val token = dataStoreManager.getToken()

            Log.d("Token", "Token yang digunakan: $token")

            if (token.isNullOrEmpty()) {
                Toast.makeText(requireContext(), "Token tidak ditemukan. Silakan login terlebih dahulu.", Toast.LENGTH_SHORT).show()
                return@launch
            }

            val apiService = RetrofitClient.instance
            val factory = MapViewModelFactory(apiService)
            viewModel = ViewModelProvider(this@MapFragment, factory)[MapViewModel::class.java]

            val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this@MapFragment)

            observeViewModel(token)
        }
    }

    private fun observeViewModel(token: String) {
        viewModel.stories.observe(viewLifecycleOwner) { stories ->
            addMarkers(stories)
        }

        viewModel.error.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

        viewModel.fetchStoriesWithLocation("Bearer $token")
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isMyLocationButtonEnabled = true

        try {
            val success = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.mapstyle)
            )
            if (!success) {
                Log.e("MapStyle", "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("MapStyle", "Can't find style. Error: ", e)
        }
    }

    private fun addMarkers(stories: List<Story>) {
        for (story in stories) {
            if (story.lat != null && story.lon != null) {
                val location = LatLng(story.lat, story.lon)
                mMap.addMarker(
                    MarkerOptions()
                        .position(location)
                        .title(story.name)
                        .snippet(story.description)
                )
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 5f))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
