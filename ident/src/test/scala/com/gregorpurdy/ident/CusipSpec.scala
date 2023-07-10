package com.gregorpurdy.ident

import zio.Chunk
import zio.test.Assertion.*
import zio.test.*

object CusipSpec extends ZIOSpecDefault {

  val cusipString = "037833100"
  val base = "037833"
  val issue = "10"
  val checkDigit = "0"

  def spec: Spec[Any, Any] = suite("CusipSpec")(
    // test("Correctly parse and validate the example AAPL ISIN from the isin.org web site") {

    test("Correctly parse and validate the example AAPL CUSIP") {
      val cusip = Cusip.fromString(cusipString).toOption.get

      assert(cusip.value)(equalTo(cusipString))
      assert(cusip.base)(equalTo(base))
      assert(cusip.issue)(equalTo(issue))
      assert(cusip.checkDigit)(equalTo(checkDigit))
    },
    test("Correctly compute the check digit for the AAPL CUSIP") {
      val cusip = Cusip.fromPartsCalcCheckDigit(base, issue).toOption.get

      assert(cusip.value)(equalTo(cusipString))
      assert(cusip.base)(equalTo(base))
      assert(cusip.issue)(equalTo(issue))
      assert(cusip.checkDigit)(equalTo(checkDigit))
    },
    test("Correctly validate the check digit for AAPL from the isin.org web site") {
      val cusip = Cusip.fromParts(base, issue, checkDigit).toOption.get

      assert(cusip.value)(equalTo(cusipString))
      assert(cusip.base)(equalTo(base))
      assert(cusip.issue)(equalTo(issue))
      assert(cusip.checkDigit)(equalTo(checkDigit))
    },
    test("Correctly parse and validate a real-world CUSIP with a '0' check digit (BCC aka Boise Cascade)") {
      assert(Cusip.fromString("09739D100").toOption.get.toString)(equalTo("09739D100"))
    },
    test("Correctly parse and validate a real-world CUSIP with a '1' check digit (ADBE aka Adobe)") {
      assert(Cusip.fromString("00724F101").toOption.get.toString)(equalTo("00724F101"))
    },
    test("Correctly parse and validate a real-world CUSIP with a '2' check digit (AAL aka American Airlines)") {
      assert(Cusip.fromString("02376R102").toOption.get.toString)(equalTo("02376R102"))
    },
    test("Correctly parse and validate a real-world CUSIP with a '3' check digit (ADP aka Automatic Data Processing)") {
      assert(Cusip.fromString("053015103").toOption.get.toString)(equalTo("053015103"))
    },
    test("Correctly parse and validate a real-world CUSIP with a '4' check digit (IMKTA aka Ingles Markets)") {
      assert(Cusip.fromString("457030104").toOption.get.toString)(equalTo("457030104"))
    },
    test(
      "Correctly parse and validate a real-world CUSIP with a '5' check digit (AJRD aka Aerojet Rocketdyne Holdings)"
    ) {
      assert(Cusip.fromString("007800105").toOption.get.toString)(equalTo("007800105"))
    },
    test("Correctly parse and validate a real-world CUSIP with a '6' check digit (XRX aka Xerox)") {
      assert(Cusip.fromString("98421M106").toOption.get.toString)(equalTo("98421M106"))
    },
    test("Correctly parse and validate a real-world CUSIP with a '7' check digit (AMD aka Advanced Micro Devices)") {
      assert(Cusip.fromString("007903107").toOption.get.toString)(equalTo("007903107"))
    },
    test("Correctly parse and validate a real-world CUSIP with a '8' check digit (VNDA aka Vanda Pharmaceuticals)") {
      assert(Cusip.fromString("921659108").toOption.get.toString)(equalTo("921659108"))
    },
    test("Correctly parse and validate a real-world CUSIP with a '9' check digit (APT aka AlphaProTec)") {
      assert(Cusip.fromString("020772109").toOption.get.toString)(equalTo("020772109"))
    },
    test("Correctly support default Ordering") {
      val xrx = Cusip.fromString("98421M106").toOption.get
      val amd = Cusip.fromString("007903107").toOption.get
      val chunk = Chunk(xrx, amd)
      val sorted = chunk.sorted
      assert(sorted)(equalTo(Chunk(amd, xrx)))
    },
    test("Correctly support pattern matching") {
      val xrx = Cusip.fromString("98421M106").toOption.get
      val amd = Cusip.fromString("007903107").toOption.get
      val chunk = Chunk(xrx, amd).map { case Cusip(v) => v }
      assert(chunk)(equalTo(Chunk("98421M106", "007903107")))
    }
  )

}
