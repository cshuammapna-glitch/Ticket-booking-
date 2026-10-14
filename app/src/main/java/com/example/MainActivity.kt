package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.outlined.ConfirmationNumber
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.BookingRequestEntity
import com.example.ui.screens.AdminAgentScreen
import com.example.ui.screens.BookingDetailScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NewBookingScreen
import com.example.ui.screens.SavedPassengersScreen
import com.example.ui.screens.TatkalGuideScreen
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NavyPrimary
import com.example.viewmodel.BookingViewModel

class MainActivity : ComponentActivity() {

  private val viewModel: BookingViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        MainAppRoot(viewModel = viewModel)
      }
    }
  }
}

enum class NavTab(val label: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector) {
  HOME("Home", Icons.Filled.Home, Icons.Outlined.Home),
  NEW_BOOKING("Book Ticket", Icons.Filled.AddCircle, Icons.Filled.AddCircle),
  ADMIN("Agent Desk", Icons.Filled.SupportAgent, Icons.Outlined.SupportAgent),
  SAVED("Passengers", Icons.Filled.People, Icons.Outlined.People),
  GUIDE("Tatkal FAQ", Icons.Filled.HelpOutline, Icons.Outlined.HelpOutline)
}

@Composable
fun MainAppRoot(viewModel: BookingViewModel) {
  var currentTab by remember { mutableStateOf(NavTab.HOME) }
  val selectedBooking by viewModel.selectedBooking.collectAsStateWithLifecycle()
  val allBookings by viewModel.allBookings.collectAsStateWithLifecycle()
  val pendingCount = allBookings.count { !it.isConfirmed }

  // Handle hardware Back button when detail is open
  BackHandler(enabled = selectedBooking != null || currentTab != NavTab.HOME) {
    if (selectedBooking != null) {
      viewModel.selectBooking(null)
    } else if (currentTab != NavTab.HOME) {
      currentTab = NavTab.HOME
    }
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    bottomBar = {
      // Hide bottom bar when viewing full screen digital e-ticket detail
      if (selectedBooking == null) {
        NavigationBar(
          containerColor = MaterialTheme.colorScheme.surface,
          tonalElevation = 6.dp,
          modifier = Modifier.testTag("main_navigation_bar")
        ) {
          NavTab.values().forEach { tab ->
            val selected = currentTab == tab
            NavigationBarItem(
              selected = selected,
              onClick = {
                viewModel.selectBooking(null)
                if (tab == NavTab.NEW_BOOKING) {
                  viewModel.resetWizard()
                }
                currentTab = tab
              },
              icon = {
                if (tab == NavTab.ADMIN && pendingCount > 0) {
                  BadgedBox(
                    badge = {
                      Badge(containerColor = AmberSecondary) {
                        Text(
                          text = "$pendingCount",
                          color = Color.White,
                          fontSize = 10.sp,
                          fontWeight = FontWeight.Bold
                        )
                      }
                    }
                  ) {
                    Icon(
                      imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                      contentDescription = tab.label
                    )
                  }
                } else {
                  Icon(
                    imageVector = if (selected) tab.selectedIcon else tab.unselectedIcon,
                    contentDescription = tab.label,
                    tint = if (tab == NavTab.NEW_BOOKING && selected) AmberSecondary else if (selected) NavyPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              },
              label = {
                Text(
                  text = tab.label,
                  fontSize = 10.sp,
                  fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                )
              },
              modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
            )
          }
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      if (selectedBooking != null) {
        BookingDetailScreen(
          booking = selectedBooking!!,
          viewModel = viewModel,
          onNavigateBack = { viewModel.selectBooking(null) }
        )
      } else {
        when (currentTab) {
          NavTab.HOME -> {
            HomeScreen(
              viewModel = viewModel,
              onNavigateToNewBooking = {
                currentTab = NavTab.NEW_BOOKING
              },
              onNavigateToBookingDetail = { booking ->
                viewModel.selectBooking(booking)
              },
              onNavigateToAdminDesk = {
                currentTab = NavTab.ADMIN
              },
              onNavigateToSavedPassengers = {
                currentTab = NavTab.SAVED
              },
              onNavigateToGuide = {
                currentTab = NavTab.GUIDE
              }
            )
          }

          NavTab.NEW_BOOKING -> {
            NewBookingScreen(
              viewModel = viewModel,
              onNavigateToDetail = { booking ->
                viewModel.selectBooking(booking)
              },
              onNavigateHome = {
                currentTab = NavTab.HOME
              }
            )
          }

          NavTab.ADMIN -> {
            AdminAgentScreen(
              viewModel = viewModel,
              onNavigateToDetail = { booking ->
                viewModel.selectBooking(booking)
              }
            )
          }

          NavTab.SAVED -> {
            SavedPassengersScreen(viewModel = viewModel)
          }

          NavTab.GUIDE -> {
            TatkalGuideScreen(viewModel = viewModel)
          }
        }
      }
    }
  }
}
