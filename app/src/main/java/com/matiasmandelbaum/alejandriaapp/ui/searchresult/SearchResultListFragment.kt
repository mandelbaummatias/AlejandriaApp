package com.matiasmandelbaum.alejandriaapp.ui.searchresult

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.matiasmandelbaum.alejandriaapp.databinding.FragmentSearchResultListBinding
import com.matiasmandelbaum.alejandriaapp.common.Result
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "SearchResultListFragment"

@AndroidEntryPoint
class SearchResultListFragment : Fragment() {

    private lateinit var binding: FragmentSearchResultListBinding
    private val viewModel: SearchResultViewModel by viewModels()

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
        viewModel.getBooksByTitle()
       // viewModel.getBooksByTitle()

        viewModel.bookListState.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Success ->{
                    handleLoading(false)
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
                progressBar.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.GONE
            }
        }
    }
}