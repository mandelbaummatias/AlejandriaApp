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
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.matiasmandelbaum.alejandriaapp.R
import com.matiasmandelbaum.alejandriaapp.common.auth.AuthManager
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.databinding.ActivityMainBinding
import com.matiasmandelbaum.alejandriaapp.ui.signout.SignOutDialogFragment
import com.matiasmandelbaum.alejandriaapp.ui.subscription.SubscriptionViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var id: String

    private val viewModel: SubscriptionViewModel by viewModels()

    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        val user = auth.currentUser
        if (user != null) {
            Log.d(TAG, "mi user logueado $user")
            val userUid = AuthManager.getCurrentUser()?.uid
            userUid?.let {
                viewModel.getUserById(it)
            }

            showMenuItem(R.id.logout)
            showMenuItem(R.id.booksReadListFragment)

        } else {
            Log.d(TAG, "user is null: $user")
            hideMenuItem(R.id.logout)
            hideMenuItem(R.id.booksReadListFragment)
        }
    }

    override fun onStart() {
        super.onStart()
        val userUid = AuthManager.getCurrentUser()?.uid
        Log.d(TAG, "UI onStart : $userUid")
        if(userUid != null){
            viewModel.getUserById(userUid)
        } else{
            Log.d(TAG, "no hay UID")
        }


        Log.d(TAG, "onStart")
        AuthManager.addAuthStateListener(authStateListener)

    }

    override fun onStop() {
        super.onStop()
        AuthManager.removeAuthStateListener(authStateListener)
        Log.d(TAG, "onStop")
    }

    private fun hideMenuItem(itemId: Int) {
        val navView: NavigationView = binding.navView
        val menu = navView.menu
        menu.findItem(itemId)?.isVisible = false// = false
    }

    // Function to show a menu item
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


        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment


        val navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeListFragment, R.id.booksReadListFragment, R.id.subscriptionListFragment,
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.logout -> {
                    Log.d(TAG, "hola logout!!!")
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

        addMenuProvider(object : MenuProvider {

            override fun onPrepareMenu(menu: Menu) {

            }

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean = when (menuItem.itemId) {

                android.R.id.home -> navController.navigateUp(appBarConfiguration)
                R.id.signOutDialogFragment -> {
                    true
                }

                else -> true
            }

        })

        navController.addOnDestinationChangedListener { nc: NavController, nd: NavDestination, args: Bundle? ->
            if (nd.id == nc.graph.startDestinationId) {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                Log.d("addOnDestinationChangedListener", "En start")


            } else {
                Log.d("addOnDestinationChangedListener", "Fuera de start")
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            }
        }

        viewModel.subscriptionStatus.observe(this) {
            when (it) {
                is Result.Success -> {
                    when (it.data.status) {
                        "authorized" -> {
                            //
                            Log.d(TAG, "suscripción pagada $it!!")
                        }

                        "pending" -> {
                            Log.d(TAG, "está pending!!!...$it!!")

                        }

                        else -> {
                            Log.d(TAG, "esta paused o cancelled...$it!!")
                        }
                    }
                }

                is Result.Error -> {
                    Log.d(TAG, "error!!!")
                    Toast.makeText(
                        this,
                        it.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {
                    Log.d(TAG, "qué onda??")
                    Unit
                }
            }
        }

        viewModel.user.observe(this) {
            when (it) {
                is Result.Success -> {
                    if (it.data.subscriptionId.isNotBlank()) {
                        Log.d("User", "is not blank ${it.data.subscriptionId}")
                    } else {
                        Log.d("User", "is blank ${it.data.subscriptionId}")
                    }
                }

                is Result.Error -> {
                    Log.d("User", "error ${it.message}")
                }

                is Result.Finished -> Unit
                Result.Loading -> Unit
            }
        }

        viewModel.subscription.observe(this) {
            when (it) {
                is Result.Success -> {
                    Log.d(TAG, "SUCCESS SUBS $it")
                    val url = it.data.initPoint
                    viewModel.addSubscriptionIdToUser(
                        it.data.id,
                        AuthManager.getCurrentUser()?.uid.toString()
                    )
                    viewModel.updateInitPointUrl(it.data.initPoint)
                    id = it.data.id
                    val intent = CustomTabsIntent.Builder()
                        .build()
                    intent.launchUrl(this@MainActivity, Uri.parse(url))
                }

                is Result.Error -> {
                    Log.d(TAG, "error...${it.message}")
                    Toast.makeText(
                        this,
                        it.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {
                    Unit
                }
            }
        }
    }


    override fun onSupportNavigateUp(): Boolean {
        Log.d(TAG, "onSupportNavigateUp")
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

}