package com.blinkreceipt.ocr.scanresult

import android.os.Build
import androidx.activity.result.ActivityResult
import com.microblink.Media
import com.microblink.camera.ui.CameraScanActivity
import com.microblink.core.ScanResults

data class BrScanResults(
    val scanResults: ScanResults,
    val media: Media
)


fun ActivityResult.extractScanResults(): BrScanResults? {
    @Suppress("DEPRECATION") val brScanResults: ScanResults? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this.data?.getParcelableExtra(
                CameraScanActivity.DATA_EXTRA,
                ScanResults::class.java
            )
        } else {
            this.data?.getParcelableExtra(CameraScanActivity.DATA_EXTRA)
        }

    val media: Media? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            this.data?.getParcelableExtra(
                CameraScanActivity.MEDIA_EXTRA,
                Media::class.java
            )
        } else {
            this.data?.getParcelableExtra(CameraScanActivity.MEDIA_EXTRA)
        }
    if (brScanResults == null || media == null) return null

    return BrScanResults(brScanResults, media)
}