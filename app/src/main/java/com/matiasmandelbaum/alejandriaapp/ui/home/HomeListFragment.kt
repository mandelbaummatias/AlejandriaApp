package com.matiasmandelbaum.alejandriaapp.ui.home

import android.os.Bundle
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
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.matiasmandelbaum.alejandriaapp.R
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.databinding.FragmentHomeListBinding
import com.matiasmandelbaum.alejandriaapp.domain.model.book.Book
import com.matiasmandelbaum.alejandriaapp.ui.adapter.BookListAdapter
import com.matiasmandelbaum.alejandriaapp.ui.adapter.BookListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeListFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragmentHomeListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeListBinding.inflate(inflater, container, false).apply {
            recyclerView.isVerticalScrollBarEnabled = false
            lifecycleOwner = viewLifecycleOwner
            recyclerView.adapter = BookListAdapter(BookListener {
                findNavController().navigate(
                    HomeListFragmentDirections.actionHomeListFragmentToBookDetailsFragment(it)
                )
            })
            recyclerView.layoutManager = LinearLayoutManager(context)
            initCollectors()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

                searchView.setOnSearchClickListener {
                    findNavController().navigate(R.id.action_homeListFragment_to_searchFragment)


                }
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return true
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
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

    private fun showGetAllBooksErrorMessage() {
        Snackbar.make(
            requireView(),
            getString(R.string.error_al_traer_los_libros),
            Snackbar.LENGTH_SHORT
        )
            .show()
    }

    private fun initCollectors() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bookListState.collect {
                    when (it) {
                        is Result.Success -> {
                            handleLoading(false)
                            val data: List<Book> = it.data
                            val adapter = binding.recyclerView.adapter as BookListAdapter
                            adapter.submitList(data)
                        }

                        Result.Loading -> handleLoading(true)
                        is Result.Error -> {
                            handleLoading(false)
                            showGetAllBooksErrorMessage()
                        }
                    }
                }
            }
        }
    }

}