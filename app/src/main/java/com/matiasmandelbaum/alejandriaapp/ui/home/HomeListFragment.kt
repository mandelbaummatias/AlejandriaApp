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
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.matiasmandelbaum.alejandriaapp.R
import com.matiasmandelbaum.alejandriaapp.databinding.FragmentHomeListBinding
import com.matiasmandelbaum.alejandriaapp.ui.booklist.Book
import com.matiasmandelbaum.alejandriaapp.ui.booklist.BookListAdapter
import dagger.hilt.android.AndroidEntryPoint


private const val TAG = "HomeListFragment"

@AndroidEntryPoint
class HomeListFragment : Fragment() {

    lateinit var v : View
    lateinit var bookRecycler : RecyclerView
    var books : MutableList<Book> = ArrayList()

    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var bookListAdapter: BookListAdapter

    private lateinit var binding: FragmentHomeListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeListBinding.inflate(inflater, container, false)
       // binding.recyclerView.layoutManager = LinearLayoutManager(context)
      //  v = inflater.inflate(R.layout.fragment_home_list, container, false)

        bookRecycler = v.findViewById(R.id.recyclerView)
        linearLayoutManager = LinearLayoutManager(context)
        bookRecycler.layoutManager = linearLayoutManager
        bookListAdapter = BookListAdapter(books) // Asegúrate de crear el adaptador adecuado
        bookRecycler.adapter = bookListAdapter

        return v

       // binding.recyclerView.layoutManager = LinearLayoutManager(this.context)
        //viewModel.getItemsList()
        //  fabViewModel.setFabVisibility(true)

      //  return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupMenu()

        for (i in 1 .. 200) {
            books.add(Book("Harry Potter y la Piedra Filosofal", "J.K. Rowling", 5.0f, "urlFalsa"))
            books.add(Book("Adan y Eva", "Mark Twain", 3.2f, "urlFalsa"))
            books.add(Book("El extraño caso del Dr. Jekyll y Mr. Hyde", "Robert Louis Stevenson", 4.2f, "urlFalsa"))
            books.add(Book("Los Ojos del Perro Siberiano", "Antonio Santa Ana", 3.7f, "urlFalsa"))
        }
        // Notificar al adaptador que los datos han cambiado
        bookListAdapter.notifyDataSetChanged()
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
}