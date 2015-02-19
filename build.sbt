name := "snowplow-kinesis-sns"

version := "1.0"

scalaVersion := "2.11.5"

mainClass := Some("com.wunderlist.snowplow.sns.Application")

resolvers += "Snowplow Analytics Maven repo" at "http://maven.snplow.com/releases/"

libraryDependencies ++= Seq(
  "com.amazonaws" % "aws-java-sdk" % "1.9.17",
  "com.amazonaws" % "amazon-kinesis-client" % "1.2.0",
  "org.slf4j" % "slf4j-simple" % "1.7.10",
  "com.snowplowanalytics" % "collector-payload-1" % "0.0.0",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.5.0",
  "org.specs2" %% "specs2-core" % "2.4.15" % "test"
)

assemblyOutputPath in assembly := file("target/snowplow-kinesis-sns.jar")
