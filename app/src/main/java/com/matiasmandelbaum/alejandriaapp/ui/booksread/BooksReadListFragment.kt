package com.matiasmandelbaum.alejandriaapp.ui.booksread

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
import com.matiasmandelbaum.alejandriaapp.R
import com.matiasmandelbaum.alejandriaapp.databinding.FragmentBooksReadListBinding
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "BooksReadListFragment"

@AndroidEntryPoint
class BooksReadListFragment : Fragment() {

    private lateinit var binding: FragmentBooksReadListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBooksReadListBinding.inflate(inflater, container, false)
        Log.d(TAG, "onCreateView")
        return binding.root
    }
}