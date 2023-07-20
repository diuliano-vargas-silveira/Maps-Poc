package com.example.myapplication

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

class MainScreen : ViewModel() {
    var locationPermissionEnabled = MutableLiveData(false)
        private set

    var location  by mutableStateOf(LatLng(0.0, 0.0))
        private set

    fun currentUserCordinates(latLng: LatLng) {
        location = latLng
    }

    fun permissionGranted(setGranted: Boolean) {
        locationPermissionEnabled.value = setGranted
    }
}
