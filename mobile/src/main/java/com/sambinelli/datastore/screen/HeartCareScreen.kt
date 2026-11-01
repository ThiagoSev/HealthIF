package com.sambinelli.datastore.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sambinelli.datastore.ui.theme.DataStoreTheme
import com.sambinelli.datastore.viewmodel.PreferenciasViewModel

//TEMPORARIO
data class batimentos (val bpm: String, val hora: String)
val batimentosList = arrayListOf(
    batimentos("85", "11:48"),
    batimentos("89", "10:56"),
)

@Composable
fun HeartCareScreen(viewModel: PreferenciasViewModel, modifier: Modifier = Modifier) {
    ComponentesInterface(viewModel, modifier)
}

@Composable
fun HeartCareScreenFake() {
    ComponentesInterface(null)
}

@Composable
private fun ComponentesInterface(
    viewModel: PreferenciasViewModel?,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    ,

                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "BPM",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    Text(
                        text = "60",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

        }
        items(batimentosList) { batimento ->
            RegistroBatimento(batimento.bpm, batimento.hora)
        }
    }
}

@Composable
fun RegistroBatimento(bpm: String, hora: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .padding(horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),

    ) {
        Text(
            text = "$bpm bpm",
            style = TextStyle(
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        )

        Text(
            text = "$hora",
            style = TextStyle(
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 8.sp,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HeartCareScreenPreview() {
    DataStoreTheme {
        HeartCareScreenFake()
    }
}
