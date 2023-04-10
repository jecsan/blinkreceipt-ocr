package com.blinkreceipt.ocr

import androidx.lifecycle.ViewModel
import com.blinkreceipt.ocr.scanresult.BrScanResults
import com.microblink.FrameCharacteristics
import com.microblink.ScanOptions
import com.microblink.core.ScanResults
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class MainViewModel : ViewModel() {

    private val confidenceThreshold = 90f

    private val _scanResults = MutableStateFlow<ScanUiState?>(null)
    val scanResults: StateFlow<ScanUiState?> = _scanResults


    private fun isPassableReceipt(scanResults: ScanResults): Boolean {
        return scanResults.ocrConfidence() > confidenceThreshold
    }

    fun setScanResults(brScanResults: BrScanResults) {

        if (isPassableReceipt(brScanResults.scanResults)) {
            _scanResults.value = ScanUiState.Success(brScanResults)
        } else {
            _scanResults.value = ScanUiState.Error("The receipt you provided is not valid")
        }

    }

    fun generateScanOptions(): ScanOptions {
        return ScanOptions.newBuilder()
            .frameCharacteristics(
                FrameCharacteristics.newBuilder()
                    .storeFrames(true)
                    .compressionQuality(100)
                    .externalStorage(false).build()
            )
            .logoDetection(true)
            .build()
    }


}