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
import com.matiasmandelbaum.alejandriaapp.R
import com.matiasmandelbaum.alejandriaapp.common.Result
import com.matiasmandelbaum.alejandriaapp.databinding.FragmentHomeListBinding
import com.matiasmandelbaum.alejandriaapp.ui.booklist.Book
import com.matiasmandelbaum.alejandriaapp.ui.booklist.BookListAdapter
import dagger.hilt.android.AndroidEntryPoint


private const val TAG = "HomeListFragment"

@AndroidEntryPoint
class HomeListFragment : Fragment() {
    var books: MutableList<Book> = ArrayList()
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var bookListAdapter : BookListAdapter

    private lateinit var binding: FragmentHomeListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeListBinding.inflate(inflater, container, false)
        bookListAdapter = BookListAdapter()
        binding.lifecycleOwner = viewLifecycleOwner
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = bookListAdapter
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getAllBooks()
        setupMenu()

        for (i in 1..200) {
            books.add(Book("Harry Potter y la Piedra Filosofal", "J.K. Rowling", 5.0f, "urlFalsa"))
            books.add(Book("Adan y Eva", "Mark Twain", 3.2f, "urlFalsa"))
            books.add(
                Book(
                    "El extraño caso del Dr. Jekyll y Mr. Hyde",
                    "Robert Louis Stevenson",
                    4.2f,
                    "urlFalsa"
                )
            )
            books.add(Book("Los Ojos del Perro Siberiano", "Antonio Santa Ana", 3.7f, "urlFalsa"))
        }
        // Notificar al adaptador que los datos han cambiado
        //binding.recyclerView.adapter?.notifyDataSetChanged()

        viewModel.bookListState.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Success ->{
                    handleLoading(false)
                    bookListAdapter.submitList(it.data)
                    Log.d(TAG, " mi data ${it.data.size}")
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