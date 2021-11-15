package com.gojol.notto.util

import androidx.lifecycle.Observer

open class TouchEvent<out T>(private val content: T) {
    @Suppress("MemberVisibilityCanBePrivate")
    var hasBeenHandled = false
        private set

    fun getContentIfNotHandled(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }
}

class EventObserver<T>(private val onEventUnhandledContent: (T) -> Unit) : Observer<TouchEvent<T>> {
    override fun onChanged(touchEvent: TouchEvent<T>?) {
        touchEvent?.getContentIfNotHandled()?.let {
            onEventUnhandledContent(it)
        }
    }
}