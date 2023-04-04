package com.blinkreceipt.ocr.scanresult

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.blinkreceipt.ocr.R


@Composable
fun ScanResultsScreen2( onRelaunchScanner: () -> Unit) {

    val scanResultsViewModel : ScanResultsViewModel = viewModel()
    val results by scanResultsViewModel.scanResults.collectAsState()

    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        LazyColumn(modifier = Modifier
            .fillMaxSize(0.9f), horizontalAlignment = CenterHorizontally, content = {

            item {

                Column {
                    Text(
                        text = stringResource(R.string.scan_results),
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(text = "Receipt ID: ${results?.blinkReceiptId()}")
                    Text(text = results?.merchantName()?.value() ?: "No merchant name")
                    Text(
                        text = "${results?.currencyCode() ?: ""}${
                            results?.total()?.value().toString()
                        }"
                    )
                }

            }

            results?.products()?.let {
                items(it) { product->
                    Text(text = product.productName()?:product.description()?.value()?:"No product name")
                }
            }
//            brScanResults.media.items()?.let {
//                items(it) { scanResult ->
//                    AsyncImage(model = scanResult, contentDescription = scanResult.name)
//                }
//            }



        })

        ElevatedButton(onClick = onRelaunchScanner) {
            Text(text = stringResource(R.string.scan_again))
        }
    }


}

