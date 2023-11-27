package com.matiasmandelbaum.alejandriaapp.ui.searchresult

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.databinding.FragmentSearchResultListBinding
import com.matiasmandelbaum.alejandriaapp.ui.adapter.BookListAdapter
import com.matiasmandelbaum.alejandriaapp.ui.adapter.BookListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchResultListFragment : Fragment() {

    private lateinit var binding: FragmentSearchResultListBinding
    private val viewModel: SearchResultViewModel by viewModels()
    private lateinit var bookListAdapter: BookListAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchResultListBinding.inflate(inflater, container, false)
        viewModel.getBooksByTitle()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bookListAdapter = BookListAdapter(BookListener {
            findNavController().navigate(
                SearchResultListFragmentDirections.actionSearchResultListFragmentToBookDetailsFragment(
                    it
                )
            )
        }
        )

        binding.lifecycleOwner = viewLifecycleOwner
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = bookListAdapter
        }
        initObservers()
    }

    private fun initObservers() {
        viewModel.bookListState.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Success -> {
                    handleLoading(false)
                    bookListAdapter.submitList(it.data)
                }

                is Result.Loading -> {
                    handleLoading(true)
                }

                is Result.Error -> {
                    handleLoading(false)
                }

                else -> Unit
            }
        }
    }

    private fun handleLoading(isLoading: Boolean) {
        with(binding) {
            if (isLoading) {
                progressBarResult.visibility = View.VISIBLE
            } else {
                progressBarResult.visibility = View.GONE
            }
        }
    }
}