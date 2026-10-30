package com.example.mediaapp

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import com.example.mediaapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var selectedItem = R.id.nav_broadcast

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applySystemInsets(binding.mainContent)
        applySystemInsets(binding.navigationView)

        setSupportActionBar(binding.toolbar)

        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        )

        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        selectedItem = savedInstanceState?.getInt("selected_item")
            ?: R.id.nav_broadcast

        binding.navigationView.setNavigationItemSelectedListener { item ->
            if (item.itemId != selectedItem) {
                selectedItem = item.itemId
                showDestination(selectedItem)
            }

            updateMenu()
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        if (savedInstanceState == null) {
            showDestination(selectedItem)
        }

        updateMenu()

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    when {
                        binding.drawerLayout.isDrawerOpen(GravityCompat.START) -> {
                            binding.drawerLayout.closeDrawer(GravityCompat.START)
                        }

                        selectedItem != R.id.nav_broadcast -> {
                            selectedItem = R.id.nav_broadcast
                            showDestination(selectedItem)
                            updateMenu()
                        }

                        else -> finish()
                    }
                }
            }
        )
    }

    private fun showDestination(itemId: Int) {
        val fragment: Fragment = when (itemId) {
            R.id.nav_image -> ImageScaleFragment()
            R.id.nav_video -> VideoFragment()
            R.id.nav_audio -> AudioFragment()
            else -> BroadcastFragment()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    private fun updateMenu() {
        binding.navigationView.setCheckedItem(selectedItem)

        supportActionBar?.title = when (selectedItem) {
            R.id.nav_image -> "Image Scale"
            R.id.nav_video -> "Video"
            R.id.nav_audio -> "Audio"
            else -> "Broadcast Receiver"
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("selected_item", selectedItem)
        super.onSaveInstanceState(outState)
    }
}
