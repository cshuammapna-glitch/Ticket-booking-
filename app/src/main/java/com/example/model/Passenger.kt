package com.example.model

import org.json.JSONArray
import org.json.JSONObject

data class Passenger(
  val fullName: String,
  val age: Int,
  val dob: String = "",
  val gender: String = "Male", // "Male", "Female", "Transgender"
  val mobileNumber: String = "",
  val aadhaarNumber: String = "",
  val panNumber: String = "",
  val berthPreference: String = "No Preference", // "Lower", "Middle", "Upper", "Side Lower", "Side Upper", "Window", "No Preference"
  val foodPreference: String = "No Food" // "Veg", "Non-Veg", "No Food"
) {
  fun toJson(): JSONObject {
    val obj = JSONObject()
    obj.put("fullName", fullName)
    obj.put("age", age)
    obj.put("dob", dob)
    obj.put("gender", gender)
    obj.put("mobileNumber", mobileNumber)
    obj.put("aadhaarNumber", aadhaarNumber)
    obj.put("panNumber", panNumber)
    obj.put("berthPreference", berthPreference)
    obj.put("foodPreference", foodPreference)
    return obj
  }

  val formattedAadhaar: String
    get() {
      val digits = aadhaarNumber.replace(" ", "").trim()
      return if (digits.length == 12) {
        "${digits.substring(0, 4)} ${digits.substring(4, 8)} ${digits.substring(8, 12)}"
      } else {
        aadhaarNumber
      }
    }

  val maskedAadhaar: String
    get() {
      val digits = aadhaarNumber.replace(" ", "").trim()
      return if (digits.length >= 8) {
        "XXXX XXXX ${digits.takeLast(4)}"
      } else {
        aadhaarNumber
      }
    }

  val maskedPan: String
    get() {
      val p = panNumber.trim().uppercase()
      return if (p.length >= 6) {
        "${p.take(2)}XXXXX${p.takeLast(3)}"
      } else {
        p
      }
    }

  companion object {
    fun fromJson(json: JSONObject): Passenger {
      return Passenger(
        fullName = json.optString("fullName", ""),
        age = json.optInt("age", 25),
        dob = json.optString("dob", ""),
        gender = json.optString("gender", "Male"),
        mobileNumber = json.optString("mobileNumber", ""),
        aadhaarNumber = json.optString("aadhaarNumber", ""),
        panNumber = json.optString("panNumber", ""),
        berthPreference = json.optString("berthPreference", "No Preference"),
        foodPreference = json.optString("foodPreference", "No Food")
      )
    }

    fun listToJsonString(passengers: List<Passenger>): String {
      val array = JSONArray()
      for (p in passengers) {
        array.put(p.toJson())
      }
      return array.toString()
    }

    fun listFromJsonString(jsonString: String): List<Passenger> {
      val list = mutableListOf<Passenger>()
      if (jsonString.isBlank()) return list
      try {
        val array = JSONArray(jsonString)
        for (i in 0 until array.length()) {
          val obj = array.getJSONObject(i)
          list.add(fromJson(obj))
        }
      } catch (e: Exception) {
        // Fallback gracefully
      }
      return list
    }
  }
}
