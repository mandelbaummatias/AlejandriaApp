package com.matiasmandelbaum.alejandriaapp.ui.subscription

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.matiasmandelbaum.alejandriaapp.R
import com.matiasmandelbaum.alejandriaapp.databinding.FragmentSubscriptionListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubscriptionListFragment : Fragment() {

    private lateinit var binding: FragmentSubscriptionListBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSubscriptionListBinding.inflate(inflater, container, false)

        val basicPlanSubscribeButton = binding.root.findViewById<Button>(R.id.basicPlanSubscribeBtn)

        basicPlanSubscribeButton.setOnClickListener {
            Toast.makeText(context, "TO-DO: Integración con MP.", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }
}