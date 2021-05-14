package com.example.abyss.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.abyss.R
import com.example.abyss.databinding.ActivityHostBinding
import com.example.abyss.extensions.onClick
import com.example.abyss.ui.posts.addpost.AddPostFragmentDirections
import com.example.abyss.utils.HidingNavigationBar
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton


class HostActivity : AppCompatActivity(), HidingNavigationBar {

    private lateinit var binding: ActivityHostBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setUpBottomNavigationAndFab()
    }

    private fun setUpBottomNavigationAndFab() {

        binding.bottomNavigation.setupWithNavController(findNavController(R.id.fragments_host))
        binding.floatingActionButton.onClick {
            navigateToAddPost()
        }
    }

    private fun navigateToAddPost() {
        findNavController(R.id.fragments_host).navigate(AddPostFragmentDirections.actionGlobalAddPostFragment())
    }

    override fun hideNavigationBar(isHide: Boolean) {
        binding.run {
            if (isHide) floatingActionButton.hide() else floatingActionButton.show()
            bottomAppBar.visibility = if (isHide) View.GONE else View.VISIBLE
        }
    }

}