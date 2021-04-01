lazy val scalaZioPlayground = (project in file(".")).settings(
  name := "scala-zio-playground",
  version := "1.0",
  scalaVersion := "2.12.3",
  libraryDependencies ++= Seq(
    "com.typesafe"  % "config" % "1.3.1",
    "org.typelevel" %% "cats-core" % "2.3.0",
    "dev.zio"       %% "zio" % "1.0.5",
    "org.scalamock" %% "scalamock" % "4.4.0" % Test,
    "org.scalatest" %% "scalatest" % "3.0.4" % Test
  )
)
