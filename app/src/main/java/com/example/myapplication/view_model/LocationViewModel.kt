package com.example.myapplication.view_model

import android.content.Context
import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.myapplication.base.BaseViewModel
import com.example.myapplication.data.database.AppDatabase
import com.example.myapplication.data.database.LocationDao
import com.example.myapplication.data.model.LocationLock
import com.example.myapplication.data.model.RecentSearch
import com.example.myapplication.utils.PreferenceHelper
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preference: PreferenceHelper
) : BaseViewModel() {

    private val _locationLockItem = MutableLiveData<LocationLock>()
    val locationLockItem: LiveData<LocationLock> get() = _locationLockItem

    private val _locations = MutableLiveData<List<LocationLock>>()
    val locations: LiveData<List<LocationLock>> get() = _locations

    private var locationDao: LocationDao? = null

    private val _searchLocation = MutableLiveData<MutableList<RecentSearch>>()
    val searchLocation: MutableLiveData<MutableList<RecentSearch>>
        get() = _searchLocation

    init {
        locationDao = AppDatabase.getInstance(context).getLocationDao()
    }

    fun getName(name: String, index: Int = 1): String {
        var currentIndex = index
        var nameCurrent = name
        val nameLocation = "$name $currentIndex"
        if (nameCurrent == "Location") {
            nameCurrent = "Location 1"
        }
        return if (locationDao?.isExits(nameCurrent) == false) {
            nameCurrent
        } else if (locationDao?.isExits(nameLocation) == false) {
            nameLocation
        } else getName(name, currentIndex + 1)
    }

    fun setLocation(locationLock: LocationLock) {
        _locationLockItem.postValue(locationLock)
    }

    fun setName(locationName: String) {
        _locationLockItem.value = _locationLockItem.value?.apply {
            this.locationName = locationName
        }
    }

    fun setRadius(value: Int) {
        _locationLockItem.value = _locationLockItem.value?.apply { radius = value }
    }

    fun setInverse(value: Boolean) {
        _locationLockItem.value = _locationLockItem.value?.apply { isInverse = value }
    }

    fun saveLocation(location: LocationLock) = viewModelScope.launch(Dispatchers.IO) {
        _locations.value?.find { it.id == location.id }?.let {
            locationDao?.updateLocation(location)
        } ?: locationDao?.insertLocation(location)
    }

    fun getNameLocation(): String {
        return _locationLockItem.value?.locationName ?: "Location ${_locations.value?.size}"
    }

    fun getListLocations() {
        viewModelScope.launch(Dispatchers.IO) {
            _locations.postValue(locationDao?.getLocationsSync())
        }
    }

    fun saveLocationToDb(location: LocationLock) {
        viewModelScope.launch(Dispatchers.IO) {
            _locations.value?.find { it.id == location.id }?.let {
                locationDao?.updateLocation(location)
            } ?: locationDao?.insertLocation(location)
            getListLocations()
        }
    }

    fun updateLocationDb(locationLock: LocationLock) {
        viewModelScope.launch(Dispatchers.IO) {
            locationDao?.updateLocation(locationLock)
        }
    }

    fun deleteLocationDb(locationLock: LocationLock) {
        viewModelScope.launch(Dispatchers.IO) {
            locationDao?.deleteLocation(locationLock.id)
            getListLocations()
        }
    }

    fun searchLocation(context: Context, name: String) {
        if (name.isEmpty()) return
        Timber.d(name)
        viewModelScope.launch(Dispatchers.IO) {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addressList = geocoder.getFromLocationName(name, 10)
            Timber.d(Gson().toJson(addressList))
            val data: MutableList<RecentSearch> = mutableListOf()
            addressList?.forEach {
                Timber.d(Gson().toJson(it))
//                val address = getAddress(context, it.latitude, it.longitude)
//                Timber.d(address)
//                if (address.isNotEmpty()) {
//                    val rc = RecentSearch(0, it.latitude, it.longitude, name, address, 1)
//                    data.add(rc)
//                }
                val addressListSearch = getListAddress(context, it.latitude, it.longitude)
                if (addressListSearch?.isNotEmpty() == true) {
                    addressListSearch.forEach { item ->
                        val rc = RecentSearch(
                            0, item.latitude, item.longitude, name, item.getAddressLine(0), 1
                        )
                        data.add(rc)
                    }
                }
            }
            _searchLocation.postValue(data)
        }
    }

    private fun getAddress(context: Context, lat: Double, lng: Double): String {
        var addrress = ""
        val geocoder = Geocoder(context, Locale.getDefault())
        val addressList = geocoder.getFromLocation(lat, lng, 10) ?: return addrress
        if (addressList.isNotEmpty()) {
            Timber.d(Gson().toJson(addressList))
            addrress = addressList.first().getAddressLine(0)
        }
        return addrress
    }

    private fun getListAddress(context: Context, lat: Double, lng: Double): List<Address>? {
        var addressListString = mutableListOf<Address>()
        val geocoder = Geocoder(context, Locale.getDefault())
        val addressList = geocoder.getFromLocation(lat, lng, 10) ?: return null
        if (addressList.isNotEmpty()) {
            Timber.d(Gson().toJson(addressList))
            addressList.forEach {
                addressListString.add(it)

            }
            return addressListString
        }
        return null
    }

    fun saveSearchLocation(context: Context, location: RecentSearch) {
        viewModelScope.launch(Dispatchers.IO) {
            AppDatabase.getInstance(
                context
            ).getSearchLocationDao().insertSearchLocation(location)
        }
    }

    fun getSearchLocationHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            val history = AppDatabase.getInstance(
                context
            ).getSearchLocationDao().getLocationsSearchHistory()

            history.map { it.type = 0 }

            _searchLocation.postValue(history)
        }
    }
}