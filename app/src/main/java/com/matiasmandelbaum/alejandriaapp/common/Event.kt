package com.matiasmandelbaum.alejandriaapp.common

import android.util.Log


private const val TAG = "Event"
open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set

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

    fun getContent(): T? {
        return content
    }


    fun peekContent(): T = content
}