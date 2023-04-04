package com.blinkreceipt.ocr

import androidx.lifecycle.ViewModel
import com.microblink.FrameCharacteristics
import com.microblink.ScanOptions

class MainViewModel : ViewModel() {

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