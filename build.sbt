name               := "FingerTree"

version            := "1.5.1-SNAPSHOT"

organization       := "de.sciss"

description        := "A Scala implementation of the versatile purely functional data structure of the same name."

homepage           := Some(url("https://github.com/Sciss/" + name.value))

licenses           := Seq("LGPL v3+" -> url("http://www.gnu.org/licenses/lgpl-3.0.txt"))

scalaVersion       := "2.11.0-RC3"

crossScalaVersions := Seq("2.11.0-RC3", "2.10.4")

scalacOptions     ++= Seq("-deprecation", "-unchecked", "-feature")

initialCommands in console := """import de.sciss.fingertree._"""

libraryDependencies in ThisBuild ++= Seq(
  "org.scalatest" %% "scalatest" % "2.1.2" % "test"
)

// retrieveManaged := true

// ---- build info ----

buildInfoSettings

sourceGenerators in Compile <+= buildInfo

buildInfoKeys := Seq(name, organization, version, scalaVersion, description,
  BuildInfoKey.map(homepage) { case (k, opt)             => k -> opt.get },
  BuildInfoKey.map(licenses) { case (_, Seq( (lic, _) )) => "license" -> lic }
)

buildInfoPackage := "de.sciss.fingertree"

// ---- publishing ----

publishMavenStyle := true

publishTo :=
  Some(if (version.value endsWith "-SNAPSHOT")
    "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
  else
    "Sonatype Releases"  at "https://oss.sonatype.org/service/local/staging/deploy/maven2"
  )

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra := { val n = name.value
<scm>
  <url>git@github.com:Sciss/{n}.git</url>
  <connection>scm:git:git@github.com:Sciss/{n}.git</connection>
</scm>
<developers>
  <developer>
    <id>sciss</id>
    <name>Hanns Holger Rutz</name>
    <url>http://www.sciss.de</url>
  </developer>
</developers>
}

// ---- ls.implicit.ly ----

seq(lsSettings :_*)

(LsKeys.tags   in LsKeys.lsync) := Seq("data-structure", "tree", "immutable")

(LsKeys.ghUser in LsKeys.lsync) := Some("Sciss")

(LsKeys.ghRepo in LsKeys.lsync) := Some(name.value)
