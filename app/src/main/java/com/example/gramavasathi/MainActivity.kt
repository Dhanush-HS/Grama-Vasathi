package com.example.gramavasathi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.example.gramavasathi.navigation.AppBottomBar
import com.example.gramavasathi.navigation.AppNavGraph
import com.example.gramavasathi.navigation.AuthNavigationGate
import com.example.gramavasathi.ui.theme.GramaVasathiTheme
import com.example.gramavasathi.viewmodel.AppFlowViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        setContent {
            GramaVasathiTheme {
                val navController = rememberNavController()
                AuthNavigationGate(navController) {
                    val appFlowVm = hiltViewModel<AppFlowViewModel>()
                    val appFlow by appFlowVm.appFlow.collectAsState()
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        bottomBar = {
                            AppBottomBar(navController = navController, appFlow = appFlow)
                        },
                        containerColor = MaterialTheme.colorScheme.background
                    ) { innerPadding ->
                        AppNavGraph(
                            navController = navController,
                            paddingValues = innerPadding
                        )
                    }
                }
            }
        }
    }
}

