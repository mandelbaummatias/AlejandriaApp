package com.matiasmandelbaum.alejandriaapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.matiasmandelbaum.alejandriaapp.databinding.FragmentHomeListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeListFragment : Fragment() {
    private lateinit var binding: FragmentHomeListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentHomeListBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }
}