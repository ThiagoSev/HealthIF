package com.sambinelli.datastore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import com.sambinelli.datastore.repository.PreferenciasRepository
import com.sambinelli.datastore.repository.dataStore
import com.sambinelli.datastore.screen.HeartCareScreen
import com.sambinelli.datastore.ui.theme.DataStoreTheme
import com.sambinelli.datastore.viewmodel.PreferenciasViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: PreferenciasViewModel by viewModels {
    viewModelFactory {
            initializer {
                //applicationContext é o contexto de toda a aplicação, não está vinculado ao ciclo de vida da Activity
                PreferenciasViewModel(PreferenciasRepository(applicationContext.dataStore))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HealthIFAPP()

        }
    }
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun HealthIFAPP(){
        val escuro by viewModel.escuro.collectAsStateWithLifecycle()
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        var modoEcuroOn: Boolean = false

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
                            viewModel?.toggleEscuro(modoEcuroOn)
                            modoEcuroOn = !modoEcuroOn
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
        ){
            it

            DataStoreTheme (darkTheme = escuro ) {
                HeartCareScreen(viewModel = viewModel)
            }
        }
    }
}


