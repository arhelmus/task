name := "config-parser"

version := "0.1"

scalaVersion := "2.12.5"

projectDependencies := Seq(
  "com.google.guava" % "guava" % "24.1-jre",
  "com.chuusai" %% "shapeless" % "2.3.3",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)