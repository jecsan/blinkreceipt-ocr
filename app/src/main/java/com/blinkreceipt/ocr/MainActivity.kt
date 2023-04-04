package com.blinkreceipt.ocr

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.microblink.ScanOptions
import com.microblink.camera.ui.CameraScanActivity


/**
 *
 * Recognizer is actually required for ScanOptions but not specified in the SDK README https://github.com/BlinkReceipt/blinkreceipt-android
 *
 * Camera permission not automatically handled by the library
 */
class MainActivity : ComponentActivity() {

    private val mainViewModel: MainViewModel by viewModels()

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val brScanResults = result.extractScanResults()
                // Handle the result here
            } else if (result.resultCode == Activity.RESULT_CANCELED) {
                finish()
            }
        }

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                launchBrCamera()
            } else {
                // Show a screen explaining why the camera permission is required
            }
        }

    private fun launchBrCamera() {
        val scanOptions: ScanOptions = mainViewModel.generateScanOptions()
        val bundle = Bundle()
        bundle.putParcelable(CameraScanActivity.SCAN_OPTIONS_EXTRA, scanOptions)

        val intent = Intent(this, CameraScanActivity::class.java)
            .putExtra(CameraScanActivity.BUNDLE_EXTRA, bundle)

        cameraLauncher.launch(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        } else {
            launchBrCamera()
        }

    }
}

