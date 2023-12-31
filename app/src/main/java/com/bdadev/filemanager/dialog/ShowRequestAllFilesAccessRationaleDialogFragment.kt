package com.bdadev.filemanager.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.bdadev.filemanager.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ShowRequestAllFilesAccessRationaleDialogFragment : AppCompatDialogFragment()  {
    private val listener : Listener
        get() = requireParentFragment() as Listener

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext(),theme)
            .setMessage(R.string.all_files_access_rationale_message)
            .setPositiveButton(android.R.string.ok){_,_-> listener.requestAllFilesAccess()}
            .setNegativeButton(android.R.string.cancel, null)
            .create()
    }

    companion object{
        fun show(fragment : Fragment){
            ShowRequestAllFilesAccessRationaleDialogFragment.show(fragment)
        }
    }

    interface Listener{
        fun requestAllFilesAccess()
    }
}