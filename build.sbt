lazy val baseName  = "FingerTree"
lazy val baseNameL = baseName.toLowerCase

lazy val projectVersion = "1.5.3-SNAPSHOT"
lazy val mimaVersion    = "1.5.1"

lazy val commonSettings = Seq(
  name               := baseName,
  version            := "1.5.3-SNAPSHOT",
  organization       := "de.sciss",
  description        := "A Scala implementation of the versatile purely functional data structure of the same name.",
  homepage           := Some(url(s"https://git.iem.at/sciss/${name.value}")),
  licenses           := Seq("LGPL v2.1+" -> url("http://www.gnu.org/licenses/lgpl-2.1.txt")),
  scalaVersion       := "2.13.0-M5",
  crossScalaVersions := Seq("2.12.8", "2.11.12", "2.13.0-M5"),
  scalacOptions     ++= Seq("-deprecation", "-unchecked", "-feature", "-Xfuture"),
  initialCommands in console := """import de.sciss.fingertree._""",
  libraryDependencies += {
    val v = if (scalaVersion.value == "2.13.0-M5") "3.0.6-SNAP5" else "3.0.5"
    "org.scalatest" %% "scalatest" % v % Test
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
