package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.MainMenuScreen
import com.example.ui.screens.MakeOrderScreen
import com.example.ui.screens.OrderSummaryScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NovaDarkBg
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.MenuViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        NovaMenuApp()
      }
    }
  }
}

@Composable
fun NovaMenuApp(
    viewModel: MenuViewModel = viewModel()
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  // Handle hardware / gesture back button
  BackHandler(enabled = uiState.currentScreen != AppScreen.AUTH && uiState.currentScreen != AppScreen.MAIN_MENU) {
    viewModel.navigateTo(AppScreen.MAIN_MENU)
  }

  Scaffold(
      modifier = Modifier.fillMaxSize(),
      containerColor = NovaDarkBg
  ) { innerPadding ->
    val contentModifier = Modifier.padding(innerPadding)

    when (uiState.currentScreen) {
      AppScreen.AUTH -> {
        AuthScreen(
            uiState = uiState,
            viewModel = viewModel,
            modifier = contentModifier
        )
      }
      AppScreen.MAIN_MENU -> {
        MainMenuScreen(
            uiState = uiState,
            viewModel = viewModel,
            modifier = contentModifier
        )
      }
      AppScreen.ORDER_FORM -> {
        MakeOrderScreen(
            uiState = uiState,
            viewModel = viewModel,
            modifier = contentModifier
        )
      }
      AppScreen.ORDER_SUMMARY -> {
        OrderSummaryScreen(
            uiState = uiState,
            viewModel = viewModel,
            modifier = contentModifier
        )
      }
    }
  }
}

