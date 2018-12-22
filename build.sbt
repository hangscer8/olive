name := "olive"

version := "0.1"

scalaVersion := "2.12.6"

unmanagedJars in Compile ++= Seq(
  file(baseDirectory.value + "/lib/jcseg-analyzer-2.3.0.jar"),
  file(baseDirectory.value + "/lib/jcseg-core-2.3.0.jar")
)

libraryDependencies += "org.apache.lucene" % "lucene-core" % "7.4.0"

libraryDependencies += "org.apache.lucene" % "lucene-analyzers-common" % "7.4.0"


libraryDependencies += "org.apache.lucene" % "lucene-queryparser" % "7.4.0"

libraryDependencies += "org.apache.lucene" % "lucene-highlighter" % "7.4.0"

libraryDependencies ++= Seq(
  "org.openjfx" % "javafx-controls" % "11.0.1",
  "com.jfoenix" % "jfoenix" % "8.0.8",
  "org.typelevel" %% "cats-effect" % "1.0.0",
  "com.lihaoyi" %% "fastparse" % "2.0.4",
  "com.typesafe.akka" %% "akka-actor" % "2.5.17",
  "com.typesafe.slick" %% "slick" % "3.2.3",
  "com.h2database" % "h2" % "1.4.195",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.2.3",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.9.7"
)
