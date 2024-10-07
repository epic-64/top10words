ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.5.0"

lazy val root = (project in file(".")).settings(name := "top10words")

libraryDependencies ++= Seq(
    "org.scala-lang.modules" %% "scala-parallel-collections" % "1.0.4",
    "com.lihaoyi" %% "upickle" % "4.0.2",
    "org.scalatest" %% "scalatest" % "3.2.18" % "test",
    "org.scalafx" %% "scalafx" % "22.0.0-R33",
    "com.googlecode.lanterna" % "lanterna" % "3.1.2"
)
