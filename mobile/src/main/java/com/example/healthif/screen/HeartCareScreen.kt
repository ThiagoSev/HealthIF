package com.example.healthif.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.healthif.ui.theme.DataStoreTheme
import com.example.healthif.viewmodel.PreferenciasViewModel
import com.developerstring.jetco.ui.charts.barchart.ColumnBarChart
import androidx.compose.ui.graphics.Color
import com.developerstring.jetco.ui.charts.barchart.config.BarChartDefaults
import com.example.healthif.Entities.HeartBeat

//TEMPORARIO
data class batimentos (val bpm: String, val hora: String)
val batimentosList = arrayListOf(
    batimentos("85", "11:48"),
    batimentos("89", "10:56"),
)

@Composable
fun HeartCareScreen(viewModel: PreferenciasViewModel, modifier: Modifier = Modifier) {
    val atual by viewModel.batimentoAtual
        .collectAsStateWithLifecycle()

    val historico by viewModel.historico
        .collectAsStateWithLifecycle()

    ComponentesInterface(atual, historico)
}

@Composable
fun HeartCareScreenFake() {
    val historico = listOf(
        HeartBeat(bpm = 70),
        HeartBeat(bpm = 80),
        HeartBeat(bpm = 90),
        HeartBeat(bpm = 85)
    )
    ComponentesInterface(HeartBeat(bpm = 70), historico)
}

@Composable
private fun ComponentesInterface(atual: HeartBeat?, historico: List<HeartBeat>) {

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 16.dp)
    ) {
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.85f),

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
                        text = if (atual != null)
                            atual.bpm.toString()
                            else "sem batimentos",
                        style = TextStyle(
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            BarChartExample()
        }
        items(historico) { itemHistorico ->
            RegistroBatimento(itemHistorico.bpm, itemHistorico.timestamp)
        }
    }
}

@Composable
fun RegistroBatimento(bpm: Int, hora: Long) {
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

@Composable
fun BarChartExample() {
    val data = mapOf(
        "Mon" to 120f,
        "Tue" to 85f,
        "Wed" to 200f,
        "Thu" to 150f,
        "Fri" to 170f
    )

    ColumnBarChart(
        chartData = data,
        barChartConfig = BarChartDefaults.columnBarChartConfig(
            color = Color(0xFF6C5CE7),
            height = 200.dp,
            width = 30.dp
        )
    )
}

@Preview(showBackground = true)
@Composable
fun HeartCareScreenPreview() {
    DataStoreTheme {
        HeartCareScreenFake()
    }
}
