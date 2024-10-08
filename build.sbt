ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.5.0"

lazy val root = (project in file(".")).settings(name := "top10words")

libraryDependencies ++= Seq(
    "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.4",
    "com.lihaoyi" %% "upickle" % "4.0.2",
    "org.scalatest" %% "scalatest" % "3.2.18" % "test"
)
