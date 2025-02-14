import sbt._

object Dependencies {
  val CirceVersion = "0.14.10"
  lazy val CirceCore = "io.circe" %% "circe-core" % CirceVersion
  lazy val CirceGeneric = "io.circe" %% "circe-generic" % CirceVersion
  lazy val CirceParser = "io.circe" %% "circe-parser" % CirceVersion

  val LogbackVersion = "1.5.16"
  lazy val Logback = "ch.qos.logback" % "logback-classic" % LogbackVersion

  val ScalaCheckVersion = "1.18.1"
  lazy val ScalaCheck = "org.scalacheck" %% "scalacheck" % ScalaCheckVersion

  val ScalaTestVersion = "3.2.19"
  lazy val ScalaTest = "org.scalatest" %% "scalatest" % ScalaTestVersion

  val Slf4JVersion = "2.0.16"
  lazy val Slf4JApi = "org.slf4j" % "slf4j-api" % Slf4JVersion
  lazy val JclOverSlf4J = "org.slf4j" % "jcl-over-slf4j" % Slf4JVersion
  lazy val Log4JOverSlf4J = "org.slf4j" % "log4j-over-slf4j" % Slf4JVersion
  lazy val JulToSlf4J = "org.slf4j" % "jul-to-slf4j" % Slf4JVersion

  val ZioConfigVersion = "4.0.3"
  lazy val ZioConfig = "dev.zio" %% "zio-config" % ZioConfigVersion
  lazy val ZioConfigMagnolia = "dev.zio" %% "zio-config-magnolia" % ZioConfigVersion

  val ZioJsonVersion = "0.7.11"
  lazy val ZioJson = "dev.zio" %% "zio-json" % ZioJsonVersion

  val ZioSchemaVersion = "1.6.1"
  lazy val ZioSchema = "dev.zio" %% "zio-schema" % ZioSchemaVersion
  lazy val ZioSchemaDerivation = "dev.zio" %% "zio-schema-derivation" % ZioSchemaVersion
  lazy val ZioSchemaJson = "dev.zio" %% "zio-schema-json" % ZioSchemaVersion

  val ZioVersion = "2.1.14"
  lazy val Zio = "dev.zio" %% "zio" % ZioVersion
  lazy val ZioTest = "dev.zio" %% "zio-test" % ZioVersion
  lazy val ZioTestMagnolia = "dev.zio" %% "zio-test-magnolia" % ZioVersion
  lazy val ZioTestSbt = "dev.zio" %% "zio-test-sbt" % ZioVersion
}
