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
import com.matiasmandelbaum.alejandriaapp.R
import com.matiasmandelbaum.alejandriaapp.common.auth.AuthManager
import com.matiasmandelbaum.alejandriaapp.common.dialogclicklistener.DialogClickListener
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.databinding.FragmentBooksDetailsBinding
import com.matiasmandelbaum.alejandriaapp.domain.model.subscription.Subscription
import com.matiasmandelbaum.alejandriaapp.ui.confirmbookreservation.ConfirmBookReservationDialogFragment
import com.matiasmandelbaum.alejandriaapp.ui.subscription.model.User
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

        viewModel.subscriptionState.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> handleSuccessResult(result.data)
                is Result.Error -> Log.d(TAG, "error en fetchSub ${result.message}")
                else -> Unit
            }
        }

        viewModel.user.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    handleUserSuccess(result.data)
                    handleLoading(false)

                }

                is Result.Error -> {
                    handleUserError(result.message)
                    handleLoading(false)
                }

                is Result.Finished -> Unit
                is Result.Loading -> handleLoading(true)
                else -> Unit
            }
        }


        viewModel.reservationState.observe(viewLifecycleOwner) { reservationState ->
            val isAuthorized = reservationState.isSubscriptionAuthorized
            val canReserve = reservationState.hasReservedBook

            binding.bookReserveBtn.isEnabled = isAuthorized && canReserve
        }

        viewModel.subscriptionExists.observe(viewLifecycleOwner){
            if(!it){
                Log.d(TAG, " subs is blank")
                handleBlankSubscriptionId()
            }
        }

        viewModel.isEnabledToReserve.observe(viewLifecycleOwner) {
            if (!it) {
              //  binding.bookReserveBtn.isEnabled = false
                binding.bookReserveBtn.visibility = View.INVISIBLE
            }
        }

        viewModel.dialogResult.observe(viewLifecycleOwner) { result ->
            if (result) {
                Log.d(TAG, "positive click")
            } else {
                Log.d(TAG, "negative click")
            }
        }

        viewModel.onSuccessfulReservation.observe(viewLifecycleOwner){
            when(it){
                is Result.Success -> {
                    Log.d(TAG, "mi ReservationResult $it")
                }

                is Result.Error -> {
                    Log.d(tag, "MI ERROR ${it.message}")
                }

                else -> {Unit}
            }
        }

    }

    private fun handleLoading(isLoading: Boolean) {
        with(binding) {
            if (isLoading) {
                bookReserveBtn.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
            } else {
                bookReserveBtn.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
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

    private fun handleSuccessResult(subscription: Subscription) {
        Log.d(TAG, "handleSuccessResult()")
       // Log.d(TAG, "mi book ${viewModel.book}")
        //Log.d("VIENDO CANTIDAD", "cant disponible ${viewModel.book.cantidadDisponible}")

        if (viewModel.book.cantidadDisponible <= 0) {
            handleNoBooksAvailable()
            return
        }

        when (subscription.status) {
            "pending" -> handlePendingStatus(subscription.id)
            "authorized" -> handleAuthorizedStatus()
            else -> handleOtherStatus()
        }
    }

    private fun handleNoBooksAvailable() {
        Log.d("VIENDO CANTIDAD", "cant disponible menor?? ${viewModel.book.cantidadDisponible}")
        binding.bookReserveBtn.visibility = View.INVISIBLE
        Toast.makeText(
            context,
            getString(R.string.no_hay_libros_disponibles_para_reservar),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun handlePendingStatus(subscriptionId: String) {
        Log.d(TAG, "subscription pending...")
        handleButtonClick(subscriptionId)
//        binding.bookReserveBtn.setOnClickListener {
//            val args = Bundle().apply {
//                putString("subscriptionId", subscriptionId)
//            }
//            val dialog = SubscriptionRequiredDialogFragment().apply {
//                arguments = args
//                setDialogClickListener(this@BooksDetailFragment)
//            }
//            dialog.show(childFragmentManager, "SubscriptionRequiredDialogFragment")
//        }
    }


    private fun handleButtonClick(subscriptionId: String? = null) {
        Log.d(TAG, "handling button click...")

        binding.bookReserveBtn.setOnClickListener {
            val dialog = SubscriptionRequiredDialogFragment().apply {
                if (subscriptionId != null) {
                    val args = Bundle().apply {
                        putString("subscriptionId", subscriptionId)
                    }
                    arguments = args
                }
                setDialogClickListener(this@BooksDetailFragment)
            }

            dialog.show(childFragmentManager, "SubscriptionRequiredDialogFragment")
        }
    }


    private fun handleAuthorizedStatus() {
        Log.d(TAG, "authorized from subscriptionExists")
        if (viewModel.isEnabledToReserve.value != false) {
            Log.d(TAG, "hay mas de 1")
            Log.d(TAG, "has not reserved book and is enabled ${viewModel.hasReservedBook.value}")
            binding.bookReserveBtn.setOnClickListener { //TODO: este también con handleButton
                val dialog = ConfirmBookReservationDialogFragment().apply {
                    setDialogClickListener(this@BooksDetailFragment)
                }
                dialog.show(childFragmentManager, "SubscriptionRequiredDialogFragment")
            }
        } else {
            Toast.makeText(
                context,
                "Ya se reservo este mes!",
                Toast.LENGTH_SHORT
            ).show()
           // binding.bookReserveBtn.visibility = View.GONE //ojo ver

        }

        binding.bookReserveBtn.isEnabled = true
    }

    private fun handleOtherStatus() {
        Log.d(TAG, "handleOtherStatus()")
        Log.d(TAG, "has reserved book and is NOT enabled ${viewModel.hasReservedBook.value}")
        binding.bookReserveBtn.isEnabled = false
    }

    private fun handleUserSuccess(user: User) {
//        if (user.subscriptionId.isBlank()){
//            handleBlankSubscriptionId() //ver de cambiarlo en VM
//        }


//
//        if (user.subscriptionId.isNotBlank()) {
//            handleNonBlankSubscriptionId(user.subscriptionId)
//        } else {
//
//        }
       // handleReservedBook(user.hasReservedBook)
    }

    private fun handleNonBlankSubscriptionId(subscriptionId: String) {
        Log.d(TAG, "is not blank $subscriptionId")
        viewModel.fetchSubscription(subscriptionId)
    }

    private fun handleBlankSubscriptionId() { //ver de cambiarlo en vm
        handleButtonClick()
//        binding.bookReserveBtn.setOnClickListener {
//            val dialog = SubscriptionRequiredDialogFragment()
//            dialog.show(childFragmentManager, "SubscriptionRequiredDialogFragment")
//        }
    }

    private fun handleReservedBook(hasReservedBook: Boolean?) {
        if (hasReservedBook != false) {
            Log.d(TAG, "usuario no habilitado para reserva $hasReservedBook")
            hasReservedBook?.let { viewModel.updateReservationState(!it) }
        } else {
            Log.d(TAG, "usuario HABILITADO para reserva $hasReservedBook")
            hasReservedBook.let { viewModel.updateReservationState(true) }
        }
    }

    private fun handleUserError(errorMessage: String) {
        Log.d(TAG, "error $errorMessage")
    }


    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
        viewModel.resetUser()
    }


    override fun onFinishClickDialog(clickValue: Boolean) {
        if (clickValue) {
            //viewModel.reserveBook(AuthManager.getCurrentUser()?.email!!)
            viewModel.reserveBook(AuthManager.getCurrentUser()?.email!!)
            showSuccessfulReservationMessage()
        }
    }

    private fun showSuccessfulReservationMessage() {
        Snackbar.make(
            requireView(),
            getString(R.string.reserva_exitosa),
            Snackbar.LENGTH_INDEFINITE
        )
            .show();
    }
}