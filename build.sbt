lazy val baseName  = "FingerTree"
lazy val baseNameL = baseName.toLowerCase

lazy val projectVersion = "1.5.5"
lazy val mimaVersion    = "1.5.1"

lazy val commonSettings = Seq(
  name               := baseName,
  version            := projectVersion,
  organization       := "de.sciss",
  description        := "A Scala implementation of the versatile purely functional data structure of the same name.",
  homepage           := Some(url(s"https://git.iem.at/sciss/${name.value}")),
  licenses           := Seq("LGPL v2.1+" -> url("http://www.gnu.org/licenses/lgpl-2.1.txt")),
  scalaVersion       := "2.13.3",
  crossScalaVersions := Seq("0.27.0-RC1", "2.13.3", "2.12.12"),
  scalacOptions     ++= Seq("-deprecation", "-unchecked", "-feature", "-Xlint", "-Xsource:2.13"),
  initialCommands in console := """import de.sciss.fingertree._""",
  libraryDependencies += {
    "org.scalatest" %% "scalatest" % "3.2.2" % Test
  },
  unmanagedSourceDirectories in Compile += {
    val sourceDir = (sourceDirectory in Compile).value
    CrossVersion.partialVersion(scalaVersion.value) match {
      case Some((2, n)) if n >= 13 => sourceDir / "scala-2.13+"
      case Some((0, _))            => sourceDir / "scala-2.13+"
      case _                       => sourceDir / "scala-2.13-"
    }
  },
  mimaPreviousArtifacts := Set("de.sciss" %% baseNameL % mimaVersion),
)

lazy val root = project.withId(baseNameL).in(file("."))
  .enablePlugins(BuildInfoPlugin)
  .settings(commonSettings)
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
  publishTo := {
    Some(if (isSnapshot.value)
      "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
    else
      "Sonatype Releases"  at "https://oss.sonatype.org/service/local/staging/deploy/maven2"
    )
  },
  publishArtifact in Test := false,
  pomIncludeRepository := { _ => false },
  pomExtra := { val n = name.value
<scm>
  <url>git@git.iem.at:sciss/{n}.git</url>
  <connection>scm:git:git@git.iem.at:sciss/{n}.git</connection>
</scm>
<developers>
  <developer>
    <id>sciss</id>
    <name>Hanns Holger Rutz</name>
    <url>http://www.sciss.de</url>
  </developer>
</developers>
}
)
