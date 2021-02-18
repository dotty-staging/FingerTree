lazy val baseName  = "FingerTree"
lazy val baseNameL = baseName.toLowerCase

lazy val projectVersion = "1.5.5"
lazy val mimaVersion    = "1.5.1"

lazy val deps = new {
  val test = new {
    val scalaTest = "3.2.4"
  }
}

lazy val commonJvmSettings = Seq(
  crossScalaVersions := Seq("3.0.0-RC1", "2.13.4", "2.12.13"),
)

// sonatype plugin requires that these are in global
ThisBuild / version      := projectVersion
ThisBuild / organization := "de.sciss"

lazy val commonSettings = Seq(
  name               := baseName,
//  version            := projectVersion,
//  organization       := "de.sciss",
  description        := "A Scala implementation of the versatile purely functional data structure of the same name.",
  homepage           := Some(url(s"https://git.iem.at/sciss/${name.value}")),
  licenses           := Seq("LGPL v2.1+" -> url("http://www.gnu.org/licenses/lgpl-2.1.txt")),
  scalaVersion       := "2.13.4",
  scalacOptions     ++= Seq("-deprecation", "-unchecked", "-feature", "-Xlint", "-Xsource:2.13"),
  initialCommands in console := """import de.sciss.fingertree._""",
  libraryDependencies += {
    "org.scalatest" %%% "scalatest" % deps.test.scalaTest % Test
  },
  unmanagedSourceDirectories in Compile += {
    val sourceDir0  = (sourceDirectory in Compile).value
    val sourceDir   = file(
      sourceDir0.getPath.replace("/jvm/" , "/shared/").replace("/js/", "/shared/")
    )
    val sv          = CrossVersion.partialVersion(scalaVersion.value)
    sv match {
      case Some((2, n)) if n >= 13 => sourceDir / "scala-2.13+"
      case Some((3, _))            => sourceDir / "scala-2.13+"
      case _                       => sourceDir / "scala-2.13-"
    }
  },
  mimaPreviousArtifacts := Set("de.sciss" %% baseNameL % mimaVersion),
)

lazy val root = crossProject(JSPlatform, JVMPlatform).in(file("."))
  .enablePlugins(BuildInfoPlugin)
  .settings(commonSettings)
  .jvmSettings(commonJvmSettings)
  .settings(publishSettings)
  .settings(
    buildInfoKeys := Seq(name, organization, version, scalaVersion, description,
      BuildInfoKey.map(homepage) { case (k, opt)             => k -> opt.get },
      BuildInfoKey.map(licenses) { case (_, Seq( (lic, _) )) => "license" -> lic }
    ),
    buildInfoPackage := "de.sciss.fingertree"
  )

// ---- publishing ----

lazy val publishSettings = Seq(
  publishMavenStyle := true,
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  developers := List(
    Developer(
      id    = "sciss",
      name  = "Hanns Holger Rutz",
      email = "contact@sciss.de",
      url   = url("https://www.sciss.de")
    )
  ),
  scmInfo := {
    val h = "git.iem.at"
    val a = s"sciss/${name.value}"
    Some(ScmInfo(url(s"https://$h/$a"), s"scm:git@$h:$a.git"))
  },
)

