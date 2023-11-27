package com.matiasmandelbaum.alejandriaapp.ui

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.MenuProvider
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseUser
import com.matiasmandelbaum.alejandriaapp.R
import com.matiasmandelbaum.alejandriaapp.common.auth.AuthManager
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.databinding.ActivityMainBinding
import com.matiasmandelbaum.alejandriaapp.domain.model.subscription.Subscription
import com.matiasmandelbaum.alejandriaapp.ui.signout.SignOutDialogFragment
import com.matiasmandelbaum.alejandriaapp.ui.subscription.SubscriptionListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var id: String
    private lateinit var navController: NavController
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var drawerLayout: DrawerLayout

    private val viewModel: SubscriptionListViewModel by viewModels()

    override fun onStart() {
        super.onStart()
        AuthManager.authStateLiveData.observe(this) {
            it?.let {
                viewModel.getUserById(it.uid)
            }
            updateAuthState(it)
        }
    }

    private fun updateMenuItemVisibility(itemId: Int, isVisible: Boolean) {
        val navView: NavigationView = binding.navView
        val menu = navView.menu
        menu.findItem(itemId)?.isVisible = isVisible
    }

    private fun updateAuthState(user: FirebaseUser?) {
        updateMenuItemVisibility(R.id.logout, user != null)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val isDarkMode = sharedPreferences.getBoolean("darkMode", false)

        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        drawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeListFragment, R.id.subscriptionListFragment,
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        setupUI()
    }

    private fun setupUI() {
        setupDestinationChangedListener()
        setupNavigationItemSelectedListener(navController, binding.drawerLayout)
        setupMenuProvider()
        observeSubscriptionResult()
    }

    private fun setupDestinationChangedListener() {
        val drawerLayout: DrawerLayout = binding.drawerLayout

        navController.addOnDestinationChangedListener { _, nd: NavDestination, _ ->
            if (nd.id == navController.graph.startDestinationId) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            } else {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }
    }

    private fun setupNavigationItemSelectedListener(
        navController: NavController,
        drawerLayout: DrawerLayout
    ) {
        val navView: NavigationView = binding.navView
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.logout -> {
                    val dialog = SignOutDialogFragment()
                    dialog.show(supportFragmentManager, "SignOutDialogFragment")
                }

                R.id.profile -> {
                    if (AuthManager.getCurrentUser() != null) {
                        navController.navigate(R.id.userProfileFragment)
                    } else {
                        navController.navigate(R.id.loginFragment)
                    }
                }

                else -> {
                    NavigationUI.onNavDestinationSelected(it, navController)
                    drawerLayout.closeDrawers()
                }
            }
            true
        }
    }

    private fun setupMenuProvider() {
        addMenuProvider(object : MenuProvider {
            override fun onPrepareMenu(menu: Menu) {}

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {}

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                val navController = findNavController(R.id.nav_host_fragment)
                return when (menuItem.itemId) {
                    android.R.id.home -> navController.navigateUp(appBarConfiguration)
                    R.id.signOutDialogFragment -> true
                    else -> true
                }
            }
        })
    }
    private fun observeSubscriptionResult() {
        viewModel.subscription.observe(this) {
            when (it) {
                is Result.Success -> handleSubscriptionSuccess(it.data)
                is Result.Error -> handleSubscriptionError(it.message)
                else -> Unit
            }
        }
    }

    private fun handleSubscriptionSuccess(subscription: Subscription) {
        val url = subscription.initPoint
        viewModel.addSubscriptionIdToUser(
            subscription.id,
            AuthManager.getCurrentUser()?.uid.toString()
        )
        id = subscription.id
        val intent = CustomTabsIntent.Builder().build()
        intent.launchUrl(this@MainActivity, Uri.parse(url))
    }

    private fun handleSubscriptionError(errorMessage: String?) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}