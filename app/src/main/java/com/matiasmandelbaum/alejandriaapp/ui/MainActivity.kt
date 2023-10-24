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
import androidx.browser.customtabs.CustomTabsCallback
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
            showMenuItem(R.id.logout) //var args? //iterar?
            showMenuItem(R.id.booksReadListFragment)

        } else {
            Log.d(TAG, "user is null: $user")
            hideMenuItem(R.id.logout)
            hideMenuItem(R.id.booksReadListFragment)
        }
    }

    override fun onStart() {
        super.onStart()

        Log.d(TAG, "onStart")
        if (::id.isInitialized) {
            Log.d(TAG, "my subscriptionId onStart: $id ")
            viewModel.fetchSubscription(id)
         //   if(viewModel.subscriptionId)
            // 'id' has been initialized, you can use it here
        } else {
            Log.d(TAG, " subscriptionId not initialized")
        }
        //   authManager.addAuthStateListener(authStateListener)
        AuthManager.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        AuthManager.removeAuthStateListener(authStateListener)
        Log.d(TAG, "onStop")
        //  authManager.removeAuthStateListener(authStateListener)
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
//        if (::id.isInitialized) {
//            Log.d(TAG, "my subscriptionId onResume: $id ")
//            viewModel.fetchSubscription(id)
//            // 'id' has been initialized, you can use it here
//        } else {
//            Log.d(TAG, " subscriptionId not initialized")
//        }
    }


    private fun hideMenuItem(itemId: Int) {
        Log.d(TAG, "showMenuItem")
        val navView: NavigationView = binding.navView
        val menu = navView.menu
        Log.d(TAG, "consegui menu en hideMenuItem $menu")
        menu.findItem(itemId)?.isVisible = false// = false
    }

    // Function to show a menu item
    private fun showMenuItem(itemId: Int) {
        Log.d(TAG, "showMenuItem")
        val navView: NavigationView = binding.navView
        val menu = navView.menu
        Log.d(TAG, "consegui menu en showMenuItem $menu")
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
                // R.id.availableBooksFragment//, R.id.signOutDialogFragment
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                //R.id.signOutDialogFragment > -> Log
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

        val customTabsCallback = object : CustomTabsCallback() {
            override fun onNavigationEvent(navigationEvent: Int, extras: Bundle?) {
                super.onNavigationEvent(navigationEvent, extras)
                // Handle Custom Tab navigation events here
                when (navigationEvent) {
                    NAVIGATION_FINISHED -> {
                        Log.d(TAG, "navgiatoin finished")
                        // Custom Tab navigation finished
                        // You can put your code here to handle the event
                    }

                    // Add more cases for other events as needed
                }
            }
        }

//
//        viewModel.subscriptionId.observe(this) {
//            when (it) {
//                is Result.Success -> {
//                    if(it.data == "authorized"){
//                        //
//                        Log.d(TAG, "suscripción pagada $it!!")
//                    } else{
//                        Log.d(TAG, "no se pago todavia...$it!!")
//                    }
//                //Log.d(TAG, "QUE ONDA SUS AL VOLVER ${it.data}")
//
//                }
//
//                is Result.Error -> {
//                    Log.d(TAG, "error!!!")
//                    Toast.makeText(
//                        this,
//                        it.message,
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//
//                else -> {
//                    Log.d(TAG, "qué onda??")
//                    Unit
//                }
//            }
//        }

        viewModel.subscriptionStatus.observe(this) {
            when (it) {
                is Result.Success -> {
                    if(it.data.status == "authorized"){
                        //
                        Log.d(TAG, "suscripción pagada $it!!")
                    } else{
                        Log.d(TAG, "no se pago todavia...$it!!")
                    }
                    //Log.d(TAG, "QUE ONDA SUS AL VOLVER ${it.data}")

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

        viewModel.subscription.observe(this) {
            when (it) {

                is Result.Success -> {
                    Log.d(TAG, "SUCCESS SUBS $it")
                    val url = it.data.initPoint
                    id = it.data.id
                    val intent = CustomTabsIntent.Builder()
                        .build()
                    intent.launchUrl(this@MainActivity, Uri.parse(url))
                }

                is Result.Error -> {
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

//        lifecycleScope.launch {
//            this@MainActivity.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                viewModel.subscription.collect {
//                    when(it){
//                        is Result.Success -> {
//                            val data: Subscription = it.data// Get the data from the Success resource
//                            // Now you can work with the 'data' list
//                            // For example, if you want to access the first item:
//                            Log.d(TAG, "first item $data")
//
//                            Log.d(TAG, "SUCCESS SUBS $it")
//                            val url = it.data.initPoint
//                            id = it.data.id
//                            val intent = CustomTabsIntent.Builder()
//                                .build()
//                            intent.launchUrl(this@MainActivity, Uri.parse(url))                            // Do something with 'firstItem'
//                        }
//                        Result.Loading -> Log.d(TAG,"Loading")
//                        is Result.Error -> {
//                            val errorMessage = it.message
//                            Log.d(TAG, "error! : $errorMessage")// Get the error message from the Error resource
//                            // Handle the error message as needed
//                        }
//                        Result.Finished -> Log.d(TAG, "Finished")
//                        else -> {}
//                    }
//                }
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
