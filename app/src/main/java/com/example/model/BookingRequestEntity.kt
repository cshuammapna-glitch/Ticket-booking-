package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "booking_requests")
data class BookingRequestEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val bookingRef: String, // e.g. TS-98241
  val fromStation: String,
  val toStation: String,
  val journeyDate: String,
  val travelType: String, // "Train / Tatkal", "Bus", "Flight"
  val trainNumberOrName: String, // e.g. "12301 Rajdhani Express"
  val travelClass: String, // "3A", "2A", "SL", "1A", "CC", "2S"
  val quota: String, // "Tatkal", "Premium Tatkal", "General", "Ladies"
  val passengersJson: String,
  val ticketFare: Double,
  val conciergeFee: Double,
  val totalPaid: Double,
  val paymentMode: String, // "UPI (9709803741@mbk)", "QR Scanner", "NetBanking"
  val upiId: String = "9709803741@mbk",
  val utrNumber: String = "",
  val paymentVerified: Boolean = true,
  val status: String, // "PAYMENT_VERIFIED", "WAITING_WINDOW", "IN_PROGRESS", "CONFIRMED", "WAITLISTED", "CANCELLED"
  val pnrNumber: String = "", // e.g. "284-9182741"
  val coachNumber: String = "", // e.g. "B4"
  val seatBerth: String = "", // e.g. "34 MB, 35 LB"
  val bookingTimeNote: String = "", // e.g. "Booked at Tatkal opening window"
  val requestTimestamp: Long = System.currentTimeMillis(),
  val confirmedTimestamp: Long = 0L,
  val agentNotes: String = ""
) {
  val passengerList: List<Passenger>
    get() = Passenger.listFromJsonString(passengersJson)

  val isConfirmed: Boolean
    get() = status == "CONFIRMED"
}
