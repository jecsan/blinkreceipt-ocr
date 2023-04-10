package com.blinkreceipt.ocr

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.blinkreceipt.ocr.scanresult.BrScanResults

sealed class ScanUiState {
    data class Error(val errorMessage: String) : ScanUiState()
    data class Success(val brScanResults: BrScanResults) : ScanUiState()
}

@Composable
fun ErrorScreen(errorMessage: String, onRelaunchScanner: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Text(

            text = errorMessage,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(11.dp))
        ElevatedButton(

            onClick = onRelaunchScanner
        ) {
            Text(text = "Relaunch Scanner")
        }
    }
}