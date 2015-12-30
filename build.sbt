name := """TwitchPlaysHearthStone"""

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.4" % "test"

libraryDependencies += "net.java.dev.jna" % "jna" % "4.2.1"

libraryDependencies += "net.java.dev.jna" % "jna-platform" % "4.2.1"

fork in run := true
