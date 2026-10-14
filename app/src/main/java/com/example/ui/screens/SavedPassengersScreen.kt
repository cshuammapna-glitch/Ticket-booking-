package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.Passenger
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.NavyContainer
import com.example.ui.theme.NavyPrimary
import com.example.viewmodel.BookingViewModel

@Composable
fun SavedPassengersScreen(viewModel: BookingViewModel) {
  val savedPassengers by viewModel.allSavedPassengers.collectAsStateWithLifecycle()
  val context = LocalContext.current
  var showAddDialog by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Column {
          Text(
            text = "Saved Passengers Master List",
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = MaterialTheme.colorScheme.onBackground
          )
          Text(
            text = "1-Tap autofill for fast Tatkal bookings",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }

        Button(
          onClick = { showAddDialog = true },
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
        ) {
          Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text("Add New", fontSize = 12.sp)
        }
      }
    }

    if (savedPassengers.isEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(24.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Person,
              contentDescription = null,
              tint = MaterialTheme.colorScheme.outline,
              modifier = Modifier.size(44.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("No Saved Passengers", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(
              "Add frequent travelers here to autofill their Aadhaar & PAN instantly during booking.",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
          }
        }
      }
    } else {
      items(savedPassengers) { sp ->
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
              Surface(
                shape = CircleShape,
                color = NavyContainer,
                modifier = Modifier.size(38.dp)
              ) {
                Box(contentAlignment = Alignment.Center) {
                  Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = NavyPrimary)
                }
              }
              Spacer(modifier = Modifier.width(12.dp))
              Column {
                Text(text = sp.fullName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(
                  text = "${sp.age} Yrs • ${sp.gender} • Mob: ${sp.mobileNumber}",
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                  text = "Aadhaar: ${sp.toPassenger().formattedAadhaar}",
                  fontSize = 11.sp,
                  color = AmberSecondary,
                  fontWeight = FontWeight.Medium
                )
                if (sp.panNumber.isNotBlank()) {
                  Text(
                    text = "PAN: ${sp.panNumber}",
                    fontSize = 11.sp,
                    color = NavyPrimary,
                    fontWeight = FontWeight.Medium
                  )
                }
              }
            }

            IconButton(onClick = { viewModel.deleteSavedPassenger(sp.id) }) {
              Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(80.dp))
    }
  }

  if (showAddDialog) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("28") }
    var dob by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }
    var mobile by remember { mutableStateOf("") }
    var aadhaar by remember { mutableStateOf("") }
    var pan by remember { mutableStateOf("") }

    AlertDialog(
      onDismissRequest = { showAddDialog = false },
      title = { Text("Add Passenger to Master List", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Full Name (as on Aadhaar)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
              value = age,
              onValueChange = { age = it },
              label = { Text("Age") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              singleLine = true,
              modifier = Modifier.weight(1f)
            )

            OutlinedTextField(
              value = dob,
              onValueChange = { dob = it },
              label = { Text("DOB (DD/MM/YYYY)") },
              singleLine = true,
              modifier = Modifier.weight(1.5f)
            )
          }

          OutlinedTextField(
            value = mobile,
            onValueChange = { mobile = it },
            label = { Text("Mobile Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = aadhaar,
            onValueChange = { aadhaar = it.filter { c -> c.isDigit() }.take(12) },
            label = { Text("Aadhaar Number (12 Digits)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          OutlinedTextField(
            value = pan,
            onValueChange = { pan = it.uppercase().take(10) },
            label = { Text("PAN Number (10 Chars)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (name.isBlank() || mobile.isBlank()) {
              Toast.makeText(context, "Please enter Name & Mobile", Toast.LENGTH_SHORT).show()
            } else {
              viewModel.savePassenger(
                Passenger(
                  fullName = name,
                  age = age.toIntOrNull() ?: 25,
                  dob = dob,
                  gender = gender,
                  mobileNumber = mobile,
                  aadhaarNumber = aadhaar,
                  panNumber = pan
                )
              )
              showAddDialog = false
              Toast.makeText(context, "Passenger saved!", Toast.LENGTH_SHORT).show()
            }
          },
          colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
        ) {
          Text("Save Passenger")
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
      }
    )
  }
}
