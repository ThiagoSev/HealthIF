package com.sambinelli.datastore.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sambinelli.datastore.ui.theme.DataStoreTheme
import com.sambinelli.datastore.viewmodel.PreferenciasViewModel


@Composable
fun HeartCareScreen(viewModel: PreferenciasViewModel) {
    ComponentesInterface(viewModel)
}

@Composable
fun HeartCareScreenFake() {
    val escuro by remember { mutableStateOf(false) }

    ComponentesInterface(null)
}

@Composable
private fun ComponentesInterface(
    viewModel: PreferenciasViewModel?,

) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    )
    {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .safeDrawingPadding()
        ) {

        }
    }
}

@Preview(showBackground = true)
@Composable
fun HeartCareScreenPreview() {
    DataStoreTheme {
        HeartCareScreenFake()
    }
}
