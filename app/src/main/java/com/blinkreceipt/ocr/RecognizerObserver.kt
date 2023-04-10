package com.blinkreceipt.ocr

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.microblink.RecognizerView

class RecognizerObserver(private val recognizerView: RecognizerView) : LifecycleEventObserver {
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_CREATE -> {

            }

            Lifecycle.Event.ON_START -> recognizerView.start()
            Lifecycle.Event.ON_RESUME -> recognizerView.resume()
            Lifecycle.Event.ON_PAUSE -> recognizerView.pause()
            Lifecycle.Event.ON_STOP -> recognizerView.stop()
            Lifecycle.Event.ON_DESTROY -> recognizerView.destroy()
            else -> {}
        }
    }

}