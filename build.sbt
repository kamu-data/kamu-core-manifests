scalaVersion := "2.11.12"

organization := "dev.kamu"
organizationName := "kamu"
name := "kamu-core-manifests"

libraryDependencies ++= Seq(
  "org.apache.hadoop" % "hadoop-common" % "2.6.5" % "provided",
  // Config
  "com.github.pureconfig" %% "pureconfig" % "0.11.1" % "provided",
  "com.github.pureconfig" %% "pureconfig-yaml" % "0.11.1" % "provided",
  // Testing
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)
