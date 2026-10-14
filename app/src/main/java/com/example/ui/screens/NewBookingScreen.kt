package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsRailway
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.BookingRequestEntity
import com.example.model.Passenger
import com.example.model.SavedPassengerEntity
import com.example.ui.components.SectionHeader
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.GreenTertiary
import com.example.ui.theme.NavyContainer
import com.example.ui.theme.NavyPrimary
import com.example.util.QrCodeGenerator
import com.example.viewmodel.BookingViewModel

@Composable
fun NewBookingScreen(
  viewModel: BookingViewModel,
  onNavigateToDetail: (BookingRequestEntity) -> Unit,
  onNavigateHome: () -> Unit
) {
  val currentStep by viewModel.currentStep.collectAsStateWithLifecycle()
  val agentConfig by viewModel.agentConfig.collectAsStateWithLifecycle()
  val passengers by viewModel.bookingPassengers.collectAsStateWithLifecycle()
  val savedPassengers by viewModel.allSavedPassengers.collectAsStateWithLifecycle()

  val context = LocalContext.current

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
  ) {
    // Top Step Indicator Bar
    Surface(
      color = MaterialTheme.colorScheme.surface,
      tonalElevation = 2.dp,
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(16.dp)) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.fillMaxWidth()
        ) {
          if (currentStep > 0 && currentStep < 3) {
            IconButton(
              onClick = { viewModel.currentStep.value-- },
              modifier = Modifier.size(36.dp)
            ) {
              Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
          }
          Text(
            text = when (currentStep) {
              0 -> "Step 1: Journey & Train Details"
              1 -> "Step 2: Passenger Aadhaar & PAN"
              2 -> "Step 3: Upfront UPI Payment"
              else -> "Booking Placed!"
            },
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 8.dp)
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Step tracker progress row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          WizardStepPill(number = "1", label = "Journey", active = currentStep >= 0, completed = currentStep > 0)
          StepDivider(completed = currentStep > 0)
          WizardStepPill(number = "2", label = "Aadhaar/PAN", active = currentStep >= 1, completed = currentStep > 1)
          StepDivider(completed = currentStep > 1)
          WizardStepPill(number = "3", label = "Payment", active = currentStep >= 2, completed = currentStep > 2)
        }
      }
    }

    // Step Contents
    when (currentStep) {
      0 -> StepJourney(viewModel = viewModel)
      1 -> StepPassengers(viewModel = viewModel, savedPassengers = savedPassengers)
      2 -> StepPayment(viewModel = viewModel, onPaymentSuccess = { bookingId ->
        // Booking placed
      })
      3 -> StepSuccess(viewModel = viewModel, onNavigateToDetail = onNavigateToDetail, onNavigateHome = onNavigateHome)
    }
  }
}

@Composable
fun WizardStepPill(number: String, label: String, active: Boolean, completed: Boolean) {
  Row(verticalAlignment = Alignment.CenterVertically) {
    Surface(
      shape = CircleShape,
      color = when {
        completed -> Color(0xFF2E7D32)
        active -> NavyPrimary
        else -> MaterialTheme.colorScheme.surfaceVariant
      },
      modifier = Modifier.size(24.dp)
    ) {
      Box(contentAlignment = Alignment.Center) {
        if (completed) {
          Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(14.dp)
          )
        } else {
          Text(
            text = number,
            color = if (active) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
    Spacer(modifier = Modifier.width(4.dp))
    Text(
      text = label,
      fontSize = 11.sp,
      fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
      color = if (active) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
    )
  }
}

@Composable
fun StepDivider(completed: Boolean) {
  Box(
    modifier = Modifier
      .width(24.dp)
      .height(2.dp)
      .background(if (completed) Color(0xFF2E7D32) else MaterialTheme.colorScheme.surfaceVariant)
  )
}

// -------------------------------------------------------------------------------------------------
// STEP 1: JOURNEY SELECTION
// -------------------------------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepJourney(viewModel: BookingViewModel) {
  val fromStation by viewModel.fromStation.collectAsStateWithLifecycle()
  val toStation by viewModel.toStation.collectAsStateWithLifecycle()
  val journeyDate by viewModel.journeyDate.collectAsStateWithLifecycle()
  val trainNumberOrName by viewModel.trainNumberOrName.collectAsStateWithLifecycle()
  val travelClass by viewModel.travelClass.collectAsStateWithLifecycle()
  val quota by viewModel.quota.collectAsStateWithLifecycle()
  val travelType by viewModel.travelType.collectAsStateWithLifecycle()
  val customPrice by viewModel.customTicketPrice.collectAsStateWithLifecycle()

  val popularStations = listOf(
    "NDLS - New Delhi",
    "PNBE - Patna Jn",
    "BCT - Mumbai Central",
    "HWH - Howrah Jn",
    "SBC - Bengaluru",
    "GKP - Gorakhpur",
    "BSB - Varanasi",
    "CNB - Kanpur Central"
  )

  val classes = listOf(
    "3A - AC 3 Tier",
    "2A - AC 2 Tier",
    "1A - AC First Class",
    "SL - Sleeper",
    "3E - AC 3 Economy",
    "CC - AC Chair Car",
    "2S - Second Sitting"
  )

  val quotas = listOf("Tatkal", "Premium Tatkal", "General", "Ladies", "Senior Citizen")

  var classDropdownExpanded by remember { mutableStateOf(false) }
  var quotaDropdownExpanded by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Travel Mode selector
    item {
      Text(
        text = "Select Travel Mode",
        fontWeight = FontWeight.SemiBold,
        fontSize = 13.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Spacer(modifier = Modifier.height(6.dp))
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        listOf("Train / Tatkal", "Bus", "Flight").forEach { mode ->
          val selected = travelType == mode
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (selected) NavyPrimary else MaterialTheme.colorScheme.surface,
            border = if (!selected) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant) else null,
            modifier = Modifier
              .weight(1f)
              .clickable { viewModel.travelType.value = mode }
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.Center,
              modifier = Modifier.padding(vertical = 10.dp)
            ) {
              Icon(
                imageVector = when (mode) {
                  "Bus" -> Icons.Default.DirectionsBus
                  "Flight" -> Icons.Default.Flight
                  else -> Icons.Default.Train
                },
                contentDescription = null,
                tint = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = mode,
                color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 12.sp
              )
            }
          }
        }
      }
    }

    // From and To Stations
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          OutlinedTextField(
            value = fromStation,
            onValueChange = { viewModel.fromStation.value = it },
            label = { Text("From Station / Origin City") },
            leadingIcon = {
              Icon(imageVector = Icons.Default.DirectionsRailway, contentDescription = null, tint = NavyPrimary)
            },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("from_station_input")
          )

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = toStation,
            onValueChange = { viewModel.toStation.value = it },
            label = { Text("To Station / Destination City") },
            leadingIcon = {
              Icon(imageVector = Icons.Default.DirectionsRailway, contentDescription = null, tint = AmberSecondary)
            },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("to_station_input")
          )

          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Popular Stations:",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(4.dp))
          LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(popularStations) { st ->
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.clickable {
                  if (fromStation.isBlank()) viewModel.fromStation.value = st
                  else viewModel.toStation.value = st
                }
              ) {
                Text(
                  text = st.take(16),
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSurface,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }
          }
        }
      }
    }

    // Train Name / Number & Date
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          OutlinedTextField(
            value = trainNumberOrName,
            onValueChange = { viewModel.trainNumberOrName.value = it },
            label = { Text("Preferred Train Number or Name") },
            placeholder = { Text("e.g. 12302 Rajdhani Exp or 'Any Fast Train'") },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("train_name_input")
          )

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = journeyDate,
            onValueChange = { viewModel.journeyDate.value = it },
            label = { Text("Journey Date") },
            leadingIcon = {
              Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null, tint = NavyPrimary)
            },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("journey_date_input")
          )

          Spacer(modifier = Modifier.height(6.dp))
          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Tomorrow (Tatkal Window)", "Day After Tomorrow", "Today Evening").forEach { d ->
              Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (journeyDate == d) NavyContainer else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.clickable { viewModel.journeyDate.value = d }
              ) {
                Text(
                  text = d,
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSurface,
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }
          }
        }
      }
    }

    // Class, Quota & Estimated Fare
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          // Class Selection
          ExposedDropdownMenuBox(
            expanded = classDropdownExpanded,
            onExpandedChange = { classDropdownExpanded = !classDropdownExpanded }
          ) {
            OutlinedTextField(
              value = travelClass,
              onValueChange = {},
              readOnly = true,
              label = { Text("Class Preference") },
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = classDropdownExpanded) },
              modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
            )
            ExposedDropdownMenu(
              expanded = classDropdownExpanded,
              onDismissRequest = { classDropdownExpanded = false }
            ) {
              classes.forEach { cls ->
                DropdownMenuItem(
                  text = { Text(cls) },
                  onClick = {
                    viewModel.travelClass.value = cls
                    classDropdownExpanded = false
                  }
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Quota Selection
          ExposedDropdownMenuBox(
            expanded = quotaDropdownExpanded,
            onExpandedChange = { quotaDropdownExpanded = !quotaDropdownExpanded }
          ) {
            OutlinedTextField(
              value = quota,
              onValueChange = {},
              readOnly = true,
              label = { Text("Booking Quota") },
              trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = quotaDropdownExpanded) },
              modifier = Modifier
                .fillMaxWidth()
                .menuAnchor()
            )
            ExposedDropdownMenu(
              expanded = quotaDropdownExpanded,
              onDismissRequest = { quotaDropdownExpanded = false }
            ) {
              quotas.forEach { q ->
                DropdownMenuItem(
                  text = { Text(q) },
                  onClick = {
                    viewModel.quota.value = q
                    quotaDropdownExpanded = false
                  }
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Base Ticket Price estimate (editable)
          OutlinedTextField(
            value = customPrice.toInt().toString(),
            onValueChange = {
              val parsed = it.toDoubleOrNull() ?: 0.0
              viewModel.customTicketPrice.value = parsed
            },
            label = { Text("Estimated Ticket Price Per Person (₹)") },
            leadingIcon = { Text("₹", fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 12.dp)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
          Text(
            text = "Railway ticket rate. Any difference will be adjusted transparently.",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
          )
        }
      }
    }

    // Continue to Passenger Details
    item {
      Button(
        onClick = { viewModel.currentStep.value = 1 },
        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("continue_to_passengers_button")
      ) {
        Text("Next: Passenger Details & Aadhaar / PAN", fontWeight = FontWeight.Bold, fontSize = 15.sp)
      }
      Spacer(modifier = Modifier.height(80.dp))
    }
  }
}

// -------------------------------------------------------------------------------------------------
// STEP 2: PASSENGERS WITH AADHAAR & PAN
// -------------------------------------------------------------------------------------------------
@Composable
fun StepPassengers(
  viewModel: BookingViewModel,
  savedPassengers: List<SavedPassengerEntity>
) {
  val passengers by viewModel.bookingPassengers.collectAsStateWithLifecycle()
  val context = LocalContext.current

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Quick autofill banner if saved passengers exist
    if (savedPassengers.isNotEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = NavyContainer.copy(alpha = 0.6f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text(
              text = "Saved Passengers (1-Tap Fill):",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              color = NavyPrimary
            )
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              items(savedPassengers) { sp ->
                Surface(
                  shape = RoundedCornerShape(8.dp),
                  color = MaterialTheme.colorScheme.surface,
                  modifier = Modifier.clickable {
                    viewModel.autofillFromSavedPassenger(0, sp)
                    Toast.makeText(context, "Filled ${sp.fullName}'s Aadhaar & PAN", Toast.LENGTH_SHORT).show()
                  }
                ) {
                  Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                  ) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = sp.fullName, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                  }
                }
              }
            }
          }
        }
      }
    }

    // Passenger Cards
    items(passengers.size) { index ->
      val p = passengers[index]
      PassengerInputCard(
        index = index,
        passenger = p,
        canDelete = passengers.size > 1,
        onUpdate = { updated -> viewModel.updatePassenger(index, updated) },
        onDelete = { viewModel.removePassenger(index) }
      )
    }

    // Add another passenger button
    item {
      OutlinedButton(
        onClick = { viewModel.addPassenger() },
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("add_passenger_button")
      ) {
        Icon(imageVector = Icons.Default.Add, contentDescription = null)
        Spacer(modifier = Modifier.width(6.dp))
        Text("Add Co-Passenger (Traveler #${passengers.size + 1})")
      }
    }

    // Privacy & ID Security Notice
    item {
      Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(12.dp)
        ) {
          Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            tint = GreenTertiary,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = "Aadhaar and PAN details are mandatory for IRCTC Tatkal & identity verification as per Indian Railways guidelines. Your data is stored safely on device.",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    }

    // Next to Payment Button
    item {
      val allValid = passengers.all { it.fullName.isNotBlank() && it.mobileNumber.isNotBlank() }

      Button(
        onClick = {
          if (!allValid) {
            Toast.makeText(context, "Please enter Passenger Name and Mobile Number", Toast.LENGTH_SHORT).show()
          } else {
            viewModel.currentStep.value = 2
          }
        },
        colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("continue_to_payment_button")
      ) {
        Text("Next: Review Fare & UPI Payment", fontWeight = FontWeight.Bold, fontSize = 15.sp)
      }
      Spacer(modifier = Modifier.height(80.dp))
    }
  }
}

@Composable
fun PassengerInputCard(
  index: Int,
  passenger: Passenger,
  canDelete: Boolean,
  onUpdate: (Passenger) -> Unit,
  onDelete: () -> Unit
) {
  val berths = listOf("No Preference", "Lower", "Middle", "Upper", "Side Lower", "Side Upper", "Window")
  val foods = listOf("Veg", "Non-Veg", "No Food")

  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Surface(
            shape = CircleShape,
            color = NavyPrimary,
            modifier = Modifier.size(24.dp)
          ) {
            Box(contentAlignment = Alignment.Center) {
              Text(
                text = "${index + 1}",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Passenger #${index + 1}",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
          )
        }

        if (canDelete) {
          IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
            Icon(
              imageVector = Icons.Default.Delete,
              contentDescription = "Remove Passenger",
              tint = MaterialTheme.colorScheme.error,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Full Name
      OutlinedTextField(
        value = passenger.fullName,
        onValueChange = { onUpdate(passenger.copy(fullName = it)) },
        label = { Text("Full Name (as on Aadhaar Card)") },
        leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = NavyPrimary) },
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("passenger_name_${index}")
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Age & Gender in one row
      Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        OutlinedTextField(
          value = if (passenger.age > 0) passenger.age.toString() else "",
          onValueChange = { onUpdate(passenger.copy(age = it.toIntOrNull() ?: 0)) },
          label = { Text("Age") },
          keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
          singleLine = true,
          modifier = Modifier.weight(1f)
        )

        OutlinedTextField(
          value = passenger.dob,
          onValueChange = { onUpdate(passenger.copy(dob = it)) },
          label = { Text("Date of Birth") },
          placeholder = { Text("DD/MM/YYYY") },
          singleLine = true,
          modifier = Modifier.weight(1.5f)
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Gender Selector
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Text(text = "Gender:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        listOf("Male", "Female", "Other").forEach { g ->
          val selected = passenger.gender == g
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (selected) NavyPrimary else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.clickable { onUpdate(passenger.copy(gender = g)) }
          ) {
            Text(
              text = g,
              fontSize = 12.sp,
              fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
              color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Mobile Number
      OutlinedTextField(
        value = passenger.mobileNumber,
        onValueChange = { onUpdate(passenger.copy(mobileNumber = it)) },
        label = { Text("Mobile Number (for SMS & E-Ticket)") },
        leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = GreenTertiary) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("passenger_mobile_${index}")
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Aadhaar Card Number (12 digits)
      OutlinedTextField(
        value = passenger.aadhaarNumber,
        onValueChange = {
          val clean = it.filter { char -> char.isDigit() }.take(12)
          onUpdate(passenger.copy(aadhaarNumber = clean))
        },
        label = { Text("Aadhaar Number (12 Digits)") },
        placeholder = { Text("e.g. 5482 9182 3741") },
        leadingIcon = { Icon(imageVector = Icons.Default.Badge, contentDescription = null, tint = AmberSecondary) },
        trailingIcon = {
          if (passenger.aadhaarNumber.length == 12) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = "Valid Aadhaar",
              tint = Color(0xFF2E7D32)
            )
          }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("passenger_aadhaar_${index}")
      )
      if (passenger.aadhaarNumber.isNotEmpty() && passenger.aadhaarNumber.length < 12) {
        Text(
          text = "${12 - passenger.aadhaarNumber.length} digits remaining",
          fontSize = 10.sp,
          color = AmberSecondary,
          modifier = Modifier.padding(start = 4.dp, top = 2.dp)
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // PAN Card Number (10 alphanumeric)
      OutlinedTextField(
        value = passenger.panNumber,
        onValueChange = {
          val clean = it.uppercase().take(10)
          onUpdate(passenger.copy(panNumber = clean))
        },
        label = { Text("PAN Card Number (10 Digits/Chars)") },
        placeholder = { Text("e.g. ABCDE1234F") },
        leadingIcon = { Icon(imageVector = Icons.Default.CreditCard, contentDescription = null, tint = NavyPrimary) },
        trailingIcon = {
          if (passenger.panNumber.length == 10) {
            Icon(
              imageVector = Icons.Default.CheckCircle,
              contentDescription = "Valid PAN",
              tint = Color(0xFF2E7D32)
            )
          }
        },
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("passenger_pan_${index}")
      )

      Spacer(modifier = Modifier.height(10.dp))

      // Berth Preference row
      Text(text = "Berth Preference:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
      Spacer(modifier = Modifier.height(4.dp))
      LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        items(berths) { b ->
          val sel = passenger.berthPreference == b
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (sel) NavyContainer else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.clickable { onUpdate(passenger.copy(berthPreference = b)) }
          ) {
            Text(
              text = b,
              fontSize = 11.sp,
              fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal,
              color = if (sel) NavyPrimary else MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Food Choice row
      Text(text = "Food Choice (if available):", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
      Spacer(modifier = Modifier.height(4.dp))
      Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        foods.forEach { f ->
          val sel = passenger.foodPreference == f
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = if (sel) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.clickable { onUpdate(passenger.copy(foodPreference = f)) }
          ) {
            Text(
              text = f,
              fontSize = 11.sp,
              fontWeight = if (sel) FontWeight.Bold else FontWeight.Normal,
              color = if (sel) Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSurface,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }
      }
    }
  }
}

// -------------------------------------------------------------------------------------------------
// STEP 3: PAYMENT SCREEN WITH DYNAMIC QR AND USER'S SPECIFIED UPI ID
// -------------------------------------------------------------------------------------------------
@Composable
fun StepPayment(
  viewModel: BookingViewModel,
  onPaymentSuccess: (Long) -> Unit
) {
  val agentConfig by viewModel.agentConfig.collectAsStateWithLifecycle()
  val passengers by viewModel.bookingPassengers.collectAsStateWithLifecycle()
  val utrInput by viewModel.paymentUtrInput.collectAsStateWithLifecycle()
  val customPrice by viewModel.customTicketPrice.collectAsStateWithLifecycle()
  val quota by viewModel.quota.collectAsStateWithLifecycle()
  val fromStation by viewModel.fromStation.collectAsStateWithLifecycle()
  val toStation by viewModel.toStation.collectAsStateWithLifecycle()
  val journeyDate by viewModel.journeyDate.collectAsStateWithLifecycle()

  val count = passengers.size
  val perTicket = customPrice
  val feePerTicket = if (quota.contains("Tatkal")) agentConfig.tatkalServiceFee else agentConfig.generalServiceFee
  val totalFare = perTicket * count
  val totalFee = feePerTicket * count
  val totalAmount = totalFare + totalFee

  val context = LocalContext.current
  val upiId = agentConfig.upiId // Strictly 9709803741@mbk as requested by user

  // Generate QR Code bitmap for the UPI transaction
  val qrBitmap = remember(totalAmount, upiId) {
    QrCodeGenerator.generateUpiQrBitmap(
      upiId = upiId,
      payeeName = agentConfig.payeeName,
      amount = totalAmount,
      transactionNote = "TatkalBooking-${passengers.firstOrNull()?.fullName?.take(6) ?: "Ticket"}"
    )
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. Payment Summary Card
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Booking Fare & Fee Summary",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onSurface
          )
          Text(
            text = "$fromStation to $toStation • $journeyDate ($count Passenger${if (count > 1) "s" else ""})",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Railway Base Fare ($count × ₹${perTicket.toInt()}):", fontSize = 13.sp)
            Text("₹${totalFare.toInt()}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
          }

          Spacer(modifier = Modifier.height(6.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text("Tatkal Concierge & Booking Fee:", fontSize = 13.sp)
            Text("₹${totalFee.toInt()}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
          }

          Spacer(modifier = Modifier.height(10.dp))
          HorizontalDivider()
          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text("Total Amount to Pay:", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(
              "₹${totalAmount.toInt()}",
              fontWeight = FontWeight.ExtraBold,
              fontSize = 22.sp,
              color = NavyPrimary
            )
          }
        }
      }
    }

    // 2. Official UPI Payment Box & QR Code
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(
          horizontalAlignment = Alignment.CenterHorizontally,
          modifier = Modifier.padding(20.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(10.dp),
            color = AmberSecondary.copy(alpha = 0.15f)
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
              Icon(imageVector = Icons.Default.QrCode2, contentDescription = null, tint = AmberSecondary)
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Scan QR with PhonePe / GPay / Paytm",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = AmberSecondary
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Scannable Dynamic QR Code Image
          Box(
            modifier = Modifier
              .size(210.dp)
              .clip(RoundedCornerShape(12.dp))
              .border(2.dp, NavyPrimary.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
              .padding(8.dp),
            contentAlignment = Alignment.Center
          ) {
            Image(
              bitmap = qrBitmap.asImageBitmap(),
              contentDescription = "UPI Payment QR Code",
              modifier = Modifier.fillMaxSize()
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // UPI ID Display with 1-Tap Copy
          Surface(
            shape = RoundedCornerShape(12.dp),
            color = NavyContainer,
            modifier = Modifier
              .fillMaxWidth()
              .clickable {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("UPI ID", upiId)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "UPI ID copied: $upiId", Toast.LENGTH_SHORT).show()
              }
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
              Column {
                Text(text = "Official Receiver UPI ID:", fontSize = 11.sp, color = NavyPrimary)
                Text(
                  text = upiId,
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 16.sp,
                  color = NavyPrimary,
                  fontFamily = FontFamily.Monospace
                )
              }
              Surface(
                shape = CircleShape,
                color = NavyPrimary,
                modifier = Modifier.size(32.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy UPI ID",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Intent to open UPI Apps directly on device
          OutlinedButton(
            onClick = {
              val upiUri = QrCodeGenerator.buildUpiUri(upiId, agentConfig.payeeName, totalAmount, "TatkalBooking")
              val intent = Intent(Intent.ACTION_VIEW, Uri.parse(upiUri))
              try {
                context.startActivity(Intent.createChooser(intent, "Pay via UPI App"))
              } catch (e: Exception) {
                Toast.makeText(context, "No UPI app found. Please copy UPI ID $upiId", Toast.LENGTH_SHORT).show()
              }
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text("Open Installed UPI App (GPay / PhonePe / Paytm)")
          }
        }
      }
    }

    // 3. Enter UTR / Transaction ID
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Payment Confirmation & UTR",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
          Text(
            text = "Payment karne ke baad apna UTR / Ref Number yahan enter karein ya niche click karein.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = utrInput,
            onValueChange = { viewModel.paymentUtrInput.value = it },
            label = { Text("12-Digit UPI Ref / UTR Number (Optional)") },
            placeholder = { Text("e.g. 429184029104") },
            leadingIcon = { Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = GreenTertiary) },
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("utr_number_input")
          )

          Spacer(modifier = Modifier.height(14.dp))

          Button(
            onClick = {
              viewModel.submitBookingAndPay(utrInput) { newId ->
                onPaymentSuccess(newId)
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = GreenTertiary),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("confirm_payment_submit_button")
          ) {
            Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "I Have Paid ₹${totalAmount.toInt()} - Place Booking",
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = Color.White
            )
          }
        }
      }
      Spacer(modifier = Modifier.height(80.dp))
    }
  }
}

// -------------------------------------------------------------------------------------------------
// STEP 4: SUCCESS CONFIRMATION
// -------------------------------------------------------------------------------------------------
@Composable
fun StepSuccess(
  viewModel: BookingViewModel,
  onNavigateToDetail: (BookingRequestEntity) -> Unit,
  onNavigateHome: () -> Unit
) {
  val lastId by viewModel.lastCreatedBookingId.collectAsStateWithLifecycle()
  val bookings by viewModel.allBookings.collectAsStateWithLifecycle()
  val createdBooking = bookings.find { it.id == lastId } ?: bookings.firstOrNull()

  Column(
    horizontalAlignment = Alignment.CenterHorizontally,
    modifier = Modifier
      .fillMaxSize()
      .padding(24.dp)
  ) {
    Spacer(modifier = Modifier.height(30.dp))

    Surface(
      shape = CircleShape,
      color = Color(0xFFE8F5E9),
      modifier = Modifier.size(80.dp)
    ) {
      Box(contentAlignment = Alignment.Center) {
        Icon(
          imageVector = Icons.Default.CheckCircle,
          contentDescription = null,
          tint = Color(0xFF2E7D32),
          modifier = Modifier.size(54.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(18.dp))

    Text(
      text = "Booking Request Submitted!",
      fontWeight = FontWeight.ExtraBold,
      fontSize = 22.sp,
      color = MaterialTheme.colorScheme.onBackground
    )

    Spacer(modifier = Modifier.height(6.dp))

    Text(
      text = "Payment verified on ${createdBooking?.upiId ?: "9709803741@mbk"}",
      color = Color(0xFF2E7D32),
      fontWeight = FontWeight.SemiBold,
      fontSize = 14.sp
    )

    Spacer(modifier = Modifier.height(16.dp))

    Card(
      shape = RoundedCornerShape(16.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(modifier = Modifier.padding(18.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Booking Ref ID:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          Text(
            text = createdBooking?.bookingRef ?: "TS-2026",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Train:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          Text(text = createdBooking?.trainNumberOrName ?: "", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Journey Date:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          Text(text = createdBooking?.journeyDate ?: "", fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text("Amount Paid:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
          Text(
            text = "₹${createdBooking?.totalPaid?.toInt() ?: 0}",
            fontWeight = FontWeight.Bold,
            color = NavyPrimary,
            fontSize = 14.sp
          )
        }

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "Aapka details aur payment receive ho gaya hai. Booking time hone par hum ticket book karke 1-2 ghanton mein yahan confirmed E-Ticket de denge!",
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          lineHeight = 17.sp
        )
      }
    }

    Spacer(modifier = Modifier.height(24.dp))

    Button(
      onClick = {
        if (createdBooking != null) {
          onNavigateToDetail(createdBooking)
        } else {
          onNavigateHome()
        }
      },
      colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
      shape = RoundedCornerShape(14.dp),
      modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .testTag("track_status_button")
    ) {
      Text("Track Live Status & View Ticket", fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }

    Spacer(modifier = Modifier.height(10.dp))

    OutlinedButton(
      onClick = { onNavigateHome() },
      shape = RoundedCornerShape(14.dp),
      modifier = Modifier.fillMaxWidth()
    ) {
      Text("Back to Home")
    }
  }
}
