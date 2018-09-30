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
  "com.typesafe.akka" %% "akka-http" % "10.1.5",
  "com.typesafe.akka" %% "akka-cluster" % "2.5.17",
  "com.typesafe.akka" %% "akka-distributed-data" % "2.5.17",
  "com.typesafe.akka" %% "akka-http-testkit" % "10.1.5" % Test,
  "com.typesafe.akka" %% "akka-stream" % "2.5.17",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.5.17" % Test
)
libraryDependencies += "com.lihaoyi" %% "requests" % "0.1.5"

