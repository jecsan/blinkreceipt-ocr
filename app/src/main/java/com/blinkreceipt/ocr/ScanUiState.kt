package com.blinkreceipt.ocr

import com.blinkreceipt.ocr.scanresult.BrScanResults

sealed class ScanUiState {
    data class Error(val errorMessage: String) : ScanUiState()
    data class Success(val brScanResults: BrScanResults) : ScanUiState()
}