name := "fizzbuzz-backend"

version := "1.0.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.7"

libraryDependencies ++= Seq(
  guice,
  jdbc,
  evolutions,
  "net.codingwell" %% "scala-guice" % "4.2.1",
  "org.playframework.anorm" %% "anorm" % "2.6.2",
  "org.playframework.anorm" %% "anorm-postgres" % "2.6.2",
  "org.postgresql" % "postgresql" % "42.2.5",
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % "test"
)