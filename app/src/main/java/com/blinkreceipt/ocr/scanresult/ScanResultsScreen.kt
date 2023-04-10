package com.blinkreceipt.ocr.scanresult

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import coil.compose.AsyncImage
import com.blinkreceipt.ocr.R
import com.microblink.Media
import com.microblink.core.ScanResults


@Composable
fun ScanResultsScreen(scanResult: ScanResults, media:Media, onRelaunchScanner: () -> Unit) {

    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        LazyColumn(modifier = Modifier
            .fillMaxSize(0.9f), horizontalAlignment = CenterHorizontally, content = {

            item {
                Column {
                    Text(
                        text = stringResource(R.string.scan_results),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(text = "Receipt ID: ${scanResult.blinkReceiptId()}")
                    Text(text = scanResult.merchantName()?.value() ?: "No merchant name")
                    Text(
                        text = "${scanResult.currencyCode() ?: ""}${
                            scanResult.total()?.value().toString()
                        }"
                    )
                }

            }

            item {
                Text(
                    text = stringResource(R.string.products),
                    style = MaterialTheme.typography.titleMedium
                )
            }

            scanResult.products()?.let {
                items(it) { product ->
                    Text(
                        text = product.productName() ?: product.description()?.value()
                        ?: "No product name"
                    )
                }
            }

            media.items()?.let {
                items(it) { scanResult ->
                    AsyncImage(model = scanResult, contentDescription = scanResult.name)
                }
            }

        })

        ElevatedButton(onClick = onRelaunchScanner) {
            Text(text = stringResource(R.string.scan_again))
        }
    }


}

