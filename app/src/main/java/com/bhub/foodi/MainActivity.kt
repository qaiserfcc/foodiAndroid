package com.bhub.foodi

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.bhub.foodi.core.BaseFragment
import com.bhub.foodi.databinding.ActivityMainBinding
import com.bhub.foodi.ui.home.HomeViewModel
import com.bhub.foodi.utilities.PreferencesManager
//import com.hallyu.style.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val REQUEST_CODE_POST_NOTIFICATIONS: Int = 111
    val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val current = PreferencesManager.get<String>("lang") ?: "en"
        if (current == "en") {
            FoodiApplication.lang = 0
        } else {
            FoodiApplication.lang = 1
        }
        Log.d("TAG", "onCreate:current Language:$current ")
        val host: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment?
                ?: return

        val navController = host.navController
        setupBottomNavMenu(navController)
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission already granted, proceed with showing notifications
        } else {
            // Permission not granted, request it

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                requestNotificationPermission()
            }
        }
    }

    private fun requestNotificationPermission() {
        // Check if an explanation is needed (e.g., if the user denied the permission before)
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.POST_NOTIFICATIONS
            )
        ) {
            // Show an explanation to the user (e.g., in a dialog) explaining why the permission is needed
            // ...
            // Then, request the permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_CODE_POST_NOTIFICATIONS
            )
        } else {
            // No explanation needed, directly request the permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                REQUEST_CODE_POST_NOTIFICATIONS
            )
        }
    }

    override fun onBackPressed() {
        try {
            var handled = false
            supportFragmentManager.fragments.forEach { fragment ->
                if (fragment is NavHostFragment) {
                    fragment.childFragmentManager.fragments.forEach { childFragment ->
                        if (childFragment is BaseFragment<*>) {
                            handled = childFragment.onBackPressed()
                            if (handled) {
                                return
                            }
                        }
                    }
                }
            }

            if (!handled) {
                super.onBackPressed()
            }
        } catch (e: Exception) {
            super.onBackPressed()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                Log.d("TAG", "onRequestPermissionsResult: PERMISSION_GRANTED")
            } else {
                Log.d("TAG", "onRequestPermissionsResult: PERMISSION_DENIED")

                // Permission denied, handle accordingly (e.g., show a message or disable notification features)
            }
        }
    }

    private fun setupBottomNavMenu(navController: NavController) {
        binding.bottomNavigation.setupWithNavController(navController)
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            NavigationUI.onNavDestinationSelected(item, navController)
            navController.popBackStack(item.itemId, inclusive = false)
            true
        }
    }

}