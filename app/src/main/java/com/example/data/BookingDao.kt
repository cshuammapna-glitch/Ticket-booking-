package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.BookingRequestEntity
import com.example.model.SavedPassengerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {
  // Booking Requests
  @Query("SELECT * FROM booking_requests ORDER BY requestTimestamp DESC")
  fun getAllBookings(): Flow<List<BookingRequestEntity>>

  @Query("SELECT * FROM booking_requests WHERE id = :id LIMIT 1")
  fun getBookingById(id: Long): Flow<BookingRequestEntity?>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertBooking(booking: BookingRequestEntity): Long

  @Update
  suspend fun updateBooking(booking: BookingRequestEntity)

  @Query("DELETE FROM booking_requests WHERE id = :id")
  suspend fun deleteBookingById(id: Long)

  // Saved Passengers
  @Query("SELECT * FROM saved_passengers ORDER BY id DESC")
  fun getAllSavedPassengers(): Flow<List<SavedPassengerEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertSavedPassenger(passenger: SavedPassengerEntity): Long

  @Query("DELETE FROM saved_passengers WHERE id = :id")
  suspend fun deleteSavedPassenger(id: Long)
}
