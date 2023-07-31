package com.bdadev.filemanager.util

import androidx.fragment.app.Fragment

fun Fragment.checkSelfPermission(permission: String) =
    requireContext().checkSelfPermissionCompat(permission)
