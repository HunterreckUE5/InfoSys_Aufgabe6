ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.17"

val sparkVersion = "4.1.0"

lazy val root = (project in file("."))
  .settings(
    name := "Informationssysteme_Aufgabe6",


    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core" % sparkVersion,
      "org.apache.spark" %% "spark-sql" % sparkVersion
    ),

    fork := true
  )