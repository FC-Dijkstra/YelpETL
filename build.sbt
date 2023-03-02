ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

libraryDependencies += "com.typesafe" % "config" % "1.4.2"

lazy val root = (project in file("."))
  .settings(
    name := "YelpETL"
  )