package com.example.healthif.ui.feature.heartbeats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.developerstring.jetco.ui.charts.barchart.ColumnBarChart
import com.developerstring.jetco.ui.charts.barchart.config.BarChartDefaults
import com.example.healthif.data.Entities.HeartBeat
import com.example.healthif.ui.theme.DataStoreTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import com.example.healthif.ui.feature.PreferenciasViewModel

private val registroFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy • HH:mm:ss", Locale.forLanguageTag("pt-BR"))
private val graficoDataFormatter = DateTimeFormatter.ofPattern("dd/MM/uuuu")

@Composable
fun AlertDialogEmergencia(){
    var showDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Button(onClick = {showDialog = true}){
            Text("Abrir Dialogo")
        }
    }

    if(showDialog){
        AlertDialog(
            onDismissRequest = {showDialog = false},

            title = {Text("Alerta!")},
            text = { Text("Batimentos cardíacos acima do recomendado!")},
            confirmButton = {
                TextButton(onClick = {showDialog = false}) {
                    Text("to bem 👍")
                }
            },

            dismissButton = {
                TextButton(onClick = {showDialog = false}) {
                    Text("não to bem 👎")
                }
            }
        )
    }
}

@Composable
fun HeartCareScreen(viewModel: PreferenciasViewModel, modifier: Modifier = Modifier) {
    val atual by viewModel.batimentoAtual.collectAsStateWithLifecycle()
    val historico by viewModel.historico.collectAsStateWithLifecycle()

    ComponentesInterface(atual, historico, modifier)
}

@Composable
fun HeartCareScreenFake() {
    val inicio = java.time.LocalDate.of(2026, 11, 8)
        .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val historico = listOf(70, 80, 90, 85, 75, 82, 78, 72, 88, 84, 76, 74).mapIndexed { index, bpm ->
        HeartBeat(id = index + 1, bpm = bpm, timestamp = inicio + index * 2 * 60 * 60_000L)
    }
    ComponentesInterface(historico.last(), historico)
}

@Composable
private fun ComponentesInterface(
    atual: HeartBeat?,
    historico: List<HeartBeat>,
    modifier: Modifier = Modifier
) {
    HeartCareTheme {
        Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
            ) {
                item {

                    //dialogo de emergência
                    if(atual != null && atual.bpm > 120){
                        AlertDialogEmergencia()
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "BPM",
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                style = MaterialTheme.typography.labelLarge
                            )
                            Text(
                                text = atual?.bpm?.toString() ?: "Sem batimentos",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = if (atual == null) 24.sp else 42.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                item {
                    BarChart(historico)
                }
                item {
                    Text(
                        text = "Histórico de batimentos",
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                itemsIndexed(historico) { index, itemHistorico ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        RegistroBatimento(itemHistorico.bpm, itemHistorico.timestamp)
                        if (index < historico.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(top = 16.dp),
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RegistroBatimento(bpm: Int, hora: Long) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = "$bpm bpm",
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = registroFormatter.format(Instant.ofEpochMilli(hora).atZone(ZoneId.systemDefault())),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun BarChart(historico: List<HeartBeat>, modifier: Modifier = Modifier) {
    val colors = MaterialTheme.colorScheme
    val zoneId = ZoneId.systemDefault()
    val medias = remember(historico, zoneId) {
        calcularMediasPorQuatroHoras(historico, zoneId)
    }
    Column(
        modifier = modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Média de batimentos a cada 4 horas",
            color = colors.onBackground,
            style = MaterialTheme.typography.titleMedium
        )
        if (historico.isEmpty()) {
            Text(
                text = "Nenhum batimento registrado. O gráfico aparecerá quando houver leituras.",
                color = colors.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            val data = medias.associate { media ->
                val hora = media.inicio.hour
                val faixa = String.format(Locale.ROOT, "%02d–%02dh", hora, hora + 4)
                "${media.inicio.format(graficoDataFormatter)}\n$faixa" to media.mediaBpm
            }
            val axisTextStyle = MaterialTheme.typography.labelSmall.copy(color = colors.onSurfaceVariant)
            ColumnBarChart(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                chartData = data,
                maxBarValue = (medias.maxOf { it.mediaBpm } * 1.1f).coerceAtLeast(1f),
                maxTextLengthXAxis = 24,
                barChartConfig = BarChartDefaults.columnBarChartConfig(
                    color = colors.primary,
                    height = 200.dp,
                    width = 40.dp
                ),
                xAxisConfig = BarChartDefaults.xAxisConfig(
                    axisLineColor = colors.outlineVariant,
                    textStyle = axisTextStyle
                ),
                yAxisConfig = BarChartDefaults.yAxisConfig(
                    axisLineColor = colors.outlineVariant,
                    textStyle = axisTextStyle
                ),
                gridLineStyle = BarChartDefaults.gridLineStyle(color = colors.outlineVariant),
                popUpConfig = BarChartDefaults.popUpConfig(
                    background = colors.primaryContainer,
                    textStyle = MaterialTheme.typography.labelMedium.copy(color = colors.onPrimaryContainer)
                )
            )
            Text(
                text = "Média de BPM por faixa de 4h • horário local • somente períodos com leituras",
                color = colors.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HeartCareScreenPreview() {
    DataStoreTheme(darkTheme = false) {
        HeartCareScreenFake()
    }
}

@Preview(showBackground = true)
@Composable
fun HeartCareScreenDarkPreview() {
    DataStoreTheme(darkTheme = true) {
        HeartCareScreenFake()
    }
}

@Preview(showBackground = true)
@Composable
fun HeartCareScreenEmptyPreview() {
    DataStoreTheme(darkTheme = false) {
        ComponentesInterface(null, emptyList())
    }
}
