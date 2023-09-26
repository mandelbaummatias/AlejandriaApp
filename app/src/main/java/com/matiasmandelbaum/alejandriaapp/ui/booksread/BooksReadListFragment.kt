package com.matiasmandelbaum.alejandriaapp.ui.booksread

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.matiasmandelbaum.alejandriaapp.databinding.FragmentBooksReadListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BooksReadListFragment : Fragment() {

    private lateinit var binding: FragmentBooksReadListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBooksReadListBinding.inflate(inflater, container, false)

        return binding.root
    }
}