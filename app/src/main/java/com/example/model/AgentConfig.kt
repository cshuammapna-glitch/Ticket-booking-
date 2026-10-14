package com.example.model

data class AgentConfig(
  val agentName: String = "Tatkal Seva Booking",
  val upiId: String = "9709803741@mbk",
  val payeeName: String = "Tatkal Seva Booking",
  val agentPhone: String = "9709803741",
  val tatkalServiceFee: Double = 250.0,
  val generalServiceFee: Double = 120.0,
  val busServiceFee: Double = 90.0
)
