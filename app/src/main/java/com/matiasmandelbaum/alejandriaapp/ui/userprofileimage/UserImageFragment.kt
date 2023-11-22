package com.matiasmandelbaum.alejandriaapp.ui.userprofileimage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.matiasmandelbaum.alejandriaapp.R

class UserImageFragment : Fragment() {

    private lateinit var image1: ImageView
    private lateinit var image2: ImageView
    private lateinit var image3: ImageView
    private lateinit var image4: ImageView
    private lateinit var image5: ImageView
    private lateinit var image6: ImageView
    private var cambiandoImagen = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.user_profile_picture_change, container, false)

        image1 = view.findViewById(R.id.image1)
        image2 = view.findViewById(R.id.image2)
        image3 = view.findViewById(R.id.image3)
        image4 = view.findViewById(R.id.image4)
        image5 = view.findViewById(R.id.image5)
        image6 = view.findViewById(R.id.image6)

        image1.setOnClickListener {
            if (!cambiandoImagen) {
                cambiandoImagen = true
                changeProfilePicture("alejandria_logo")
            }
        }

        image2.setOnClickListener {
            if (!cambiandoImagen) {
                cambiandoImagen = true
                changeProfilePicture("ic_profile")
            }
        }

        image3.setOnClickListener {
            if (!cambiandoImagen) {
                cambiandoImagen = true
                changeProfilePicture("ic_menu_book")
            }
        }

        image4.setOnClickListener {
            if (!cambiandoImagen) {
                cambiandoImagen = true
                changeProfilePicture("ic_book")
            }
        }

        image5.setOnClickListener {
            if (!cambiandoImagen) {
                cambiandoImagen = true
                changeProfilePicture("ic_face")
            }
        }

        image6.setOnClickListener {
            if (!cambiandoImagen) {
                cambiandoImagen = true
                changeProfilePicture("ic_temple")
            }
        }

        return view
    }

    private fun changeProfilePicture(newImage: String) {
        val db = FirebaseFirestore.getInstance()
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {

            val userCollection = db.collection("users")
            userCollection.whereEqualTo("email", currentUser.email)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val userDocument = querySnapshot.documents[0].reference
                    userDocument.update("image", newImage)
                        .addOnSuccessListener {
                            showImageUpdatedMessage()
                            val navOptions = NavOptions.Builder()
                                .setPopUpTo(R.id.userProfileFragment, true)
                                .build()
                            findNavController().navigate(R.id.userProfileFragment, null, navOptions)
                        }
                }
        }
    }

    private fun showImageUpdatedMessage() {
        Snackbar.make(
            requireView(),
            getString(R.string.imagen_de_perfil_actualizada), Snackbar.LENGTH_SHORT
        ).show()
    }
}
