package com.matiasmandelbaum.alejandriaapp.ui.search

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.core.view.doOnLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.matiasmandelbaum.alejandriaapp.R
import com.matiasmandelbaum.alejandriaapp.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "SearchFragment"

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private lateinit var binding: FragmentSearchBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated")
        setupMenu()
    }

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onPrepareMenu(menu: Menu) {
            }

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.search_menu, menu)

                val menuItem = menu.findItem(R.id.search)
                val searchView = menuItem.actionView as SearchView

                searchView.queryHint = getString(R.string.search_hint)

                menuItem.expandActionView()
                searchView.doOnLayout {
                    //searchView.clearFocus()
                }

                menuItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
                    override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                        Log.d(TAG, "onMenuItemActionExpand")
                        return true
                    }

                    override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                        Log.d(TAG, "onMenuItemActionCollapse")
                        findNavController().navigateUp()
                        return true
                    }

                })

                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextChange(newText: String?): Boolean {
                        Log.d(TAG, "onQueryTextChange cambiando!")
                        return true
                    }

                    override fun onQueryTextSubmit(query: String?): Boolean {
                        Log.d(TAG, "onQueryTextSubmit submit! (false")
                        //findNavController().navigate(R.id.action_searchFragment_to_booksReadListFragment)
                        //Esto lo pueden descomentar para probar la navegación del search a un fragment
                        return false
                    }
                })
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                Log.d(TAG, "onMenuItemSelected executed")
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED).also {
            Log.d(TAG, "Attached to resume lifecycle state")
        }
    }
}