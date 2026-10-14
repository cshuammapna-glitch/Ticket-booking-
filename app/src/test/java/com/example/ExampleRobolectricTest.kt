package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.Passenger
import com.example.util.QrCodeGenerator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("TatkalSeva", appName)
  }

  @Test
  fun `passenger serialization and formatting`() {
    val p = Passenger(
      fullName = "Suman Besra",
      age = 30,
      dob = "10/05/1996",
      gender = "Male",
      mobileNumber = "9709803741",
      aadhaarNumber = "548291823741",
      panNumber = "ABCDE1234F"
    )

    assertEquals("5482 9182 3741", p.formattedAadhaar)
    assertEquals("XXXX XXXX 3741", p.maskedAadhaar)
    assertEquals("ABXXXXX34F", p.maskedPan)

    val json = p.toJson()
    val restored = Passenger.fromJson(json)
    assertEquals(p.fullName, restored.fullName)
    assertEquals(p.aadhaarNumber, restored.aadhaarNumber)
    assertEquals(p.panNumber, restored.panNumber)
  }

  @Test
  fun `upi qr uri generation with user upi id`() {
    val upiId = "9709803741@mbk"
    val uri = QrCodeGenerator.buildUpiUri(upiId, "TatkalSeva", 1650.0, "TicketBooking")
    assertTrue(uri.contains("pa=9709803741@mbk"))
    assertTrue(uri.contains("am=1650.00"))

    val bitmap = QrCodeGenerator.generateUpiQrBitmap(upiId, "TatkalSeva", 1650.0, "TicketBooking", 200)
    assertNotNull(bitmap)
    assertEquals(200, bitmap.width)
    assertEquals(200, bitmap.height)
  }
}
