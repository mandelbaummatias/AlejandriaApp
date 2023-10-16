package com.matiasmandelbaum.alejandriaapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.core.view.MenuProvider
import androidx.core.view.get
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
import com.matiasmandelbaum.alejandriaapp.common.ex.show
import com.matiasmandelbaum.alejandriaapp.databinding.ActivityMainBinding
import com.matiasmandelbaum.alejandriaapp.ui.signout.SignOutDialogFragment
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        val user = auth.currentUser
        if (user != null) {
            Log.d(TAG, "Mi user logueado ${user.uid}")
        } else {
            Log.d(TAG, "user is null")
        }
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
                R.id.availableBooksFragment//, R.id.signOutDialogFragment
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

//
//        val menu = navView.menu
//        val item = menu.findItem(R.id.logout)
//        if(AuthManager.getCurrentUser() != null){
//            Log.d(TAG, "mi item (que es el logout) $item")
//        } else{
//            menu.removeItem(item.itemId)
//        }

        //navView.setItem

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                //R.id.signOutDialogFragment > -> Log
                R.id.logout -> {
                    Log.d(TAG, "hola logout!!!")
                    val dialog = SignOutDialogFragment()
                    dialog.show(supportFragmentManager, "SignOutDialogFragment")
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
//                val coso = menu.getItem(R.id.loginFragment)
//                if(coso != null){
//                    Log.d(TAG, "LO ENCONTRE AL LOGIN")
//                } else{
//                    Log.d(TAG, "no lo encontré...")
//                }

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

//        navController.addOnDestinationChangedListener { nc: NavController, nd: NavDestination, args: Bundle? ->
//            if (nd.id == nc.graph.startDestinationId) {
//                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
//                Log.d("addOnDestinationChangedListener", "En start")
//            } else {
//                val signOutFragment =
//                    R.id.signOutDialogFragment // Replace with the ID of the fragment you want to search for
//                if (nd.id == signOutFragment) {
//                    navController.popBackStack()
//                    val nuevo = signOutFragment as SignOutDialogFragment
//                    nuevo.show(supportFragmentManager,"hola")
//                    true
//                    // You found the fragment, you can perform actions here
//                    Log.d(
//                        "addOnDestinationChangedListener",
//                        "Found the fragment with ID: $signOutFragment"
//                    )
//                } else {
//                    // Fragment not found
//                    Log.d(
//                        "addOnDestinationChangedListener",
//                        "Fragment with ID: $signOutFragment not found"
//                    )
//                }
//                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
//            }
//        }
    }

    override fun onSupportNavigateUp(): Boolean {
        Log.d(TAG, "onSupportNavigateUp")
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
