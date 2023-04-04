@file:OptIn(ExperimentalComposeUiApi::class)

package com.blinkreceipt.ocr

import android.graphics.RectF
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.microblink.BitmapResult
import com.microblink.CameraCaptureListener
import com.microblink.CameraRecognizerCallback
import com.microblink.Media
import com.microblink.RecognizerCallback
import com.microblink.RecognizerResult
import com.microblink.RecognizerView
import com.microblink.ScanOptions
import com.microblink.core.ScanResults
import java.io.File


@Composable
fun ScanScreen(scanOptions: ScanOptions, onRecognizerDone: (ScanResults, Media) -> Unit) {

    val recognizerView = RecognizerView(LocalContext.current)


    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        AndroidView(modifier = Modifier.fillMaxSize(), factory = {
            recognizerView.initialize(scanOptions)

            recognizerView.recognizerCallback(object :
                CameraRecognizerCallback {
                override fun onRecognizerDone(p0: ScanResults, p1: Media) {
                    onRecognizerDone(p0, p1)
                }

                override fun onRecognizerException(p0: Throwable) {
                }

                override fun onRecognizerResultsChanged(p0: RecognizerResult) {
                }

                override fun onConfirmPicture(p0: File) {
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

            recognizerView.create()
            recognizerView.start()
            recognizerView.resume()
            recognizerView
        }, onReset = {})


        Row {
            TextButton(onClick = { /*TODO*/ }) {
                Text(text = "Cancel")
            }
            IconButton(onClick = {
                recognizerView.takePicture(object : CameraCaptureListener {
                    override fun onCaptured(p0: BitmapResult) {
                        recognizerView.confirmPicture(p0)
                    }

                    override fun onException(p0: Throwable) {
                    }

                })
            }) {
                Icon(imageVector = Icons.Default.AddCircle, contentDescription = "")
            }
            TextButton(onClick = { recognizerView.finishedScanning() }) {
                Text(text = "Finish")
            }
        }

    }


}