package com.example.myapplication

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.TextButton
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import androidx.compose.material3.Text


class MainActivity : ComponentActivity() {
    private val viewModel = MainScreen()
    private val REQUEST_LOCATION_PERMISSION = 123
    private val ZOOM_CONFIG = 18.5f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getLocationPermission()

        setContent {
            if (!viewModel.locationPermissionEnabled.value!!) {
               Column() {
                    TextButton(onClick = { getLocationPermission() }) {
                        Text("Permissão não habilitada, clique para solicitar a autorização novamente")
                    }
                }
            } else {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = CameraPositionState(
                        position = CameraPosition.fromLatLngZoom(
                            viewModel.location,
                            ZOOM_CONFIG
                        )
                    ),
                    uiSettings = MapUiSettings(
                        zoomControlsEnabled = false
                    )
                ) {
                    Marker(
                        state = MarkerState(viewModel.location),
                        title = "Your current location"
                    )
                }
            }
        }
    }

    private fun getLocationPermission() {
        val isPermissionGranted = ContextCompat.checkSelfPermission(
            this.applicationContext,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        viewModel.permissionGranted(isPermissionGranted)

        if (isPermissionGranted) {
            getCurrentUserLocation()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
            getLocationPermission()
        }
    }

    private fun getCurrentUserLocation() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        try {
            viewModel.locationPermissionEnabled.value.let {
                val locationResult = fusedLocationProviderClient.lastLocation

                locationResult.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val lastKnewLocation = task.result

                        if (lastKnewLocation != null) {
                            viewModel.currentUserCordinates(
                                LatLng(lastKnewLocation.latitude, lastKnewLocation.longitude)
                            )
                        }
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.d("Exception Security", "${e.message}")
        }
    }
}
