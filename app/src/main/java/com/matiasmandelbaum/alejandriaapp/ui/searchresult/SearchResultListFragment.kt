package com.matiasmandelbaum.alejandriaapp.ui.searchresult

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.matiasmandelbaum.alejandriaapp.databinding.FragmentSearchResultListBinding
import com.matiasmandelbaum.alejandriaapp.ui.booklist.BookListAdapter
import com.matiasmandelbaum.alejandriaapp.ui.booklist.BookListener
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "SearchResultListFragment"

@AndroidEntryPoint
class SearchResultListFragment : Fragment() {

    private lateinit var binding: FragmentSearchResultListBinding
    private val viewModel: SearchResultViewModel by viewModels()
    private lateinit var bookListAdapter : BookListAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchResultListBinding.inflate(inflater, container, false)
        //viewModel.getBooksByTitle()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: SearchResultListFragmentArgs by navArgs()
        Log.d(TAG, args.titulo)
        bookListAdapter = BookListAdapter(BookListener {
            Log.d(TAG, "click")
            findNavController().navigate(
                SearchResultListFragmentDirections.actionSearchResultListFragmentToBookDetailsFragment(
                    it
                )
            )
        }
        )

        binding.lifecycleOwner = viewLifecycleOwner
        binding.recyclerView2.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = bookListAdapter
        }

        viewModel.getBooksByTitle()
        // viewModel.getBooksByTitle()

        viewModel.bookListState.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Success ->{
                    handleLoading(false)
                    bookListAdapter.submitList(it.data)
                    val className = it.data::class.java.simpleName
                    Log.d(TAG, " mi data ${it.data}")
                    Log.d(TAG, "The data type is: $className")
                }
                is Result.Loading -> handleLoading(true)
                is Result.Error -> handleLoading(false)
                else -> {

                }
            }
        }
    }

    private fun handleLoading(isLoading: Boolean) {
        with(binding) {
            if (isLoading) {
                progressBar2.visibility = View.VISIBLE
            } else {
                progressBar2.visibility = View.GONE
            }
        }
    }
}