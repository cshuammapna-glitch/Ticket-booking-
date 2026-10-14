package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.BookingRepository
import com.example.model.AgentConfig
import com.example.model.BookingRequestEntity
import com.example.model.Passenger
import com.example.model.SavedPassengerEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BookingViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: BookingRepository
  val allBookings: StateFlow<List<BookingRequestEntity>>
  val allSavedPassengers: StateFlow<List<SavedPassengerEntity>>

  private val _agentConfig = MutableStateFlow(AgentConfig())
  val agentConfig: StateFlow<AgentConfig> = _agentConfig.asStateFlow()

  private val _isAdminMode = MutableStateFlow(false)
  val isAdminMode: StateFlow<Boolean> = _isAdminMode.asStateFlow()

  private val _selectedBooking = MutableStateFlow<BookingRequestEntity?>(null)
  val selectedBooking: StateFlow<BookingRequestEntity?> = _selectedBooking.asStateFlow()

  // Live Tatkal Timers
  private val _acCountdown = MutableStateFlow("00:00:00")
  val acCountdown: StateFlow<String> = _acCountdown.asStateFlow()

  private val _sleeperCountdown = MutableStateFlow("00:00:00")
  val sleeperCountdown: StateFlow<String> = _sleeperCountdown.asStateFlow()

  // New Booking Wizard State
  var currentStep = MutableStateFlow(0) // 0: Journey, 1: Passengers, 2: Payment, 3: Confirmed
  var travelType = MutableStateFlow("Train / Tatkal") // "Train / Tatkal", "Bus", "Flight"
  var fromStation = MutableStateFlow("NDLS - New Delhi")
  var toStation = MutableStateFlow("PNBE - Patna Jn")
  var journeyDate = MutableStateFlow("Tomorrow")
  var trainNumberOrName = MutableStateFlow("12302 Rajdhani Exp")
  var travelClass = MutableStateFlow("3A - AC 3 Tier")
  var quota = MutableStateFlow("Tatkal")

  // Passenger list for current booking
  var bookingPassengers = MutableStateFlow<List<Passenger>>(
    listOf(
      Passenger(
        fullName = "",
        age = 28,
        dob = "",
        gender = "Male",
        mobileNumber = "",
        aadhaarNumber = "",
        panNumber = "",
        berthPreference = "Lower",
        foodPreference = "Veg"
      )
    )
  )

  // Payment State
  var customTicketPrice = MutableStateFlow(1650.0)
  var paymentUtrInput = MutableStateFlow("")
  var lastCreatedBookingId = MutableStateFlow<Long?>(null)

  init {
    val database = AppDatabase.getDatabase(application)
    repository = BookingRepository(database.bookingDao())

    allBookings = repository.allBookings.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

    allSavedPassengers = repository.allSavedPassengers.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000),
      initialValue = emptyList()
    )

    viewModelScope.launch {
      repository.prepopulateIfEmpty()
    }

    startTatkalTimerLoop()
  }

  fun toggleAdminMode() {
    _isAdminMode.value = !_isAdminMode.value
  }

  fun setAdminMode(enabled: Boolean) {
    _isAdminMode.value = enabled
  }

  fun selectBooking(booking: BookingRequestEntity?) {
    _selectedBooking.value = booking
  }

  fun resetWizard() {
    currentStep.value = 0
    fromStation.value = "NDLS - New Delhi"
    toStation.value = "PNBE - Patna Jn"
    journeyDate.value = "Tomorrow"
    trainNumberOrName.value = "12302 Rajdhani Exp"
    travelClass.value = "3A - AC 3 Tier"
    quota.value = "Tatkal"
    customTicketPrice.value = 1650.0
    paymentUtrInput.value = ""
    bookingPassengers.value = listOf(
      Passenger(
        fullName = "",
        age = 28,
        dob = "",
        gender = "Male",
        mobileNumber = "",
        aadhaarNumber = "",
        panNumber = "",
        berthPreference = "Lower",
        foodPreference = "Veg"
      )
    )
  }

  fun addPassenger() {
    val current = bookingPassengers.value.toMutableList()
    current.add(
      Passenger(
        fullName = "",
        age = 25,
        dob = "",
        gender = "Male",
        mobileNumber = current.firstOrNull()?.mobileNumber ?: "",
        aadhaarNumber = "",
        panNumber = "",
        berthPreference = "Middle",
        foodPreference = "Veg"
      )
    )
    bookingPassengers.value = current
  }

  fun removePassenger(index: Int) {
    if (bookingPassengers.value.size > 1) {
      val current = bookingPassengers.value.toMutableList()
      current.removeAt(index)
      bookingPassengers.value = current
    }
  }

  fun updatePassenger(index: Int, updated: Passenger) {
    val current = bookingPassengers.value.toMutableList()
    if (index in current.indices) {
      current[index] = updated
      bookingPassengers.value = current
    }
  }

  fun autofillFromSavedPassenger(index: Int, saved: SavedPassengerEntity) {
    updatePassenger(index, saved.toPassenger())
  }

  val totalEstimatedFare: Double
    get() {
      val count = bookingPassengers.value.size
      val perTicket = customTicketPrice.value
      val feePerTicket = if (quota.value.contains("Tatkal")) {
        _agentConfig.value.tatkalServiceFee
      } else {
        _agentConfig.value.generalServiceFee
      }
      return (perTicket * count) + (feePerTicket * count)
    }

  fun submitBookingAndPay(utr: String, onComplete: (Long) -> Unit) {
    viewModelScope.launch {
      val count = bookingPassengers.value.size
      val perTicket = customTicketPrice.value
      val feePerTicket = if (quota.value.contains("Tatkal")) {
        _agentConfig.value.tatkalServiceFee
      } else {
        _agentConfig.value.generalServiceFee
      }
      val totalFare = perTicket * count
      val totalFee = feePerTicket * count
      val totalPayable = totalFare + totalFee

      val randomNum = (1000..9999).random()
      val refId = "TS-${Calendar.getInstance().get(Calendar.YEAR)}-$randomNum"

      val entity = BookingRequestEntity(
        bookingRef = refId,
        fromStation = fromStation.value,
        toStation = toStation.value,
        journeyDate = journeyDate.value,
        travelType = travelType.value,
        trainNumberOrName = trainNumberOrName.value,
        travelClass = travelClass.value,
        quota = quota.value,
        passengersJson = Passenger.listToJsonString(bookingPassengers.value),
        ticketFare = totalFare,
        conciergeFee = totalFee,
        totalPaid = totalPayable,
        paymentMode = "UPI (${_agentConfig.value.upiId})",
        upiId = _agentConfig.value.upiId,
        utrNumber = if (utr.isBlank()) "UPI-REF-${System.currentTimeMillis().toString().takeLast(8)}" else utr.trim(),
        paymentVerified = true,
        status = "PAYMENT_VERIFIED",
        pnrNumber = "",
        coachNumber = "",
        seatBerth = "",
        bookingTimeNote = "Payment received on ${_agentConfig.value.upiId}. Awaiting Tatkal/booking window opening.",
        requestTimestamp = System.currentTimeMillis(),
        confirmedTimestamp = 0L,
        agentNotes = "Customer details received with Aadhaar & PAN verification. Ready for booking window."
      )

      val newId = repository.insertBooking(entity)
      lastCreatedBookingId.value = newId

      // Save passengers to local quick-access saved list
      for (p in bookingPassengers.value) {
        if (p.fullName.isNotBlank() && p.aadhaarNumber.isNotBlank()) {
          repository.insertSavedPassenger(SavedPassengerEntity.fromPassenger(p))
        }
      }

      currentStep.value = 3
      onComplete(newId)
    }
  }

  // Admin / Agent actions
  fun agentConfirmTicket(
    bookingId: Long,
    pnr: String,
    coach: String,
    seatBerth: String,
    note: String
  ) {
    viewModelScope.launch {
      val booking = allBookings.value.find { it.id == bookingId } ?: return@launch
      val updated = booking.copy(
        status = "CONFIRMED",
        pnrNumber = pnr.ifBlank { "245-${(1000000..9999999).random()}" },
        coachNumber = coach.ifBlank { "B3" },
        seatBerth = seatBerth.ifBlank { "32 LB, 33 MB (CNF)" },
        bookingTimeNote = if (note.isBlank()) "Booked successfully during IRCTC booking window. Confirmed E-Ticket delivered." else note,
        confirmedTimestamp = System.currentTimeMillis(),
        agentNotes = "Verified payment on ${_agentConfig.value.upiId}. Ticket confirmed by Agent."
      )
      repository.updateBooking(updated)
      if (_selectedBooking.value?.id == bookingId) {
        _selectedBooking.value = updated
      }
    }
  }

  fun agentUpdateStatus(bookingId: Long, newStatus: String, notes: String) {
    viewModelScope.launch {
      val booking = allBookings.value.find { it.id == bookingId } ?: return@launch
      val updated = booking.copy(
        status = newStatus,
        agentNotes = notes
      )
      repository.updateBooking(updated)
      if (_selectedBooking.value?.id == bookingId) {
        _selectedBooking.value = updated
      }
    }
  }

  fun agentDeleteBooking(bookingId: Long) {
    viewModelScope.launch {
      repository.deleteBooking(bookingId)
      if (_selectedBooking.value?.id == bookingId) {
        _selectedBooking.value = null
      }
    }
  }

  fun updateAgentConfig(upi: String, phone: String, tatkalFee: Double, generalFee: Double) {
    _agentConfig.value = _agentConfig.value.copy(
      upiId = upi.trim(),
      agentPhone = phone.trim(),
      tatkalServiceFee = tatkalFee,
      generalServiceFee = generalFee
    )
  }

  fun savePassenger(passenger: Passenger) {
    viewModelScope.launch {
      repository.insertSavedPassenger(SavedPassengerEntity.fromPassenger(passenger))
    }
  }

  fun deleteSavedPassenger(id: Long) {
    viewModelScope.launch {
      repository.deleteSavedPassenger(id)
    }
  }

  private fun startTatkalTimerLoop() {
    viewModelScope.launch {
      while (true) {
        val now = Calendar.getInstance()

        // AC Tatkal Window: 10:00 AM
        val acTarget = Calendar.getInstance().apply {
          set(Calendar.HOUR_OF_DAY, 10)
          set(Calendar.MINUTE, 0)
          set(Calendar.SECOND, 0)
          set(Calendar.MILLISECOND, 0)
          if (before(now)) {
            add(Calendar.DAY_OF_YEAR, 1)
          }
        }
        val acDiffMs = acTarget.timeInMillis - now.timeInMillis
        _acCountdown.value = formatDuration(acDiffMs)

        // Sleeper / Non-AC Tatkal Window: 11:00 AM
        val sleeperTarget = Calendar.getInstance().apply {
          set(Calendar.HOUR_OF_DAY, 11)
          set(Calendar.MINUTE, 0)
          set(Calendar.SECOND, 0)
          set(Calendar.MILLISECOND, 0)
          if (before(now)) {
            add(Calendar.DAY_OF_YEAR, 1)
          }
        }
        val sleeperDiffMs = sleeperTarget.timeInMillis - now.timeInMillis
        _sleeperCountdown.value = formatDuration(sleeperDiffMs)

        delay(1000)
      }
    }
  }

  private fun formatDuration(millis: Long): String {
    val hours = TimeUnit.MILLISECONDS.toHours(millis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
    return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
  }
}
