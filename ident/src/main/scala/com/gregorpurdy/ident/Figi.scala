/*
 * Copyright 2023-2025 Gregor Purdy
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gregorpurdy.ident

import com.gregorpurdy.ccs.Modulus10DoubleAddDouble

import scala.util.CommandLineParser
import scala.util.matching.Regex

/** @see
  *   https://en.wikipedia.org/wiki/Financial_Instrument_Global_Identifier
  *
  * NOTE: Wikipedia page mentions the provider can be any two letters except "BS, BM, GG, GB, GH, KY, VG" but I could
  * not find anything about that on the openfigi web site.
  *
  * @see
  *   https://www.omg.org/spec/FIGI/1.0/PDF
  *
  * NOTE: Section 2.1, page 7 says of the first two digits: "Qualification: positions 1 and 2 cannot be the following
  * sequences: BS, BM, GG, GB, VG.". Later in Section 6.1.2, page 16 says "BS, BM, GG, GB, GH, KY, VG" are forbidden, as
  * does the Wikipedia page. This same list comes up again in Section 8.2.4, page 34-35.
  *
  * @see
  *   https://www.openfigi.com/about/figi
  * @see
  *   https://www.openfigi.com/assets/content/figi-check-digit-2173341b2d.pdf
  * @see
  *   https://www.openfigi.com/assets/local/figi-allocation-rules.pdf
  */
final case class Figi private (value: String) {

  def provider: String = value.substring(0, 2)

  def scope: String = value.substring(2, 3)

  def id: String = value.substring(3, 11)

  def checkDigit: String = value.substring(11, 12)

  override def toString: String = value

  def toStringTagged: String = s"figi:$value"

}

object Figi {

  val providerFormat: Regex = "[B-DF-HJ-NP-TV-Z0-9]{2}".r
  val providerExclusions: Set[String] = Set("BS", "BM", "GG", "GB", "GH", "KY", "VG")
  val scopeFormat: Regex = "G".r
  val idFormat: Regex = "[B-DF-HJ-NP-TV-Z0-9]{8}".r
  val checkDigitFormat: Regex = "[0-9]".r
  val figiFormat: Regex = "([B-DF-HJ-NP-TV-Z0-9]{2})(G)([B-DF-HJ-NP-TV-Z0-9]{8})([0-9])".r

  given Ordering[Figi] = Ordering.by(_.value)

  object FigiCommandLineParserFromString extends CommandLineParser.FromString[Figi] {
    def fromString(s: String): Figi = Figi.fromString(s) match {
      case Left(s)      => throw new IllegalArgumentException(s)
      case Right(ident) => ident
    }
    override def fromStringOption(s: String): Option[Figi] = Figi.fromString(s).toOption
  }

  given CommandLineParser.FromString[Figi] = FigiCommandLineParserFromString

  def calculateCheckDigit(
      provider: String,
      scope: String,
      id: String
  ): String = {
    val tempProvider = normalize(provider)
    val tempScope = normalize(scope)
    val tempId = normalize(id)

    if (!isValidProviderFormatStrict(tempProvider))
      throw new IllegalArgumentException(
        s"Format of provider '$provider' is not valid"
      )

    if (!isValidScopeFormatStrict(tempScope))
      throw new IllegalArgumentException(
        s"Format of scope '$scope' is not valid"
      )

    if (!isValidIdFormatStrict(tempId))
      throw new IllegalArgumentException(
        s"Format of id '$id' is not valid"
      )

    calculateCheckDigitInternal(tempProvider, tempScope, tempId)
  }

  /** This method is used internally when the base and issue have already been validated to be the right format.
    */
  private def calculateCheckDigitInternal(
      provider: String,
      scope: String,
      id: String
  ): String =
    Modulus10DoubleAddDouble.CusipVariant.calculate(s"$provider$scope$id")

  def isValidProviderFormatStrict(string: String): Boolean =
    providerFormat.matches(string) && !providerExclusions.contains(string)

  def isValidProviderFormatLoose(string: String): Boolean =
    isValidProviderFormatStrict(normalize(string))

  def isValidScopeFormatStrict(string: String): Boolean =
    scopeFormat.matches(string)

  def isValidScopeFormatLoose(string: String): Boolean =
    isValidScopeFormatStrict(normalize(string))

  def isValidIdFormatStrict(string: String): Boolean =
    idFormat.matches(string)

  def isValidIdFormatLoose(string: String): Boolean =
    isValidIdFormatStrict(normalize(string))

  def isValidCheckDigitFormatStrict(string: String): Boolean =
    checkDigitFormat.matches(string)

  def isValidCheckDigitFormatLoose(string: String): Boolean =
    isValidCheckDigitFormatStrict(normalize(string))

  /** This will only return true if the input String has no whitespace, all letters are already uppercase, the length is
    * 12 and each component is the right mix of letters, digits and/or special characters. It does enforce provider
    * exclusions, but it does not validate the check digit.
    *
    * [[fromString]] is more permissive, because it will trim leading and/or trailing whitespace and convert to
    * uppercase before validating the CUSIP.
    */
  def isValidFormatStrict(string: String): Boolean =
    figiFormat.matches(string) && !providerExclusions.contains(string.substring(0, 2))

  /** This returns true if the input String would be allowed as an argument to [[fromString]]. It does not validate the
    * check digit.
    */
  def isValidFormat(string: String): Boolean =
    isValidFormatStrict(normalize(string))

  def fromParts(
      provider: String,
      scope: String,
      id: String,
      checkDigit: String
  ): Either[String, Figi] = {
    val tempProvider = normalize(provider)
    val tempScope = normalize(scope)
    val tempId = normalize(id)
    val tempCheckDigit = normalize(checkDigit)

    if (!isValidProviderFormatStrict(tempProvider))
      Left(s"Format of provider '$provider' is not valid")
    else if (!isValidScopeFormatStrict(tempScope))
      Left(s"Format of scope '$scope' is not valid")
    else if (!isValidIdFormatStrict(tempId))
      Left(s"Format of id '$id' is not valid")
    else if (!isValidCheckDigitFormatStrict(tempCheckDigit))
      Left(s"Format of check digit '$checkDigit' is not valid")
    else {
      val correctCheckDigit = calculateCheckDigitInternal(provider, scope, id)
      if (tempCheckDigit != correctCheckDigit)
        Left(
          s"Check digit '$checkDigit' is not correct for provider '$provider', scope '$scope' and id '$id'. It should be '$correctCheckDigit'"
        )
      else
        Right(new Figi(s"$provider$scope$id$tempCheckDigit"))
    }
  }

  /** Create a FIGI from a provider, scope and id, computing the correct check digit automatically.
    */
  def fromPartsCalcCheckDigit(provider: String, scope: String, id: String): Either[String, Figi] = {
    val tempProvider = normalize(provider)
    val tempScope = normalize(scope)
    val tempId = normalize(id)

    if (!isValidProviderFormatStrict(tempProvider))
      Left(s"Format of provider '$provider' is not valid")
    else if (!isValidScopeFormatStrict(tempScope))
      Left(s"Format of scope '$scope' is not valid")
    else if (!isValidIdFormatStrict(tempId))
      Left(s"Format of id '$id' is not valid")
    else {
      val correctCheckDigit = calculateCheckDigitInternal(provider, scope, id)
      Right(new Figi(s"$provider$scope$id$correctCheckDigit"))
    }
  }

  def fromString(value: String): Either[String, Figi] =
    normalize(value) match {
      case figiFormat(provider, scope, id, checkDigit) =>
        fromParts(provider, scope, id, checkDigit)
      case _ =>
        Left(s"Input string is not in valid FIGI format: '$value'")
    }

}
