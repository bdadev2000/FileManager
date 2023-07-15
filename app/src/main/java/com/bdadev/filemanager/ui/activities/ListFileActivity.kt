package com.bdadev.filemanager.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.add
import androidx.fragment.app.commit
import com.bdadev.filemanager.R
import com.bdadev.filemanager.ui.fragments.FileListFragment

class ListFileActivity : AppCompatActivity(R.layout.activity_list_file) {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                add<FileListFragment>(R.id.fragment_container_view)
            }
        }
    }
}