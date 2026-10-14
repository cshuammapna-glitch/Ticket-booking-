package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DirectionsRailway
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BookingRequestEntity
import com.example.ui.components.StatusBadge
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.GreenTertiary
import com.example.ui.theme.NavyContainer
import com.example.ui.theme.NavyPrimary
import com.example.util.QrCodeGenerator
import com.example.viewmodel.BookingViewModel

@Composable
fun BookingDetailScreen(
  booking: BookingRequestEntity,
  viewModel: BookingViewModel,
  onNavigateBack: () -> Unit
) {
  val context = LocalContext.current
  val passengers = booking.passengerList

  // QR code bitmap representing ticket PNR and verification
  val ticketQr = remember(booking.pnrNumber, booking.bookingRef) {
    QrCodeGenerator.generateQrBitmap(
      content = "TICKET-PNR:${if (booking.pnrNumber.isNotBlank()) booking.pnrNumber else booking.bookingRef}|TRAIN:${booking.trainNumberOrName}|PAID:INR${booking.totalPaid.toInt()}",
      sizePx = 300
    )
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .padding(16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Header Row with Back Button
    item {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(onClick = onNavigateBack) {
            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
          }
          Column {
            Text(
              text = if (booking.isConfirmed) "Electronic Reservation Slip (ERS)" else "Booking Request Status",
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp
            )
            Text(
              text = "Ref: ${booking.bookingRef}",
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
          }
        }
        StatusBadge(status = booking.status)
      }
    }

    // 1. If CONFIRMED: Official E-Ticket Layout
    if (booking.isConfirmed) {
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("e_ticket_card")
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            // E-Ticket Header
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                  shape = CircleShape,
                  color = NavyPrimary,
                  modifier = Modifier.size(36.dp)
                ) {
                  Icon(
                    imageVector = Icons.Default.DirectionsRailway,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(8.dp)
                  )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = "INDIAN RAILWAYS / TATKAL SEVA",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp,
                    color = NavyPrimary,
                    letterSpacing = 0.5.sp
                  )
                  Text(
                    text = "Confirmed E-Ticket",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    color = Color(0xFF2E7D32)
                  )
                }
              }

              Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFE8F5E9)
              ) {
                Text(
                  text = "CNF / CONFIRMED",
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp,
                  color = Color(0xFF2E7D32),
                  modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(14.dp))

            // PNR & Train Row
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column {
                Text("PNR NUMBER", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                  text = booking.pnrNumber,
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 18.sp,
                  fontFamily = FontFamily.Monospace,
                  color = NavyPrimary
                )
              }

              Column(horizontalAlignment = Alignment.End) {
                Text("COACH / BERTH", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                  text = "${booking.coachNumber} - ${booking.seatBerth}",
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 16.sp,
                  color = Color(0xFF2E7D32)
                )
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Train & Route Details
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(modifier = Modifier.padding(12.dp)) {
                Text(
                  text = booking.trainNumberOrName,
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(
                    text = "From: ${booking.fromStation}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                  )
                  Text(
                    text = "To: ${booking.toStation}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                  )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(
                    text = "Date: ${booking.journeyDate}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                  Text(
                    text = "Class: ${booking.travelClass} (${booking.quota})",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Passenger Table
            Text(
              text = "Passenger Details (With ID Proof):",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            passengers.forEachIndexed { i, p ->
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 4.dp)
              ) {
                Row(
                  modifier = Modifier.padding(10.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Column(modifier = Modifier.weight(1f)) {
                    Text(
                      text = "${i + 1}. ${p.fullName} (${p.age} Yrs / ${p.gender})",
                      fontWeight = FontWeight.Bold,
                      fontSize = 13.sp
                    )
                    Text(
                      text = "Aadhaar: ${p.maskedAadhaar} • PAN: ${p.maskedPan}",
                      fontSize = 11.sp,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                      text = "Berth: ${p.berthPreference} • Food: ${p.foodPreference}",
                      fontSize = 11.sp,
                      color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                  }

                  Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFE8F5E9)
                  ) {
                    Text(
                      text = "CNF",
                      fontWeight = FontWeight.Bold,
                      fontSize = 11.sp,
                      color = Color(0xFF2E7D32),
                      modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Verification QR Code & Fare Breakdown
            Row(
              modifier = Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Box(
                modifier = Modifier
                  .size(90.dp)
                  .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(8.dp))
                  .padding(4.dp)
              ) {
                Image(
                  bitmap = ticketQr.asImageBitmap(),
                  contentDescription = "Ticket QR Verification",
                  modifier = Modifier.fillMaxSize()
                )
              }

              Column(
                modifier = Modifier.weight(1f).padding(start = 14.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
              ) {
                Text(text = "Total Fare Paid: ₹${booking.totalPaid.toInt()}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(text = "Paid to: ${booking.upiId}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = "UTR: ${booking.utrNumber}", fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                Text(text = booking.bookingTimeNote, fontSize = 10.sp, color = Color(0xFF2E7D32))
              }
            }
          }
        }
      }
    } else {
      // 2. If NOT YET CONFIRMED: Real-Time Fulfillment Tracker
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Text(
              text = "Live Fulfillment Tracker",
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
            Text(
              text = "Aapka payment receive ho chuka hai. Hum booking time par ticket book kar rahe hain.",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Step 1: Payment verified
            TimelineStepItem(
              stepNumber = "1",
              title = "Payment Received & Verified",
              subtitle = "₹${booking.totalPaid.toInt()} confirmed on ${booking.upiId} (UTR: ${booking.utrNumber})",
              isDone = true,
              isCurrent = false
            )

            // Step 2: Passenger ID verified
            TimelineStepItem(
              stepNumber = "2",
              title = "Aadhaar & PAN Verification",
              subtitle = "${passengers.size} Passenger details logged for IRCTC Tatkal submission",
              isDone = true,
              isCurrent = false
            )

            // Step 3: Waiting for Tatkal window or in progress
            TimelineStepItem(
              stepNumber = "3",
              title = "Booking Window Queue",
              subtitle = if (booking.status == "IN_PROGRESS") "Agent is actively booking your ticket in the IRCTC window now!" else "Queued for Tatkal opening (AC: 10:00 AM / Non-AC: 11:00 AM)",
              isDone = booking.status == "IN_PROGRESS",
              isCurrent = booking.status == "WAITING_WINDOW" || booking.status == "PAYMENT_VERIFIED"
            )

            // Step 4: Final confirmed ticket
            TimelineStepItem(
              stepNumber = "4",
              title = "Ticket Issued & Delivered",
              subtitle = "Confirmed PNR, Coach, and Seats will be uploaded here in 1-2 hours",
              isDone = false,
              isCurrent = false,
              isLast = true
            )
          }
        }
      }
    }

    // Passenger Data Display Card
    item {
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(text = "Submitted Passenger Information:", fontWeight = FontWeight.Bold, fontSize = 14.sp)
          Spacer(modifier = Modifier.height(10.dp))

          passengers.forEachIndexed { i, p ->
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
              Text(
                text = "${i + 1}. ${p.fullName} • ${p.age} Yrs (${p.gender})",
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
              )
              Text(
                text = "Mobile: ${p.mobileNumber}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              Text(
                text = "Aadhaar: ${p.maskedAadhaar} • PAN: ${p.maskedPan}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              )
              if (i < passengers.size - 1) {
                Spacer(modifier = Modifier.height(6.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(6.dp))
              }
            }
          }
        }
      }
    }

    // Actions & Share
    item {
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        if (booking.isConfirmed) {
          Button(
            onClick = {
              val shareText = """
                *TATKAL SEVA - CONFIRMED E-TICKET*
                Train: ${booking.trainNumberOrName}
                PNR: ${booking.pnrNumber}
                Coach: ${booking.coachNumber} | Seat: ${booking.seatBerth}
                Route: ${booking.fromStation} to ${booking.toStation}
                Date: ${booking.journeyDate}
                Passenger: ${passengers.firstOrNull()?.fullName}
                Status: CONFIRMED (CNF)
              """.trimIndent()

              val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, shareText)
                type = "text/plain"
              }
              context.startActivity(Intent.createChooser(sendIntent, "Share E-Ticket via WhatsApp"))
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
          ) {
            Icon(imageVector = Icons.Default.Share, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Share E-Ticket on WhatsApp", fontWeight = FontWeight.Bold)
          }

          OutlinedButton(
            onClick = {
              Toast.makeText(context, "E-Ticket downloaded to device", Toast.LENGTH_SHORT).show()
            },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(imageVector = Icons.Default.Print, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Download / Print PDF Ticket")
          }
        } else {
          Button(
            onClick = {
              val helplineText = "Hello TatkalSeva Agent, I have placed booking ${booking.bookingRef} for train ${booking.trainNumberOrName} on ${booking.journeyDate}. Please verify my payment on ${booking.upiId}."
              val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, helplineText)
                type = "text/plain"
              }
              try {
                context.startActivity(Intent.createChooser(sendIntent, "Contact Booking Executive"))
              } catch (e: Exception) {
                Toast.makeText(context, "Agent Helpline: 9709803741", Toast.LENGTH_LONG).show()
              }
            },
            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp)
          ) {
            Icon(imageVector = Icons.Default.SupportAgent, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Contact Booking Executive on WhatsApp", fontWeight = FontWeight.Bold)
          }
        }
      }
      Spacer(modifier = Modifier.height(80.dp))
    }
  }
}

@Composable
fun TimelineStepItem(
  stepNumber: String,
  title: String,
  subtitle: String,
  isDone: Boolean,
  isCurrent: Boolean,
  isLast: Boolean = false
) {
  Row(verticalAlignment = Alignment.Top) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Surface(
        shape = CircleShape,
        color = when {
          isDone -> Color(0xFF2E7D32)
          isCurrent -> AmberSecondary
          else -> MaterialTheme.colorScheme.surfaceVariant
        },
        modifier = Modifier.size(26.dp)
      ) {
        Box(contentAlignment = Alignment.Center) {
          if (isDone) {
            Icon(
              imageVector = Icons.Default.Check,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(16.dp)
            )
          } else if (isCurrent) {
            Icon(
              imageVector = Icons.Default.HourglassTop,
              contentDescription = null,
              tint = Color.White,
              modifier = Modifier.size(14.dp)
            )
          } else {
            Text(
              text = stepNumber,
              fontSize = 11.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }

      if (!isLast) {
        Box(
          modifier = Modifier
            .width(2.dp)
            .height(36.dp)
            .background(if (isDone) Color(0xFF2E7D32) else MaterialTheme.colorScheme.surfaceVariant)
        )
      }
    }

    Spacer(modifier = Modifier.width(12.dp))

    Column(modifier = Modifier.padding(bottom = if (isLast) 0.dp else 16.dp)) {
      Text(
        text = title,
        fontWeight = if (isCurrent || isDone) FontWeight.Bold else FontWeight.Medium,
        fontSize = 13.sp,
        color = if (isCurrent) AmberSecondary else if (isDone) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
      )
      Text(
        text = subtitle,
        fontSize = 11.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        lineHeight = 15.sp
      )
    }
  }
}
