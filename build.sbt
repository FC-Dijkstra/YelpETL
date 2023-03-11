ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

libraryDependencies += "com.typesafe" % "config" % "1.4.2"
libraryDependencies += "org.apache.spark" %% "spark-core" % "3.3.1"
libraryDependencies += "org.apache.spark" %% "spark-sql" % "3.3.1"

lazy val root = (project in file("."))
  .settings(
    name := "YelpETL"
  )