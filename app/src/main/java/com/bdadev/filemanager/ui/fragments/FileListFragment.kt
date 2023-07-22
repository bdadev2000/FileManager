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
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.Toolbar
import androidx.core.view.updatePaddingRelative
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bdadev.filemanager.R
import com.bdadev.filemanager.application.application
import com.bdadev.filemanager.databinding.FileListFragmentAppBarIncludeBinding
import com.bdadev.filemanager.databinding.FileListFragmentBottomBarIncludeBinding
import com.bdadev.filemanager.databinding.FileListFragmentContentIncludeBinding
import com.bdadev.filemanager.databinding.FileListFragmentIncludeBinding
import com.bdadev.filemanager.databinding.FileListFragmentSpeedDialIncludeBinding
import com.bdadev.filemanager.databinding.FragmentFileListBinding
import com.bdadev.filemanager.dialog.ShowRequestAllFilesAccessRationaleDialogFragment
import com.bdadev.filemanager.dialog.ShowRequestStoragePermissionRationaleDialogFragment
import com.bdadev.filemanager.util.ShowRequestStoragePermissionInSettingsRationaleDialogFragment
import com.bdadev.filemanager.util.checkSelfPermission
import com.bdadev.filemanager.util.checkSelfPermissionCompat
import com.bdadev.filemanager.util.viewModels
import com.bdadev.filemanager.view.BreadcrumbLayout
import com.bdadev.filemanager.view.CoordinatorAppBarLayout
import com.bdadev.filemanager.view.PersistentBarLayout
import com.bdadev.filemanager.view.PersistentDrawerLayout
import com.bdadev.filemanager.view_model.FileListViewModel
import com.google.android.material.appbar.AppBarLayout
import com.leinardi.android.speeddial.SpeedDialView

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
        val drawerLayout: DrawerLayout? = null,
        val persistentDrawerLayout: PersistentDrawerLayout? = null,
        val persistentBarLayout: PersistentBarLayout,
        val appBarLayout: CoordinatorAppBarLayout,
        val toolbar: Toolbar,
        val overlayToolbar: Toolbar,
        val breadcrumbLayout: BreadcrumbLayout,
        val contentLayout: ViewGroup,
        val progress: ProgressBar,
        val errorText: TextView,
        val emptyView: View,
        val swipeRefreshLayout: SwipeRefreshLayout,
        val recyclerView: RecyclerView,
        val bottomBarLayout: ViewGroup,
        val bottomToolbar: Toolbar,
        val speedDialView: SpeedDialView
    ) {
        companion object {
            fun inflate(
                inflater: LayoutInflater,
                root: ViewGroup?,
                attachToRoot: Boolean
            ): Binding {
                val binding = FragmentFileListBinding.inflate(inflater, root, attachToRoot)
                val bindingRoot = binding.root
                val includeBinding = FileListFragmentIncludeBinding.bind(bindingRoot)
                val appBarBinding = FileListFragmentAppBarIncludeBinding.bind(bindingRoot)
                val contentBinding = FileListFragmentContentIncludeBinding.bind(bindingRoot)
                val bottomBarBinding = FileListFragmentBottomBarIncludeBinding.bind(bindingRoot)
                val speedDialBinding = FileListFragmentSpeedDialIncludeBinding.bind(bindingRoot)
                return Binding(
                    bindingRoot, includeBinding.drawerLayout,null,
                    includeBinding.persistentBarLayout, appBarBinding.appBarLayout,
                    appBarBinding.toolbar, appBarBinding.overlayToolbar,
                    appBarBinding.breadcrumbLayout, contentBinding.contentLayout,
                    contentBinding.progress, contentBinding.errorText, contentBinding.emptyView,
                    contentBinding.swipeRefreshLayout, contentBinding.recyclerView,
                    bottomBarBinding.bottomBarLayout, bottomBarBinding.bottomToolbar,
                    speedDialBinding.speedDialView
                )
            }

        }

    }
}