package com.blinkreceipt.ocr

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.blinkreceipt.ocr.scanresult.ScanResultsScreen
import com.blinkreceipt.ocr.ui.theme.BlinkReceiptOCR2Theme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState


/**
 *
 * Recognizer is actually required for ScanOptions but not specified in the SDK README https://github.com/BlinkReceipt/blinkreceipt-android
 *
 * Camera permission not automatically handled by the library
 */
@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {


    private val mainViewModel: MainViewModel by viewModels()

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            BlinkReceiptOCR2Theme {

                val title by remember { mutableStateOf("Scan") }
                val navHostController = rememberNavController()
                val scanResults = mainViewModel.scanResults.collectAsStateWithLifecycle()

                val cameraPermissionState = rememberPermissionState(
                    Manifest.permission.CAMERA
                )

                LaunchedEffect(key1 = cameraPermissionState, block = {
                    cameraPermissionState.launchPermissionRequest()
                })
                val snackbarHostState = remember { SnackbarHostState() }



                LaunchedEffect(scanResults.value) {
                    if (mainViewModel.scanResults.value != null && mainViewModel.scanResults.value is ScanUiState.Success) {
                        navHostController.navigate(Routes.ScanResultScreen)
                    } else if (mainViewModel.scanResults.value is ScanUiState.Error) {
                        snackbarHostState.showSnackbar("Invalid receipt")
                    }
                }


                Scaffold(topBar = { TopAppBar(title = { Text(text = title) }) },
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) {

                    NavHost(
                        modifier = Modifier.padding(it),
                        navController = navHostController,
                        startDestination = Routes.ScanScreen
                    ) {
                        composable(Routes.ScanScreen) {

                            if (cameraPermissionState.status.isGranted) {
                                ScanScreen(mainViewModel.generateScanOptions()) { results ->
                                    mainViewModel.setScanResults(results)
                                }
                            } else {
                                ErrorScreen(errorMessage = "Camera permission required") {
                                    cameraPermissionState.launchPermissionRequest()
                                }
                            }

                        }

                        composable(Routes.ScanResultScreen) {

                            val results = (scanResults.value as ScanUiState.Success).brScanResults

                            ScanResultsScreen(
                                scanResult = results.scanResults,
                                media = results.media,
                                onRelaunchScanner = {
                                    navHostController.navigate(Routes.ScanScreen)
                                })
                        }


                    }

                }

            }

        }

    }
}

