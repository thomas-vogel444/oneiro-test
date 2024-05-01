ThisBuild / scalaVersion     := "2.13.12"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.example"
ThisBuild / organizationName := "example"

lazy val root = (project in file("."))
  .settings(
    name := "oneiro-test",
    libraryDependencies ++=
      Seq(
        "org.typelevel" %% "cats-effect" % "3.5.4",
        "org.typelevel" %% "squants" % "1.8.3",
        "org.tpolecat" %% "doobie-core" % "1.0.0-RC4",
        "org.scalamock" %% "scalamock" % "6.0.0" % Test,
        "org.scalatest" %% "scalatest" % "3.2.18" % Test
      )
  )
