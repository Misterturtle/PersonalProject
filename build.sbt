name := """TwitchPlaysHearthStone"""

version := "1.0"

scalaVersion := "2.11.7"

lazy val akkaVersion = "2.4.1"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4"

libraryDependencies += "net.java.dev.jna" % "jna" % "4.2.1"

libraryDependencies += "net.java.dev.jna" % "jna-platform" % "4.2.1"

libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaVersion

libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % akkaVersion

libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.1.2"

libraryDependencies += "org.slf4j" % "slf4j-api" % "1.7.6"

libraryDependencies += "com.typesafe.akka" % "akka-slf4j_2.11" % "2.4.1"

libraryDependencies += "net.java.dev.jna" % "jna" % "4.2.1"

libraryDependencies += "net.java.dev.jna" % "jna-platform" % "4.2.1"

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0"

libraryDependencies += "pircbot" % "pircbot" % "1.5.0"

fork in run := true
