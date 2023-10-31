package com.matiasmandelbaum.alejandriaapp.ui.booksdetails

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.matiasmandelbaum.alejandriaapp.common.auth.AuthManager
import com.matiasmandelbaum.alejandriaapp.common.dialogclicklistener.DialogClickListener
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.databinding.FragmentBooksDetailsBinding
import com.matiasmandelbaum.alejandriaapp.ui.confirmbookreservation.ConfirmBookReservationDialogFragment
import com.matiasmandelbaum.alejandriaapp.ui.subscriptionrequired.SubscriptionRequiredDialogFragment
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "BooksDetailFragment"

@AndroidEntryPoint
class BooksDetailFragment : Fragment(), DialogClickListener {

    private lateinit var binding: FragmentBooksDetailsBinding
    private var isInitialized = false
    private val viewModel: BooksDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBooksDetailsBinding.inflate(inflater, container, false)
        viewModel.onCreate()
        val rating = viewModel.book.valoracion

        binding.bookIsbn.text = viewModel.book.titulo
        binding.bookTitle.text = viewModel.book.titulo
        binding.bookAuthor.text = viewModel.book.autor
        Picasso.get().load(viewModel.book.imageLinks?.smallThumbnail).into(binding.bookCover);
        binding.bookCalification.text = rating?.let {
            "Valoración: $it"
        }
        binding.bookSynopsis.text = viewModel.book.descripcion
        initObservers()

        return binding.root
    }


    private fun initObservers() {
        AuthManager.authStateLiveData.observe(viewLifecycleOwner) { user ->
            if (user != null) {
                Log.d(TAG, "user logged $user")
                val userUid = AuthManager.getCurrentUser()?.uid
                userUid?.let {
                    viewModel.getUserById(it)
                }
            } else {
                Log.d(TAG, "user not logged")
                binding.bookReserveBtn.isEnabled = false
            }
        }

        viewModel.subscriptionExists.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    if (viewModel.book.cantidadDisponible > 0) {
                        when (result.data.status) {
                            "pending" -> {
                                Log.d(TAG, "subscription pending...")
                                //si consiguió usuario al princi
                                binding.bookReserveBtn.setOnClickListener {

                                    val subscriptionId = result.data.id

                                    val args = Bundle()
                                    args.putString("subscriptionId", subscriptionId)

                                    val dialog = SubscriptionRequiredDialogFragment()


                                    dialog.arguments = args
                                    dialog.setDialogClickListener(this)
                                    dialog.show(
                                        childFragmentManager,
                                        "SubscriptionRequiredDialogFragment"
                                    )
                                }
                            }

                            "authorized" -> {
                                Log.d(TAG, "authorized from subscriptionExists")
                                if (viewModel.isEnabledToReserve.value != false) {
                                    //        if (viewModel.book.cantidadDisponible > 0) {
                                    Log.d(TAG, "hay mas de 1")
                                    binding.bookReserveBtn.setOnClickListener {
                                        val dialog = ConfirmBookReservationDialogFragment()
                                        dialog.setDialogClickListener(this)
                                        dialog.show(
                                            childFragmentManager,
                                            "SubscriptionRequiredDialogFragment"
                                        )
                                    }
                                } else {
                                    binding.bookReserveBtn.visibility = View.GONE
                                    Toast.makeText( //esto pasarlo antes del click
                                        context,
                                        "No hay libros disponibles para reservar.",
                                        Toast.LENGTH_SHORT
                                    ).show()


                                }
                                // }
                                Log.d(
                                    TAG,
                                    "has not reserved book and is enabled ${viewModel.hasReservedBook.value}"
                                )
                                binding.bookReserveBtn.isEnabled = true
                            }

                            else -> {
                                Log.d(
                                    TAG,
                                    "has reserved book and is NOT enabled ${viewModel.hasReservedBook.value}"
                                )
                                binding.bookReserveBtn.isEnabled = false
                            }
                        }

                    } else {
                        binding.bookReserveBtn.visibility = View.GONE
                        Toast.makeText( //esto pasarlo antes del click
                            context,
                            "No hay libros disponibles para reservar.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                is Result.Error -> {
                    Unit
                }

                else -> {
                    Unit
                }
            }
        }

        viewModel.user.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    if (result.data.subscriptionId.isNotBlank()) {
                        Log.d(TAG, "is not blank ${result.data.subscriptionId}")
                        viewModel.fetchSubscription(result.data.subscriptionId)


                    } else {
                        Log.d(TAG, "is blank ${result.data.subscriptionId}")
                        binding.bookReserveBtn.setOnClickListener {
                            val dialog = SubscriptionRequiredDialogFragment()
                            dialog.show(childFragmentManager, "SubscriptionRequiredDialogFragment")
                        }
                    }

                    if (result.data.hasReservedBook != false) {
                        Log.d(
                            TAG,
                            "usuario no habilitado para reserva ${result.data.hasReservedBook}"
                        )
                        result.data.hasReservedBook?.let { viewModel.updateReservationState(!it) }
                    } else {
                        Log.d(TAG, "usuario HABILITADO para reserva ${result.data.hasReservedBook}")
                        result.data.hasReservedBook.let { viewModel.updateReservationState(true) }
                    }
                }

                is Result.Error -> {
                    Log.d(TAG, "error ${result.message}")
                }

                is Result.Finished -> Unit
                Result.Loading -> Unit
                else -> {}
            }
        }


        viewModel.reservationState.observe(viewLifecycleOwner) { reservationState ->
            val isAuthorized = reservationState.isSubscriptionAuthorized
            val canReserve = reservationState.hasReservedBook

            binding.bookReserveBtn.isEnabled = isAuthorized && canReserve
        }

        viewModel.isEnabledToReserve.observe(viewLifecycleOwner) {
            if (!it) {
                binding.bookReserveBtn.isEnabled = false
            }
        }

        viewModel.dialogResult.observe(viewLifecycleOwner) { result ->
            if (result) {
                Log.d(TAG, "positive click")
            } else {
                Log.d(TAG, "negative click")
            }
        }

    }


    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume")
        if (!isInitialized) {
            Log.d(TAG, ("isInitialized false. Setting to true..."))
            isInitialized = true
        } else {
            Log.d(TAG, ("isInitialized true!"))
            val userUid = AuthManager.getCurrentUser()?.uid
            userUid?.let {
                viewModel.getUserById(userUid)
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
        viewModel.resetUser()
    }


    override fun onFinishClickDialog(clickValue: Boolean) {
        if (clickValue) {
            viewModel.reserveBook(AuthManager.getCurrentUser()?.email!!)
            Snackbar.make(
                requireView(),
                "Reserva de libro exitoso! Puede retirarla en la Av.Siempreviva 742, de 9:00 a 17:00, planta baja",
                Snackbar.LENGTH_INDEFINITE
            )
                .show();
        }
    }
}