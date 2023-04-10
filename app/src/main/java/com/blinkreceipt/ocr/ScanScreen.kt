@file:OptIn(ExperimentalComposeUiApi::class)

package com.blinkreceipt.ocr

import android.graphics.RectF
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.blinkreceipt.ocr.scanresult.BrScanResults
import com.microblink.BitmapResult
import com.microblink.CameraCaptureListener
import com.microblink.CameraRecognizerCallback
import com.microblink.Media
import com.microblink.RecognizerResult
import com.microblink.RecognizerView
import com.microblink.ScanOptions
import com.microblink.core.ScanResults
import java.io.File


@Composable
fun ScanScreen(scanOptions: ScanOptions, onRecognizerDone: (BrScanResults) -> Unit) {

    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val recognizerObserver = remember {
        mutableStateOf<RecognizerObserver?>(null)
    }

    var captureFrame by remember {
        mutableStateOf(false)
    }

    var processing by remember {
        mutableStateOf(false)
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        AndroidView(modifier = Modifier.fillMaxSize(), factory = {

            val recognizerView = RecognizerView(it)

            recognizerView.recognizerCallback(object :
                CameraRecognizerCallback {
                override fun onRecognizerDone(p0: ScanResults, p1: Media) {
                    onRecognizerDone(BrScanResults(p0, p1))
                    Log.d("ScanScreen", "onRecognizerDone: $p0")
                    recognizerView.finishedScanning()
                }

                override fun onRecognizerException(p0: Throwable) {
                }

                override fun onRecognizerResultsChanged(p0: RecognizerResult) {

                }

                override fun onConfirmPicture(p0: File) {
                    Log.d("ScanScreen", "onConfirmPicture: $p0")
                }

                override fun onPermissionDenied() {
                }

                override fun onPreviewStarted() {
                }

                override fun onPreviewStopped() {
                }

                override fun onException(p0: Throwable) {
                }

            })



            val regionOfInterest = RectF(.05f, .10f, .95f, .90f)

            recognizerView.scanRegion(regionOfInterest)

            recognizerView.setMeteringAreas(
                arrayOf(
                    regionOfInterest
                ), true
            )

            recognizerObserver.value = RecognizerObserver(recognizerView)

            recognizerView.initialize(scanOptions)
            recognizerView.create()
            lifecycle.addObserver(recognizerObserver.value!!)

            recognizerView
        }, onReset = {
            recognizerObserver?.let {
                lifecycle.removeObserver(recognizerObserver.value!!)
            }
        }, update = {
            if (captureFrame) {
                processing = true
                it.takePicture(object : CameraCaptureListener {
                    override fun onCaptured(p0: BitmapResult) {
                        it.confirmPicture(p0)
                        it.finishedScanning()

                    }

                    override fun onException(p0: Throwable) {
                    }

                })
            }
        }, onRelease = {
            recognizerObserver.value?.let {
                lifecycle.removeObserver(it)
            }

        })

        if (processing)
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.Red
            )

        IconButton(modifier = Modifier.align(Alignment.BottomCenter), onClick = {
            captureFrame = true

        }) {
            Icon(
                imageVector = Icons.Default.AddCircle,
                contentDescription = "",
                modifier = Modifier.size(55.dp)
            )
        }

    }


}