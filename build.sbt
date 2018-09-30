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

libraryDependencies += "org.nlpcn" % "nlp-lang" % "1.7.7"

libraryDependencies += "org.ansj" % "ansj_seg" % "5.1.6"
