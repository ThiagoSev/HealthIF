package com.example.wearos.healthif.ui.health

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.ButtonDefaults.buttonColors
import androidx.wear.compose.material3.Card
import androidx.wear.compose.material3.CardDefaults.cardColors
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import com.example.wearos.R
import com.example.wearos.healthif.data.model.HealthReadings
import com.example.wearos.healthif.theme.DataStoreTheme

@Composable
fun HealthScreen(
    readings: HealthReadings,
    syncStatus: String,
    onSync: () -> Unit,
    onFindPhone: () -> Unit,
    onRefreshSteps: () -> Unit,
    onRefreshHeartRate: () -> Unit,
) {
    DataStoreTheme {
        AppScaffold {
            val listState = rememberTransformingLazyColumnState()
            val transformationSpec = rememberTransformationSpec()
            val cardColor = Color(0xFF1C1B1F)

            ScreenScaffold(
                scrollState = listState,
                edgeButton = {
                    EdgeButton(
                        onClick = onFindPhone,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        ),
                    ) {
                        Text("Encontrar\nTelefone")
                    }
                },
            ) { contentPadding ->
                TransformingLazyColumn(contentPadding = contentPadding, state = listState) {
                    item {
                        ListHeader(
                            modifier = Modifier
                                .fillMaxWidth()
                                .transformedHeight(this, transformationSpec),
                            transformation = SurfaceTransformation(transformationSpec),
                        ) {
                            Text(text = stringResource(R.string.app_name))
                        }
                    }

                    item {
                        Card(
                            onClick = {},
                            modifier = Modifier
                                .fillMaxWidth()
                                .transformedHeight(this, transformationSpec),
                            colors = cardColors(containerColor = cardColor),
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(10.dp)),
                                verticalArrangement = androidx.compose.foundation.layout.Arrangement
                                    .spacedBy(8.dp),
                            ) {
                                androidx.compose.foundation.layout.Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(IntrinsicSize.Min),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    ReadingBox(
                                        text = "Passos:\n${readings.stepsLabel}",
                                        modifier = Modifier.clickable(onClick = onRefreshSteps),
                                    )
                                    VerticalDivider(
                                        modifier = Modifier
                                            .padding(vertical = 1.dp)
                                            .fillMaxHeight(),
                                        thickness = 2.dp,
                                        color = MaterialTheme.colorScheme.outlineVariant,
                                    )
                                    ReadingBox(
                                        text = "Calorias:\n${readings.caloriesLabel}",
                                        modifier = Modifier.clickable(onClick = onRefreshSteps),
                                    )
                                }

                                HorizontalDivider(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(1.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant,
                                    thickness = 2.dp,
                                )

                                androidx.compose.foundation.layout.Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(IntrinsicSize.Min),
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    ReadingBox(
                                        text = "Batimentos:\n${readings.heartRateLabel}",
                                        modifier = Modifier.clickable(onClick = onRefreshHeartRate),
                                    )
                                    VerticalDivider(
                                        modifier = Modifier
                                            .padding(vertical = 1.dp)
                                            .fillMaxHeight(),
                                        thickness = 2.dp,
                                        color = MaterialTheme.colorScheme.outlineVariant,
                                    )
                                    ReadingBox(
                                        text = "Distância:\n${readings.distanceLabel}",
                                        modifier = Modifier.clickable(onClick = onRefreshSteps),
                                    )
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(5.dp)) }

                    item {
                        Button(
                            onClick = onSync,
                            modifier = Modifier
                                .transformedHeight(this, transformationSpec),
                            transformation = SurfaceTransformation(transformationSpec),
                            colors = buttonColors(Color(0xFFFF9800))

                        ) {
                            Text(
                                text = "Sincronizar",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    if (syncStatus.isNotBlank()) {
                        item {
                            Text(
                                text = syncStatus,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyExtraSmall,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.ReadingBox(
    text: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .defaultMinSize(minHeight = 25.dp)
            .weight(1f),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyExtraSmall,
            textAlign = TextAlign.Center,
        )
    }
}

@WearPreviewDevices
@WearPreviewFontScales
@Composable
private fun HealthScreenPreview() {
    HealthScreen(
        readings = HealthReadings(),
        syncStatus = "",
        onSync = {},
        onFindPhone = {},
        onRefreshSteps = {},
        onRefreshHeartRate = {},
    )
}
