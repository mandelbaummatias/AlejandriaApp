package com.matiasmandelbaum.alejandriaapp.ui.home

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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.rpc.context.AttributeContext.Auth
import com.matiasmandelbaum.alejandriaapp.R
import com.matiasmandelbaum.alejandriaapp.common.auth.AuthManager
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.common.auth.AuthManager.addAuthStateListener
import com.matiasmandelbaum.alejandriaapp.common.auth.AuthManager.removeAuthStateListener
import com.matiasmandelbaum.alejandriaapp.databinding.FragmentHomeListBinding
import com.matiasmandelbaum.alejandriaapp.domain.model.book.Book
import com.matiasmandelbaum.alejandriaapp.ui.booklist.BookListAdapter
import com.matiasmandelbaum.alejandriaapp.ui.booklist.BookListener
import dagger.hilt.android.AndroidEntryPoint


private const val TAG = "HomeListFragment"

@AndroidEntryPoint
class HomeListFragment : Fragment() {
    private var currentBookList: List<Book> = emptyList()
    private var isFirstLoad = true
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragmentHomeListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeListBinding.inflate(inflater, container, false)
        binding.recyclerView.isVerticalScrollBarEnabled = false
        binding.lifecycleOwner = viewLifecycleOwner
        binding.recyclerView.adapter = BookListAdapter(BookListener {
            Log.d(TAG, "click")
            findNavController().navigate(
                HomeListFragmentDirections.actionHomeListFragmentToBookDetailsFragment(
                    it
                )
            )
        })
        binding.recyclerView.layoutManager = LinearLayoutManager(this.context)



        return binding.root
    }


    private val authStateListener = FirebaseAuth.AuthStateListener { auth ->
        val user = auth.currentUser
        if (user != null) {
            Log.d(TAG, "Mi user logueado ${user.uid}")
        } else {
            Log.d(TAG, "user is null")
        }
    }

    override fun onResume() {
        Log.d(TAG, "onResume()")
        super.onResume()
    }

    override fun onStart() {
        super.onStart()
        addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        removeAuthStateListener(authStateListener)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (currentBookList.isNotEmpty()) {
            val adapter = binding.recyclerView.adapter as BookListAdapter
            adapter.submitList(currentBookList)
            Log.d(TAG, "Using existing data. Size: ${currentBookList.size}")
        } else {
            viewModel.getAllBooks()
        }
        setupMenu()

        viewModel.bookListState.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Success -> {

                    handleLoading(false)
                    val newBookList = it.data
                    if (isFirstLoad) {
                        isFirstLoad = false
                        currentBookList = newBookList
                        val adapter = binding.recyclerView.adapter as BookListAdapter
                        adapter.submitList(newBookList)
                        Log.d(TAG, "New data loaded. Size: ${newBookList.size}")
                    } else {
                        Log.d(TAG, "Data is the same. Not updating the RecyclerView.")
                    }
                }

                is Result.Loading -> handleLoading(true)
                is Result.Error -> handleLoading(false)
                else -> {
                }
            }
        }
    }

    private fun setupMenu() {
        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onPrepareMenu(menu: Menu) {
                // Handle for example visibility of menu items
            }

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.search_menu, menu)

                val menuItem = menu.findItem(R.id.search)
                val searchView = menuItem.actionView as SearchView
                searchView.queryHint = getString(R.string.search_hint)

                searchView.setOnSearchClickListener {
                    Log.d(TAG, "Click en search!")
                    findNavController().navigate(R.id.action_homeListFragment_to_searchFragment)


                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                Log.d("MenuHost", "onMenuItemSelected executed")
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED).also {
            Log.d(TAG, "Attached to resume lifecycle state")
        }
    }

    private fun handleLoading(isLoading: Boolean) {
        with(binding) {
            if (isLoading) {
                progressBarHome.visibility = View.VISIBLE
            } else {
                progressBarHome.visibility = View.GONE
            }
        }
    }
}