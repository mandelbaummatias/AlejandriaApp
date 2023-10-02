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
        viewModel.fetchBooksResult()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: SearchResultListFragmentArgs by navArgs()
        Log.d(TAG, args.titulo)
    }
}