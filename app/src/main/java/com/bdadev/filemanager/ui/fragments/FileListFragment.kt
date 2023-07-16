package com.bdadev.filemanager.ui.fragments

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.bdadev.filemanager.R
import com.bdadev.filemanager.application.application
import com.bdadev.filemanager.databinding.FragmentFileListBinding
import com.bdadev.filemanager.dialog.ShowRequestAllFilesAccessRationaleDialogFragment
import com.bdadev.filemanager.dialog.ShowRequestStoragePermissionRationaleDialogFragment
import com.bdadev.filemanager.util.ShowRequestStoragePermissionInSettingsRationaleDialogFragment
import com.bdadev.filemanager.util.checkSelfPermission
import com.bdadev.filemanager.util.checkSelfPermissionCompat
import com.bdadev.filemanager.util.viewModels
import com.bdadev.filemanager.view_model.FileListViewModel

class FileListFragment : Fragment(), ShowRequestStoragePermissionRationaleDialogFragment.Listener,
    ShowRequestStoragePermissionInSettingsRationaleDialogFragment.Listener,
    ShowRequestAllFilesAccessRationaleDialogFragment.Listener {
    private val viewModel by viewModels { { FileListViewModel() } }
    private val requestStoragePermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(), this::onRequestStoragePermissionResult
    )
    private val requestStoragePermissionInSettingsLauncher = registerForActivityResult(
        RequestStoragePermissionInSettingsContract(),
        this::onRequestStoragePermissionInSettingsResult
    )
    private val requestAllFilesAccessLauncher = registerForActivityResult(
        RequestAllFilesAccessContract(), this::onRequestAllFilesAccessResult
    )
    private lateinit var binding: Binding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = Binding.inflate(inflater, container, false)
        .also { binding = it }
        .root

    override fun onResume() {
        super.onResume()
        checkStorageAccess()
    }

    private fun checkStorageAccess() {
        if (viewModel.isStorageAccessRequested) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                ShowRequestAllFilesAccessRationaleDialogFragment.show(this)
                viewModel.isStorageAccessRequested = true
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
            ) {
                if (shouldShowRequestPermissionRationale(
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                ) {
                    ShowRequestStoragePermissionRationaleDialogFragment.show(this)
                } else {
                    requestStoragePermission()
                }
                viewModel.isStorageAccessRequested = true
            }
        }
    }

    override fun requestStoragePermission() {
        requestStoragePermissionLauncher.launch(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    override fun requestStoragePermissionInSettings() {
        requestStoragePermissionInSettingsLauncher.launch(Unit)
    }

    private class RequestStoragePermissionInSettingsContract
        : ActivityResultContract<Unit, Boolean>() {
        override fun createIntent(context: Context, input: Unit): Intent =
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", context.packageName, null)
            )

        override fun parseResult(resultCode: Int, intent: Intent?): Boolean =
            application.checkSelfPermissionCompat(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
    }

    private class RequestAllFilesAccessContract : ActivityResultContract<Unit, Boolean>() {
        @RequiresApi(Build.VERSION_CODES.R)
        override fun createIntent(context: Context, input: Unit): Intent =
            Intent(
                android.provider.Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                Uri.fromParts("package", context.packageName, null)
            )

        @RequiresApi(Build.VERSION_CODES.R)
        override fun parseResult(resultCode: Int, intent: Intent?): Boolean =
            Environment.isExternalStorageManager()
    }

    private fun onRequestStoragePermissionInSettingsResult(isGranted: Boolean) {
        if (isGranted) {
            viewModel.isStorageAccessRequested = false
//            refresh()
        }
    }

    private fun onRequestAllFilesAccessResult(isGranted: Boolean) {
        if (isGranted) {
            viewModel.isStorageAccessRequested = false
//            refresh()
        }
    }

    private fun onRequestStoragePermissionResult(isGranted: Boolean) {
        if (isGranted) {
            viewModel.isStorageAccessRequested = false
//            refresh()
        } else if (!shouldShowRequestPermissionRationale(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            ShowRequestStoragePermissionInSettingsRationaleDialogFragment.show(this)
        }
    }

    override fun requestAllFilesAccess() {
        requestAllFilesAccessLauncher.launch(Unit)
    }
    private class Binding private constructor(
        val root: View,
    ) {
        companion object {
            fun inflate(
                inflater: LayoutInflater,
                root: ViewGroup?,
                attachToRoot: Boolean
            ): Binding {
                val binding = FragmentFileListBinding.inflate(inflater, root, attachToRoot)
                val bindingRoot = binding.root
                return Binding(bindingRoot)
            }

        }

    }
}