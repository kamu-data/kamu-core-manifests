scalaVersion := "2.11.12"

organization := "dev.kamu"
organizationName := "kamu"
name := "kamu-core-manifests"
version := "0.1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "org.apache.hadoop" % "hadoop-common" % "2.6.5" % "provided",
  // Config
  "com.github.pureconfig" %% "pureconfig" % "0.10.2" % "provided",
  "com.github.pureconfig" %% "pureconfig-yaml" % "0.10.2" % "provided"
)
