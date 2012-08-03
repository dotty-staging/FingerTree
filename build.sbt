name := "FingerTree"

version := "0.21"

organization := "de.sciss"

description := "A Scala implementation of the versatile purely functional data structure of the same name."

homepage := Some( url( "https://github.com/Sciss/FingerTree" ))

licenses := Seq( "GPL v2+" -> url( "http://www.gnu.org/licenses/gpl-2.0.txt" ))

scalaVersion := "2.9.2"

crossScalaVersions := Seq( "2.10.0-M6", "2.9.2" )

scalacOptions ++= Seq( "-deprecation", "-unchecked" )

initialCommands in console := """import de.sciss.fingertree._"""

resolvers += "Sonatype OSS Releases" at "https://oss.sonatype.org/content/groups/public"

libraryDependencies <+= scalaVersion { sv =>
   val v = sv match {
      case "2.10.0-M6" => "1.9-2.10.0-M6-B2"
      case _ => "1.8"
   }
   "org.scalatest" %% "scalatest" % v % "test"
}

retrieveManaged := true

// ---- publishing ----

publishMavenStyle := true

publishTo <<= version { (v: String) =>
   Some( if( v.endsWith( "-SNAPSHOT" ))
      "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
   else
      "Sonatype Releases"  at "https://oss.sonatype.org/service/local/staging/deploy/maven2"
   )
}

publishArtifact in Test := false

pomIncludeRepository := { _ => false }

pomExtra :=
<scm>
  <url>git@github.com:Sciss/FingerTree.git</url>
  <connection>scm:git:git@github.com:Sciss/FingerTree.git</connection>
</scm>
<developers>
   <developer>
      <id>sciss</id>
      <name>Hanns Holger Rutz</name>
      <url>http://www.sciss.de</url>
   </developer>
</developers>

// ---- ls.implicit.ly ----

seq( lsSettings :_* )

(LsKeys.tags in LsKeys.lsync) := Seq( "data-structure", "tree", "immutable" )

(LsKeys.ghUser in LsKeys.lsync) := Some( "Sciss" )

(LsKeys.ghRepo in LsKeys.lsync) := Some( "FingerTree" )

// bug in ls -- doesn't find the licenses from global scope
(licenses in LsKeys.lsync) := Seq( "GPL v2+" -> url( "http://www.gnu.org/licenses/gpl-2.0.txt" ))
