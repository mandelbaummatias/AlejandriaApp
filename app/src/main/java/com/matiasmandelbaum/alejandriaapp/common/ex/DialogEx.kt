package com.matiasmandelbaum.alejandriaapp.common.ex

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.matiasmandelbaum.alejandriaapp.common.dialog.DialogFragmentLauncher
fun DialogFragment.show(launcher: DialogFragmentLauncher, activity: FragmentActivity) {
    launcher.show(this, activity)
}