package ru.freedot.osmdroidapp.ui.home

import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.config.Configuration.getInstance
import org.osmdroid.config.Configuration.setConfigurationProvider
import org.osmdroid.library.BuildConfig
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import ru.freedot.osmdroidapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private val REQUEST_PERMISSIONS_REQUEST_CODE = 1

    private lateinit var mapView: MapView
    private lateinit var mapController: IMapController

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //val context = requireActivity()
        getInstance().load(this@HomeFragment.requireContext(), PreferenceManager.getDefaultSharedPreferences(this@HomeFragment.requireContext()))

        Configuration.getInstance().setUserAgentValue(BuildConfig.BUILD_TYPE)
        mapView = binding.mapview
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapController = mapView.controller
        mapView.minZoomLevel= 3.8
        mapView.maxZoomLevel= 18.0
        mapController.setZoom(5.0)

        mapView.setBuiltInZoomControls(false)
        mapView.setMultiTouchControls(true)

        val myLocationOverlay =
            MyLocationNewOverlay(GpsMyLocationProvider(requireContext()), mapView)
        myLocationOverlay.enableMyLocation()
        //myLocationOverlay.isOptionsMenuEnabled = true
        myLocationOverlay.enableFollowLocation() //Карта будет сосредоточена на вашем текущем местоположении и автоматически прокручивается по мере вашего движения. Прокрутка карты в пользовательском интерфейсе будет отключена.
        mapController.setCenter(myLocationOverlay.myLocation)
        mapView.setFlingEnabled(true)
        mapView.overlays.add(myLocationOverlay)

        switchFunc()
        binding.fabMyLocation.setOnClickListener {
            mapController.setCenter(myLocationOverlay.myLocation)
            mapView.animate().setDuration(2000L)

            mapView.overlays.add(myLocationOverlay)
        }
        return root
    }

    private fun switchFunc() {
        binding.switchLock.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                binding.switchLock.setText("lock")

                //binding.switchLock.thumbDrawable.setTint(255)
            } else
                binding.switchLock.setText("unlock")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        val permissionsToRequest = ArrayList<String>()
        var i = 0
        while (i < grantResults.size) {
            permissionsToRequest.add(permissions[i])
            i++
        }
        if (permissionsToRequest.size > 0) {

            ActivityCompat.requestPermissions(
                requireActivity(),
                permissionsToRequest.toTypedArray(),
                REQUEST_PERMISSIONS_REQUEST_CODE)
        }
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}