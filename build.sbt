ThisBuild / version := "0.1.0-SNAPSHOT"

//ThisBuild / scalaVersion := "2.13.10"
ThisBuild / scalaVersion := "2.12.15"

libraryDependencies += "org.apache.spark" %% "spark-core" % "3.3.1"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.3.1"
libraryDependencies += "org.postgresql" % "postgresql" % "42.5.4"

lazy val root = (project in file("."))
  .settings(
    name := "YelpETL"
  )