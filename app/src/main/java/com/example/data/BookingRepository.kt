package com.example.data

import com.example.model.BookingRequestEntity
import com.example.model.Passenger
import com.example.model.SavedPassengerEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class BookingRepository(private val bookingDao: BookingDao) {
  val allBookings: Flow<List<BookingRequestEntity>> = bookingDao.getAllBookings()
  val allSavedPassengers: Flow<List<SavedPassengerEntity>> = bookingDao.getAllSavedPassengers()

  fun getBookingById(id: Long): Flow<BookingRequestEntity?> = bookingDao.getBookingById(id)

  suspend fun insertBooking(booking: BookingRequestEntity): Long = bookingDao.insertBooking(booking)

  suspend fun updateBooking(booking: BookingRequestEntity) = bookingDao.updateBooking(booking)

  suspend fun deleteBooking(id: Long) = bookingDao.deleteBookingById(id)

  suspend fun insertSavedPassenger(passenger: SavedPassengerEntity): Long = bookingDao.insertSavedPassenger(passenger)

  suspend fun deleteSavedPassenger(id: Long) = bookingDao.deleteSavedPassenger(id)

  suspend fun prepopulateIfEmpty() {
    val existing = bookingDao.getAllBookings().firstOrNull()
    if (existing.isNullOrEmpty()) {
      // Create initial confirmed sample e-ticket
      val samplePassengers = listOf(
        Passenger(
          fullName = "Rajesh Kumar",
          age = 32,
          dob = "14/08/1994",
          gender = "Male",
          mobileNumber = "9876543210",
          aadhaarNumber = "548291823741",
          panNumber = "ABCDE1234F",
          berthPreference = "Lower",
          foodPreference = "Veg"
        ),
        Passenger(
          fullName = "Priya Kumari",
          age = 29,
          dob = "22/11/1997",
          gender = "Female",
          mobileNumber = "9876543210",
          aadhaarNumber = "892134561092",
          panNumber = "PQXYZ5678M",
          berthPreference = "Middle",
          foodPreference = "Veg"
        )
      )

      val confirmedBooking = BookingRequestEntity(
        bookingRef = "TS-2026-7841",
        fromStation = "NDLS - New Delhi",
        toStation = "PNBE - Patna Jn",
        journeyDate = "Tomorrow, 14 Oct",
        travelType = "Train / Tatkal",
        trainNumberOrName = "12302 Howrah Rajdhani Express",
        travelClass = "3A - AC 3 Tier",
        quota = "Tatkal",
        passengersJson = Passenger.listToJsonString(samplePassengers),
        ticketFare = 3450.0,
        conciergeFee = 350.0,
        totalPaid = 3800.0,
        paymentMode = "UPI (9709803741@mbk)",
        upiId = "9709803741@mbk",
        utrNumber = "UTR429184029104",
        paymentVerified = true,
        status = "CONFIRMED",
        pnrNumber = "245-8910294",
        coachNumber = "B3",
        seatBerth = "32 LB, 33 MB (CNF)",
        bookingTimeNote = "Booked at 10:00:22 AM during IRCTC AC Tatkal window. Confirmed E-Ticket delivered.",
        requestTimestamp = System.currentTimeMillis() - 7200000,
        confirmedTimestamp = System.currentTimeMillis() - 3600000,
        agentNotes = "Ticket issued successfully. Verified Aadhaar & PAN."
      )

      val pendingBooking = BookingRequestEntity(
        bookingRef = "TS-2026-9102",
        fromStation = "BCT - Mumbai Central",
        toStation = "BKN - Bikaner Jn",
        journeyDate = "16 Oct 2026",
        travelType = "Train / Tatkal",
        trainNumberOrName = "12490 Dadar Bikaner SF Exp",
        travelClass = "SL - Sleeper",
        quota = "Tatkal",
        passengersJson = Passenger.listToJsonString(
          listOf(
            Passenger(
              fullName = "Amit Sharma",
              age = 26,
              dob = "05/03/2000",
              gender = "Male",
              mobileNumber = "9823456789",
              aadhaarNumber = "782349018234",
              panNumber = "AMTPN4321K",
              berthPreference = "Side Lower",
              foodPreference = "No Food"
            )
          )
        ),
        ticketFare = 780.0,
        conciergeFee = 250.0,
        totalPaid = 1030.0,
        paymentMode = "UPI (9709803741@mbk)",
        upiId = "9709803741@mbk",
        utrNumber = "UTR581920394812",
        paymentVerified = true,
        status = "WAITING_WINDOW",
        pnrNumber = "",
        coachNumber = "",
        seatBerth = "",
        bookingTimeNote = "Awaiting 11:00 AM Sleeper Tatkal booking window tomorrow.",
        requestTimestamp = System.currentTimeMillis() - 1800000,
        confirmedTimestamp = 0L,
        agentNotes = "Payment verified on 9709803741@mbk. Queued for 11:00 AM IRCTC Tatkal opening."
      )

      bookingDao.insertBooking(confirmedBooking)
      bookingDao.insertBooking(pendingBooking)

      // Add a default saved passenger for quick filling
      bookingDao.insertSavedPassenger(
        SavedPassengerEntity.fromPassenger(samplePassengers.first())
      )
    }
  }
}
