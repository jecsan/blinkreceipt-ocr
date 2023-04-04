package com.blinkreceipt.ocr

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.blinkreceipt.ocr.scanresult.ErrorScreen
import com.blinkreceipt.ocr.scanresult.ScanResultsScreen
import com.blinkreceipt.ocr.scanresult.ScanResultsScreen2
import com.blinkreceipt.ocr.scanresult.extractScanResults
import com.blinkreceipt.ocr.ui.theme.BlinkReceiptOCR2Theme
import com.microblink.ScanOptions
import com.microblink.camera.ui.CameraScanActivity


/**
 *
 * Recognizer is actually required for ScanOptions but not specified in the SDK README https://github.com/BlinkReceipt/blinkreceipt-android
 *
 * Camera permission not automatically handled by the library
 */
@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {


    private val mainViewModel: MainViewModel by viewModels()

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val brScanResults = result.extractScanResults()
                if (brScanResults != null) {
                    mainViewModel.setScanResults(brScanResults)
                }
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
                AlertDialog.Builder(this, R.style.AppTheme)
                    .setTitle(getString(R.string.camera_permission_required))
                    .setMessage(getString(R.string.this_app_requires_camera_permission_to_scan_receipts))
                    .setPositiveButton(getString(R.string.ok)) { dialog, _ ->
                        dialog.dismiss()
                        finish()
                    }
                    .show()
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

//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
//            != PackageManager.PERMISSION_GRANTED
//        ) {
//            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
//        } else {
//            launchBrCamera()
//        }

        setContent {

            BlinkReceiptOCR2Theme {

                var title by remember { mutableStateOf("Scan") }
                val navHostController = rememberNavController()

                Scaffold(topBar = { TopAppBar(title = { Text(text = title) }) }) {
                    val scanResult by mainViewModel.scanResults.collectAsState()

                    NavHost(navController = navHostController, startDestination = "scan" ){
                        composable("scan"){
                            ScanScreen(mainViewModel.generateScanOptions()){ result, media->

                                navHostController.currentBackStackEntry?.savedStateHandle?.set("scan_results", result)
                                navHostController.currentBackStackEntry?.savedStateHandle?.set("media", media)
                                navHostController.navigate("results")
                            }
                        }

                        composable("results"){


                            ScanResultsScreen2(
                                onRelaunchScanner = { launchBrCamera() })
//                            ScanResultsScreen()
                        }

                    }

                    Column(modifier = Modifier.padding(it)) {
                        if (scanResult != null) {
                            if (scanResult is ScanUiState.Success) {
                                title = "Results"
                                ScanResultsScreen(
                                    (scanResult as ScanUiState.Success).brScanResults,
                                    onRelaunchScanner = { launchBrCamera() })

                            } else {
                                title = "Error"
                                ErrorScreen(errorMessage = (scanResult as ScanUiState.Error).errorMessage) {
                                    launchBrCamera()
                                }
                            }
                        }
                    }


                }

            }

        }

    }
}

