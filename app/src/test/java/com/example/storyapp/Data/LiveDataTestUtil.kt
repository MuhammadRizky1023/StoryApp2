package com.example.storyapp

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


@VisibleForTesting(otherwise = VisibleForTesting.NONE)
fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    afterObserver: () -> Unit = {}
): T {
    var data: T? = null
    val launch = CountDownLatch(1)
    val inspection = object : Observer<T> {
        override fun onChanged(o: T?) {
            data = o
            launch.countDown()
            this@getOrAwaitValue.removeObserver(this)
        }
    }
    this.observeForever(inspection)
    try {
        afterObserver.invoke()
        if (!launch.await(time, timeUnit)) {
            throw TimeoutException("The LiveData value was never set by user.")
        }
    } finally {
        this.removeObserver(inspection)
    }
    @Suppress("UNCHECKED_CAST")
    return data as T
}
