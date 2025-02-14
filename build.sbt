import Dependencies._

val Scala3Version = "3.6.3"

ThisBuild / scalaVersion := Scala3Version

ThisBuild / organization := "com.gregorpurdy"
ThisBuild / version := "0.4.0-SNAPSHOT"
ThisBuild / versionScheme := Some("early-semver")
ThisBuild / organizationName := "Gregor Purdy"
ThisBuild / organizationHomepage := Some(url("https://github.com/gnp"))
ThisBuild / description := "Scala classes for representing syntactically valid identifiers of various kinds."
ThisBuild / startYear := Some(2023)
ThisBuild / licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0"))
ThisBuild / homepage := Some(url("https://github.com/gnp/ident"))
ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/gnp/ident"),
    "scm:git@github.com:gnp/ident.git"
  )
)
ThisBuild / developers := List(
  Developer(
    "gnp",
    "Gregor Purdy",
    "gregor@abcelo.com",
    url("http://github.com/gnp")
  )
)

// Remove all additional repository other than Maven Central from POM
ThisBuild / sonatypeCredentialHost := "s01.oss.sonatype.org"
ThisBuild / pomIncludeRepository := { _ => false }
ThisBuild / publishTo := sonatypePublishToBundle.value

ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision

addCommandAlias(
  "check",
  "; scalafmtSbtCheck; scalafmtCheckAll; scalafixAll --check; doc"
)

addCommandAlias(
  "generateReadme",
  "; project docs; set mdocIn := file(\"docs/index.md\"); set mdocOut := file(\"README.md\"); doc / mdoc"
)

val stdCompilerOptions3 = Seq(
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-encoding",
  "utf-8", // Specify character encoding used by source files.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-Wunused:explicits", // Warn if an explicit parameter is unused.
//  "-Wunused:implicits", // Warn if an implicit parameter is unused.
  "-Wunused:imports", // Warn if an import selector is not referenced.
  "-Wunused:linted", // -Xlint:unused.
  "-Wunused:locals", // Warn if a local definition is unused.
  "-Wunused:params", // Enable -Wunused:explicits,implicits.
  "-Wunused:privates", // Warn if a private member is unused.
  "-Wvalue-discard", // Warn when non-Unit expression results are unused.
  "-Xfatal-warnings"
)

lazy val root = (project in file("."))
  .aggregate(
    ident,
    identCirce,
    identZioConfig,
    identZioJson,
    identZioSchema,
    bench,
    examples
  )
  .settings(
    name := "root",
    crossScalaVersions := Nil, // To avoid "double publishing"
    publish := {},
    publish / skip := true,
    publishLocal := {}
  )

lazy val docs = project
  .in(file("ident-docs"))
  .settings(
    mdocVariables := Map(
      "VERSION" -> version.value.replaceAll("-SNAPSHOT$", "")
    )
  )
  .dependsOn(ident)
  .enablePlugins(MdocPlugin)

lazy val examples = (project in file("examples"))
  .dependsOn(ident, identCirce, identZioConfig, identZioJson, identZioSchema)
  .settings(
    name := "examples",
    crossScalaVersions := Nil,
    publish := {},
    publish / skip := true,
    publishLocal := {},
    scalacOptions := stdCompilerOptions3,
    libraryDependencies ++= Seq(
      CirceCore % Compile,
      CirceGeneric % Compile,
      CirceParser % Compile,
      ZioSchemaJson % Compile
    )
  )

lazy val bench = (project in file("bench"))
  .dependsOn(ident)
  .enablePlugins(JmhPlugin)
  .settings(
    name := "bench",
    crossScalaVersions := Nil,
    publish := {},
    publish / skip := true,
    publishLocal := {},
    scalacOptions := stdCompilerOptions3,
    libraryDependencies ++= Seq(
      ScalaCheck % Compile
    )
  )

lazy val ident = (project in file("ident"))
  .settings(
    name := "ident",
    crossScalaVersions := Nil,
    scalacOptions := stdCompilerOptions3,
    libraryDependencies ++= Seq(
      ScalaTest % Test,
      ScalaCheck % Test,
      Slf4JApi % Test,
      JclOverSlf4J % Test,
      Log4JOverSlf4J % Test,
      JulToSlf4J % Test,
      Logback % Test
    )
      .map(_.exclude("commons-logging", "commons-logging"))
      .map(_.exclude("log4j", "log4j"))
      .map(_.exclude("org.slf4j", "slf4j-log4j12"))
  )

lazy val identCirce = (project in file("ident-circe"))
  .dependsOn(ident)
  .settings(
    name := "ident-circe",
    crossScalaVersions := Nil,
    scalacOptions := stdCompilerOptions3,
    libraryDependencies ++= Seq(
      CirceCore % Compile,
      ScalaTest % Test,
      Slf4JApi % Test,
      JclOverSlf4J % Test,
      Log4JOverSlf4J % Test,
      JulToSlf4J % Test,
      CirceParser % Test,
      Logback % Test
    )
      .map(_.exclude("commons-logging", "commons-logging"))
      .map(_.exclude("log4j", "log4j"))
      .map(_.exclude("org.slf4j", "slf4j-log4j12"))
  )

lazy val identZioConfig = (project in file("ident-zio-config"))
  .dependsOn(ident)
  .settings(
    name := "ident-zio-config",
    crossScalaVersions := Nil,
    scalacOptions := stdCompilerOptions3,
    libraryDependencies ++= Seq(
      ZioConfig % Compile,
      ZioConfigMagnolia % Compile,
      Slf4JApi % Test,
      JclOverSlf4J % Test,
      Log4JOverSlf4J % Test,
      JulToSlf4J % Test,
      Logback % Test,
      ZioTest % Test,
      ZioTestMagnolia % Test,
      ZioTestSbt % Test
    )
      .map(_.exclude("commons-logging", "commons-logging"))
      .map(_.exclude("log4j", "log4j"))
      .map(_.exclude("org.slf4j", "slf4j-log4j12")),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )

lazy val identZioJson = (project in file("ident-zio-json"))
  .dependsOn(ident)
  .settings(
    name := "ident-zio-json",
    crossScalaVersions := Nil,
    scalacOptions := stdCompilerOptions3,
    libraryDependencies ++= Seq(
      ZioJson % Compile,
      Slf4JApi % Test,
      JclOverSlf4J % Test,
      Log4JOverSlf4J % Test,
      JulToSlf4J % Test,
      Logback % Test,
      ZioTest % Test,
      ZioTestMagnolia % Test,
      ZioTestSbt % Test
    )
      .map(_.exclude("commons-logging", "commons-logging"))
      .map(_.exclude("log4j", "log4j"))
      .map(_.exclude("org.slf4j", "slf4j-log4j12")),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )

lazy val identZioSchema = (project in file("ident-zio-schema"))
  .dependsOn(ident)
  .settings(
    name := "ident-zio-schema",
    crossScalaVersions := Nil,
    scalacOptions := stdCompilerOptions3,
    libraryDependencies ++= Seq(
      ZioSchema % Compile,
      ZioSchemaDerivation % Compile,
      ZioSchemaJson % Test,
      Slf4JApi % Test,
      JclOverSlf4J % Test,
      Log4JOverSlf4J % Test,
      JulToSlf4J % Test,
      Logback % Test,
      ZioTest % Test,
      ZioTestMagnolia % Test,
      ZioTestSbt % Test
    )
      .map(_.exclude("commons-logging", "commons-logging"))
      .map(_.exclude("log4j", "log4j"))
      .map(_.exclude("org.slf4j", "slf4j-log4j12")),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
