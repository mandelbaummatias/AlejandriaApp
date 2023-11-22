package com.matiasmandelbaum.alejandriaapp.ui.booksdetails

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil.load
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import com.matiasmandelbaum.alejandriaapp.R
import com.matiasmandelbaum.alejandriaapp.common.auth.AuthManager
import com.matiasmandelbaum.alejandriaapp.common.dialogclicklistener.DialogClickListener
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.data.util.dialog.DialogType
import com.matiasmandelbaum.alejandriaapp.data.util.dialog.factory.DefaultDialogFragmentFactory
import com.matiasmandelbaum.alejandriaapp.data.util.dialog.factory.DialogFragmentFactory
import com.matiasmandelbaum.alejandriaapp.data.util.subscriptionstatus.SubscriptionStatus
import com.matiasmandelbaum.alejandriaapp.databinding.FragmentBooksDetailsBinding
import com.matiasmandelbaum.alejandriaapp.domain.model.ReservationResult
import com.matiasmandelbaum.alejandriaapp.domain.model.subscription.Subscription
import com.matiasmandelbaum.alejandriaapp.ui.subscription.model.User
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "BooksDetailFragment"

@AndroidEntryPoint
class BooksDetailFragment : Fragment(), DialogClickListener {

    private lateinit var binding: FragmentBooksDetailsBinding
    private var isInitialized = false
    private val viewModel: BooksDetailViewModel by viewModels()
    private val dialogFragmentFactory: DialogFragmentFactory = DefaultDialogFragmentFactory()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBooksDetailsBinding.inflate(inflater, container, false)
        initView()

        return binding.root
    }

    private fun initView() = with(binding) {
        viewModel.book.apply {
            bookTitle.text = titulo
            bookAuthor.text = autor
            bookCover.load(imageLinks?.smallThumbnail)
            bookCalification.rating = viewModel.book.valoracion?.toFloat() ?: 0.0f
            bookSynopsis.text = descripcion
        }
        initObservers()
    }

    private fun initObservers() {
        with(viewModel) {
            AuthManager.authStateLiveData.observe(viewLifecycleOwner) { user ->
                handleAuthState(user)
            }

            subscriptionState.observe(viewLifecycleOwner) { result ->
                handleSubscriptionState(result)
            }

            user.observe(viewLifecycleOwner) { result ->
                if (result != null) {
                    handleUserState(result)
                }
            }

            subscriptionExists.observe(viewLifecycleOwner) {
                handleSubscriptionExists(it)
            }

            isEnabledToReserve.observe(viewLifecycleOwner) {
                handleReserveButtonVisibility(it)
            }

            onSuccessfulReservation.observe(viewLifecycleOwner) {
                handleReservationResult(it)
            }
        }
    }

    private fun handleAuthState(user: FirebaseUser?) {
        if (user != null) {
            Log.d(TAG, "user logged $user")
            user.uid.let {
                viewModel.getUserById(it)
            }
        } else {
            Log.d(TAG, "user not logged")
            binding.bookReserveButton.isEnabled = false
        }
    }

    private fun handleSubscriptionState(result: Result<Subscription>) {
        when (result) {
            is Result.Success -> handleSuccessResult(result.data)
            is Result.Error -> Log.d(TAG, "error en fetchSub ${result.message}")
            else -> Unit
        }
    }

    private fun handleUserState(result: Result<User>) {
        when (result) {
            is Result.Success -> {
                Log.d(TAG, "user success")
                handleLoading(false)
            }

            is Result.Error -> {
                Log.d(TAG, "user error")
                handleUserError(result.message)
                handleLoading(false)
            }

            is Result.Loading -> {
                Log.d(TAG, "user loading")
                handleLoading(true)
            }

            else -> Unit
        }
    }

    private fun handleSubscriptionExists(subscriptionExists: Boolean) {
        if (!subscriptionExists) {
            Log.d(TAG, " subs is blank")
            handleBlankSubscriptionId()
        }
    }

    private fun handleReserveButtonVisibility(isEnabled: Boolean) {
        binding.bookReserveButton.visibility = if (isEnabled) View.VISIBLE else View.INVISIBLE
    }

    private fun handleReservationResult(result: Result<ReservationResult>?) {
        when (result) {
            is Result.Success -> {
                Log.d(TAG, "mi ReservationResult $result")
                showSuccessfulReservationMessage()
            }

            is Result.Error -> {
                Log.d(tag, "MI ERROR ${result.message}")
                showErrorOnReservationMessage(result.message)
            }

            else -> Unit
        }
    }

    private fun handleLoading(isLoading: Boolean) {
        with(binding) {
            if (isLoading) {
                bookReserveButton.visibility = View.GONE
                progressBar.visibility = View.VISIBLE
            } else {
                bookReserveButton.visibility = View.VISIBLE
                progressBar.visibility = View.GONE
            }
        }
    }

    private fun handleSuccessResult(subscription: Subscription) {
        Log.d(TAG, "handleSuccessResult()")
        if (viewModel.book.cantidadDisponible <= 0) {
            handleNoBooksAvailable()
            return
        }
        when (subscription.status) {
            SubscriptionStatus.Pending.statusString -> handlePendingStatus(subscription.id)
            SubscriptionStatus.Authorized.statusString -> handleAuthorizedStatus()
            else -> handleOtherStatus()
        }
    }

    private fun handleNoBooksAvailable() {
        with(binding) {
            bookReserveButton.visibility = View.INVISIBLE
            Toast.makeText(
                requireContext(),
                getString(R.string.no_hay_libros_disponibles_para_reservar),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun handleAuthorizedStatus() {
        Log.d(TAG, "authorized from subscriptionExists")
        if (viewModel.isEnabledToReserve.value != false) {
            Log.d(TAG, "hay mas de 1")
            handleButtonClick(DialogType.RESERVATION)
        } else {
            Toast.makeText(
                context,
                getString(R.string.ya_se_reservo_este_mes),
                Toast.LENGTH_SHORT
            ).show()
        }
        binding.bookReserveButton.isEnabled = true
    }

    private fun handleBlankSubscriptionId() {
        Log.d(TAG, "handleBlankSubscriptionId()")
        handleButtonClick(DialogType.SUBSCRIPTION)
    }

    private fun handlePendingStatus(subscriptionId: String) {
        Log.d(TAG, "subscription pending...")
        // handleButtonClick(subscriptionId)
        handleButtonClick(DialogType.SUBSCRIPTION, subscriptionId)
    }

    private fun handleButtonClick(type: DialogType, subscriptionId: String? = null) {
        Log.d(TAG, "handling button click...")
        binding.bookReserveButton.setOnClickListener {
            val dialogProvider = dialogFragmentFactory.createDialogFragment(type, subscriptionId)
            dialogProvider.setDialogClickListener(this@BooksDetailFragment)
            val dialog = dialogProvider as DialogFragment
            dialog.show(
                childFragmentManager,
                type.tag
            )
        }
    }


    private fun handleOtherStatus() {
        Log.d(TAG, "handleOtherStatus()")
        binding.bookReserveButton.isEnabled = false
    }


    private fun handleUserError(errorMessage: String) {
        Log.d(TAG, "error $errorMessage")
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
        }
    }

    private fun showSuccessfulReservationMessage() {
        Snackbar.make(
            requireView(),
            getString(R.string.reserva_exitosa),
            Snackbar.LENGTH_LONG
        )
            .show()
    }

    private fun showErrorOnReservationMessage(errorMessage: String) {
        Snackbar.make(
            requireView(),
            errorMessage,
            Snackbar.LENGTH_LONG
        )
            .show()
    }
}