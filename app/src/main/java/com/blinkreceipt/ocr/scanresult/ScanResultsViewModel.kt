package com.blinkreceipt.ocr.scanresult

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.microblink.core.ScanResults

class ScanResultsViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {

    val scanResults = savedStateHandle.getStateFlow<ScanResults?>("scan_results",null)
}