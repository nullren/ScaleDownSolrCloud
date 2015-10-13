name := "ScaleDownSolrCloud"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "org.apache.curator" % "curator-recipes" % "2.9.0",
  "net.liftweb" %% "lift-json" % "2.6.2",
  "org.logback-extensions" % "logback-ext-loggly" % "0.1.4",
  "com.amazonaws" % "aws-java-sdk" % "1.10.26",
  "ch.qos.logback" % "logback-classic" % "1.1.3",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
  "com.typesafe.akka" %% "akka-actor" % "2.4.0"
)