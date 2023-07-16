package com.bdadev.filemanager.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bdadev.filemanager.util.valueCompat

class FileListViewModel : ViewModel() {
    private val _isRequestingStorageAccessLiveData = MutableLiveData(false)

    var isStorageAccessRequested : Boolean
        get() = _isRequestingStorageAccessLiveData.valueCompat
        set(value) {
            _isRequestingStorageAccessLiveData.value = value
        }
}