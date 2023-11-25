package com.matiasmandelbaum.alejandriaapp.ui.userprofileimage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.matiasmandelbaum.alejandriaapp.R
import com.matiasmandelbaum.alejandriaapp.common.result.Result
import com.matiasmandelbaum.alejandriaapp.data.util.imageconstants.ImageConstants.ALEJANDRIA_LOGO
import com.matiasmandelbaum.alejandriaapp.data.util.imageconstants.ImageConstants.IC_BOOK
import com.matiasmandelbaum.alejandriaapp.data.util.imageconstants.ImageConstants.IC_FACE
import com.matiasmandelbaum.alejandriaapp.data.util.imageconstants.ImageConstants.IC_MENU_BOOK
import com.matiasmandelbaum.alejandriaapp.data.util.imageconstants.ImageConstants.IC_PROFILE
import com.matiasmandelbaum.alejandriaapp.data.util.imageconstants.ImageConstants.IC_TEMPLE
import com.matiasmandelbaum.alejandriaapp.databinding.UserProfilePictureChangeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserImageFragment : Fragment() {

    private lateinit var binding: UserProfilePictureChangeBinding
    private val viewModel: UserImageViewModel by viewModels()
    private var cambiandoImagen = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = UserProfilePictureChangeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
        initObservers()
    }

    private fun initObservers() {
        viewModel.changeResult.observe(viewLifecycleOwner) {
            when (it) {
                is Result.Success -> {
                    showImageUpdatedMessage()
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.userProfileFragment, true)
                        .build()
                    findNavController().navigate(R.id.userProfileFragment, null, navOptions)
                }

                is Result.Error -> {
                    showOnUpdatedErrorMessage()
                }

                else -> {
                    Unit
                }
            }
        }
    }

    private fun initListeners() {
        binding.apply {
            image1.setOnClickListener { onImageClick(ALEJANDRIA_LOGO) }
            image2.setOnClickListener { onImageClick(IC_PROFILE) }
            image3.setOnClickListener { onImageClick(IC_MENU_BOOK) }
            image4.setOnClickListener { onImageClick(IC_BOOK) }
            image5.setOnClickListener { onImageClick(IC_FACE) }
            image6.setOnClickListener { onImageClick(IC_TEMPLE) }
        }
    }

    private fun onImageClick(newImage: String) {
        if (!cambiandoImagen) {
            cambiandoImagen = true
            viewModel.changeImageForUser(newImage)
            viewModelStore
        }
    }

    private fun showImageUpdatedMessage() {
        Snackbar.make(
            requireView(),
            getString(R.string.imagen_de_perfil_actualizada), Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun showOnUpdatedErrorMessage() {
        Snackbar.make(
            requireView(),
            getString(R.string.error_en_cambio_de_imagen), Snackbar.LENGTH_SHORT
        ).show()
    }
}
