package com.matiasmandelbaum.alejandriaapp.ui.booksreserved

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.google.firebase.auth.FirebaseUser
import com.matiasmandelbaum.alejandriaapp.R
import com.matiasmandelbaum.alejandriaapp.common.auth.AuthManager
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.databinding.FragmentBooksReservedBinding
import com.matiasmandelbaum.alejandriaapp.domain.model.reserve.Reserves
import com.matiasmandelbaum.alejandriaapp.ui.adapter.ReserveAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BooksReservedFragment : Fragment() {

    private lateinit var binding: FragmentBooksReservedBinding
    private lateinit var adapter: ReserveAdapter
    private val reserveList = ArrayList<Reserves>()

    private val viewModel: BooksReservedViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBooksReservedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            val layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            val snapHelper: SnapHelper = LinearSnapHelper()
            reservesRecyclerView.layoutManager = layoutManager
            reservesRecyclerView.setHasFixedSize(true)
            adapter = ReserveAdapter(reserveList)
            reservesRecyclerView.adapter = adapter

            snapHelper.attachToRecyclerView(reservesRecyclerView)
            initObservers()
        }
    }


    private fun initObservers() {
        AuthManager.authStateLiveData.observe(viewLifecycleOwner) { handleAuthState(it) }

        viewModel.reserveList.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Success -> {
                    handleLoading(false)
                    if (it.data.isNotEmpty()) {
                        reserveList.addAll(it.data)
                        reserveList.sort()
                        adapter.notifyDataSetChanged()
                    } else {
                        with(binding) {
                            reserveListEmpty.text = getString(R.string.no_reserved_books_message)
                            reserveListEmpty.visibility = View.VISIBLE
                        }
                    }
                }

                is Result.Loading -> {
                    handleLoading(true)
                }

                is Result.Error -> {

                }
            }
        }
    }

    private fun handleAuthState(user: FirebaseUser?) {
        with(binding) {
            user?.let {
                viewModel.getReservesForCurrentUser(user.email!!)
            } ?: run {
                reserveListEmpty.text = getString(R.string.not_logged_reserved_books_message)
                reserveListEmpty.visibility = View.VISIBLE
            }
        }

    }

    private fun handleLoading(isLoading: Boolean) {
        with(binding) {
            if (isLoading) {
                progressBarReserves.visibility = View.VISIBLE
                reservesContainer.visibility = View.GONE
            } else {
                progressBarReserves.visibility = View.GONE
                reservesContainer.visibility = View.VISIBLE
            }
        }
    }

}