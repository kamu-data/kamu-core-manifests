scalaVersion := "2.11.12"

organization := "dev.kamu"
organizationName := "kamu"
name := "kamu-core-manifests"
version := "0.1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "org.apache.hadoop" % "hadoop-common" % "3.2.0"
)
