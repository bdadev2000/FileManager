package com.bdadev.filemanager.util

import androidx.lifecycle.LiveData

@Suppress("UNCHECKED_CAST")
val <T> LiveData<T>.valueCompat: T
    get() = value as T
