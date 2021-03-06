scalaVersion := "2.11.7"

organization := "com.evojam"

name := "scala-common"

scalacOptions ++= Seq(
  "-target:jvm-1.7",
  "-encoding", "UTF-8",
  "-deprecation",
  "-feature",
  "-unchecked",
  "-language:postfixOps",
  "-Xlint",
  "-Ywarn-adapted-args",
  "-Ywarn-value-discard",
  "-Ywarn-inaccessible",
  "-Ywarn-dead-code",
  "-Xfatal-warnings"
)

licenses +=("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.html"))

resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

resolvers ++= Seq(
  Resolver.mavenLocal,
  Resolver.sbtPluginRepo("snapshots"),
  Resolver.sonatypeRepo("snapshots"),
  Resolver.typesafeRepo("snapshots"),
  Resolver.typesafeRepo("releases"),
  Resolver.typesafeIvyRepo("releases")
)

val playV = "2.4.6"

libraryDependencies ++= Seq(
  "com.sksamuel.elastic4s" %% "elastic4s-core" % "1.6.6",
  "com.typesafe.play" %% "play-json" % playV,
  "com.typesafe.play" %% "play-ws" % playV,
  "net.codingwell" %% "scala-guice" % "4.0.1",
  "org.ocpsoft.prettytime" % "prettytime-nlp" % "3.2.7.Final",
  "org.scalanlp" %% "epic" % "0.3.1"
)

