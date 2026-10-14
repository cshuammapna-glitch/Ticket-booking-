package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.GreenTertiary
import com.example.ui.theme.NavyContainer
import com.example.ui.theme.NavyPrimary
import com.example.viewmodel.BookingViewModel

@Composable
fun TatkalGuideScreen(viewModel: BookingViewModel) {
  val agentConfig by viewModel.agentConfig.collectAsStateWithLifecycle()
  val context = LocalContext.current

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Text(
        text = "Tatkal Booking Rules & FAQ",
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp,
        color = MaterialTheme.colorScheme.onBackground
      )
      Text(
        text = "Indian Railways guidelines & concierge assistance terms",
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
    }

    // 1. Timings Card
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.AccessTime, contentDescription = null, tint = NavyPrimary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Tatkal Booking Timings", fontWeight = FontWeight.Bold, fontSize = 14.sp)
          }
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = "• AC Tatkal (1A, 2A, 3A, 3E, CC): Opens at 10:00 AM, one day prior to date of journey from train originating station.\n• Non-AC Tatkal (Sleeper, 2S): Opens at 11:00 AM, one day prior.\n• Requests must be submitted at least 30-60 minutes before window opens for fastest agent booking.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 18.sp
          )
        }
      }
    }

    // 2. ID Proof rules
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Badge, contentDescription = null, tint = AmberSecondary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Aadhaar & PAN Card Mandate", fontWeight = FontWeight.Bold, fontSize = 14.sp)
          }
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = "As per Ministry of Railways, at least one passenger traveling on a Tatkal e-ticket must carry an original government photo identity card (Aadhaar Card, PAN Card, Voter ID, or Driving License) in original while traveling.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 18.sp
          )
        }
      }
    }

    // 3. Payment & Refund Policy
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.MoneyOff, contentDescription = null, tint = GreenTertiary)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Payment & 100% Refund Policy", fontWeight = FontWeight.Bold, fontSize = 14.sp)
          }
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = "• Payment is deposited upfront via UPI to ${agentConfig.upiId}.\n• If due to heavy IRCTC rush your Tatkal ticket could not be booked or remains WL, your ticket fare amount is immediately refunded to your original UPI account.\n• Transparent pricing with zero hidden charges.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 18.sp
          )
        }
      }
    }

    // 4. Contact Agent
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavyContainer),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "Need Urgent Booking Help?",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = NavyPrimary
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Contact our executive directly on WhatsApp for emergency Tatkal bookings.",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
          Spacer(modifier = Modifier.height(12.dp))
          Button(
            onClick = {
              val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://wa.me/91${agentConfig.agentPhone}?text=Hi,%20I%20need%20urgent%20Tatkal%20ticket%20booking%20assistance.")
              }
              try {
                context.startActivity(intent)
              } catch (e: Exception) {
                Toast.makeText(context, "WhatsApp/Phone: +91 ${agentConfig.agentPhone}", Toast.LENGTH_LONG).show()
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(imageVector = Icons.Default.SupportAgent, contentDescription = null, tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Chat on WhatsApp (+91 ${agentConfig.agentPhone})", color = Color.White, fontWeight = FontWeight.Bold)
          }
        }
      }
      Spacer(modifier = Modifier.height(80.dp))
    }
  }
}
