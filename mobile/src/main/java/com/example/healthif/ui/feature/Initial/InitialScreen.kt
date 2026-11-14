package com.example.healthif.ui.feature.Initial

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.healthif.R
import com.example.healthif.ui.feature.PreferenciasViewModel
import com.example.healthif.ui.feature.heartbeats.HeartCareTheme

@Composable
fun InitialScreen(
    navigateToHeartCareScreen: () -> Unit
) {
    //val atual by viewModel.batimentoAtual.collectAsStateWithLifecycle()

    InitialContent(batimentos = "10", navigateToHeartCareScreen = navigateToHeartCareScreen)
}

@Composable
private fun InitialContent(
    batimentos: String? = null,
    calorias: String? = null,
    passos: String? = null,
    distancia: String? = null,
    navigateToHeartCareScreen: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MetricCard(stringResource(R.string.metric_calories), calorias, Modifier.weight(1f), {})
                MetricCard(stringResource(R.string.metric_heartbeats), batimentos, Modifier.weight(1f), navigateToHeartCareScreen)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MetricCard(stringResource(R.string.metric_steps), passos, Modifier.weight(1f), {})
                MetricCard(stringResource(R.string.metric_distance), distancia, Modifier.weight(1f), {})
            }
        }
    }
}

@Composable
private fun MetricCard(title: String, value: String?, modifier: Modifier = Modifier, onclickEvent: () -> Unit) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        onClick = onclickEvent,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 110.dp)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
        ) {
            Text(
                text = title,
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center
            )
            Text(
                text = value ?: stringResource(R.string.metric_unavailable),
                modifier = Modifier.fillMaxWidth(),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun InitialScreenPreview() {
    HeartCareTheme(darkTheme = false) {
        InitialContent(batimentos = "78", calorias = "320", passos = "4.250", distancia = "3,2 km", {})
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun InitialScreenDarkPreview() {
    HeartCareTheme(darkTheme = true) {
        InitialContent(navigateToHeartCareScreen = {})
    }
}
