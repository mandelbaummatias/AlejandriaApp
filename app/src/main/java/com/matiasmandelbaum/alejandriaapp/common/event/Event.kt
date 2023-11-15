package com.matiasmandelbaum.alejandriaapp.common.event

import android.util.Log


private const val TAG = "Event"

open class Event<out T>(private val content: T) {

    private var hasBeenHandled = false

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            Log.d(TAG, "hasBeenHandled devuelve null")
            null
        } else {
            Log.d(TAG, "hasBeenHandled true")
            hasBeenHandled = true
            content
        }
    }
}