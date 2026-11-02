package com.example.healthif

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.healthif.DAO.HeartBeatsDao
import com.example.healthif.repository.PreferenciasRepository
import com.example.healthif.repository.dataStore
import com.example.healthif.screen.HeartCareScreen
import com.example.healthif.ui.theme.DataStoreTheme
import com.example.healthif.viewmodel.PreferenciasViewModel
import com.example.healthif.repository.HeartBeatsRepository
import androidx.lifecycle.viewmodel.compose.viewModel
class MainActivity : ComponentActivity() {

    private val viewModel: PreferenciasViewModel by viewModels {
        viewModelFactory {
            initializer {
                PreferenciasViewModel(
                    PreferenciasRepository(applicationContext.dataStore),
                    (application as MeuApp).batimentosRepository
                )
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(applicationContext)

        val repo = PreferenciasRepository(
            applicationContext.dataStore
        )

        val repository = (application as MeuApp).batimentosRepository

        enableEdgeToEdge()
        setContent {
            HealthIFAPP()
        }
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun HealthIFAPP() {
        val escuro by viewModel.escuro.collectAsStateWithLifecycle()
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

        DataStoreTheme(darkTheme = escuro) {
            Scaffold(
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),

                topBar = {
                    CenterAlignedTopAppBar(
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.primary
                        ),
                        title = {
                            Text(
                                text = "HealthIF",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        actions = {
                            IconButton(onClick = {
                                viewModel.toggleEscuro(!escuro)
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Home,
                                    contentDescription = "trocar tema"
                                )
                            }
                        },
                        scrollBehavior = scrollBehavior,
                    )
                }
            ) { innerPadding ->
                HeartCareScreen(
                    viewModel = viewModel,
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
    }
}


