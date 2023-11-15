package com.matiasmandelbaum.alejandriaapp.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.matiasmandelbaum.alejandriaapp.R
import com.matiasmandelbaum.alejandriaapp.common.auth.AuthManager
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.databinding.ActivityMainBinding
import com.matiasmandelbaum.alejandriaapp.domain.model.subscription.Subscription
import com.matiasmandelbaum.alejandriaapp.ui.signout.SignOutDialogFragment
import com.matiasmandelbaum.alejandriaapp.ui.subscription.SubscriptionListViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var id: String
    private lateinit var navController: NavController

    private val viewModel: SubscriptionListViewModel by viewModels()

    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        updateAuthState(auth.currentUser)
    }

    override fun onStart() {
        super.onStart()
        setupAuthStateListener()
        val userUid = AuthManager.getCurrentUser()?.uid
        userUid?.let {
            viewModel.getUserById(userUid)
        }
    }

    override fun onStop() {
        super.onStop()
        AuthManager.removeAuthStateListener(authStateListener)
        Log.d(TAG, "onStop")
    }

    private fun updateMenuItemVisibility(itemId: Int, isVisible: Boolean) {
        val navView: NavigationView = binding.navView
        val menu = navView.menu
        menu.findItem(itemId)?.isVisible = isVisible
    }

    private fun updateAuthState(user: FirebaseUser?) {
        updateMenuItemVisibility(R.id.logout, user != null)
        //updateMenuItemVisibility(R.id.booksReadListFragment, user != null)
    }

    private fun hideMenuItem(itemId: Int) {
        val navView: NavigationView = binding.navView
        val menu = navView.menu
        menu.findItem(itemId)?.isVisible = false
    }

    private fun showMenuItem(itemId: Int) {
        val navView: NavigationView = binding.navView
        val menu = navView.menu
        menu.findItem(itemId)?.isVisible = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView

        Log.d(TAG, "ViewModel hash code: ${viewModel.hashCode()}")

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        // val navController = navHostFragment.navController
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
        setupAuthStateListener()
        observeSubscriptionResult()
    }

    private fun setupDestinationChangedListener() {
        val drawerLayout: DrawerLayout = binding.drawerLayout

        navController.addOnDestinationChangedListener { _, nd: NavDestination, _ ->
            if (nd.id == navController.graph.startDestinationId) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                Log.d("addOnDestinationChangedListener", "En start")
            } else {
                Log.d("addOnDestinationChangedListener", "Fuera de start")
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

    private fun setupAuthStateListener() {
        AuthManager.addAuthStateListener(authStateListener)
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
        Log.d(TAG, "SUCCESS SUBS $subscription")
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
        Log.d(TAG, "error...$errorMessage")
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        Log.d(TAG, "onSupportNavigateUp")
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}