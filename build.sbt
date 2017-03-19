name := """TwitchPlaysHearthStone"""

version := "1.0"

scalaVersion := "2.11.8"

lazy val akkaVersion = "2.4.1"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4"

libraryDependencies += "net.java.dev.jna" % "jna" % "4.2.1"

libraryDependencies += "net.java.dev.jna" % "jna-platform" % "4.2.1"

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.2"

libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.6"

libraryDependencies += "com.typesafe.akka" % "akka-slf4j_2.11" % "2.4.1"

libraryDependencies += "net.java.dev.jna" % "jna" % "4.2.1"

libraryDependencies += "net.java.dev.jna" % "jna-platform" % "4.2.1"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"

libraryDependencies += "pircbot" % "pircbot" % "1.5.0"

libraryDependencies += "commons-io" % "commons-io" % "2.5"

libraryDependencies += "org.scalafx" %% "scalafx" % "8.0.92-R10"

addCompilerPlugin("org.scalamacros" % "paradise_2.11.8" % "2.1.0")

libraryDependencies += "org.scalafx" %% "scalafxml-core-sfx8" % "0.3"

libraryDependencies += "net.liftweb" %% "lift-json" % "2.6+"

fork in run := true
