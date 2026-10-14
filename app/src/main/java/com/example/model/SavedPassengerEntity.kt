package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_passengers")
data class SavedPassengerEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val fullName: String,
  val age: Int,
  val dob: String,
  val gender: String = "Male",
  val mobileNumber: String,
  val aadhaarNumber: String,
  val panNumber: String,
  val berthPreference: String = "No Preference",
  val foodPreference: String = "No Food"
) {
  fun toPassenger(): Passenger {
    return Passenger(
      fullName = fullName,
      age = age,
      dob = dob,
      gender = gender,
      mobileNumber = mobileNumber,
      aadhaarNumber = aadhaarNumber,
      panNumber = panNumber,
      berthPreference = berthPreference,
      foodPreference = foodPreference
    )
  }

  companion object {
    fun fromPassenger(p: Passenger): SavedPassengerEntity {
      return SavedPassengerEntity(
        fullName = p.fullName,
        age = p.age,
        dob = p.dob,
        gender = p.gender,
        mobileNumber = p.mobileNumber,
        aadhaarNumber = p.aadhaarNumber,
        panNumber = p.panNumber,
        berthPreference = p.berthPreference,
        foodPreference = p.foodPreference
      )
    }
  }
}
