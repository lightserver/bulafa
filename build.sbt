import org.scalajs.sbtplugin.ScalaJSPlugin.autoImport._
import sbt.Keys._
import sbt._

scalaJSStage in Global := FastOptStage

skip in packageJSDependencies := false

val app = crossProject.settings(
  scalaVersion := "2.11.8",
  name := "bulafa",
  version := "0.1",
  unmanagedSourceDirectories in Compile +=
    baseDirectory.value / "shared" / "main" / "scala",

  libraryDependencies ++= Seq(
    "pl.setblack.lsa" %%% "cataracta" % "0.98",
    "pl.setblack" %%% "cryptotpyrc" % "0.4",
    "biz.enef" %%% "slogging" % "0.4.0"
  ),
  testFrameworks += new TestFramework("utest.runner.Framework")

).jsSettings(


  libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.9.0",
    "com.github.japgolly.scalajs-react" %%% "core" % "0.11.1",
    "com.github.japgolly.scalajs-react" %%% "extra" % "0.11.1"

  ),
  // React itself (react-with-addons.js can be react.js, react.min.js, react-with-addons.min.js)
  jsDependencies ++= Seq(

    "org.webjars.bower" % "react" % "15.2.1"
      / "react-with-addons.js"
      minified "react-with-addons.min.js"
      commonJSName "React",

    "org.webjars.bower" % "react" % "15.2.1"
      / "react-dom.js"
      minified "react-dom.min.js"
      dependsOn "react-with-addons.js"
      commonJSName "ReactDOM",

    "org.webjars.bower" % "react" % "15.2.1"
      / "react-dom-server.js"
      minified "react-dom-server.min.js"
      dependsOn "react-dom.js"
      commonJSName "ReactDOMServer",
    "org.webjars" % "cryptojs" % "3.1.2" / "rollups/sha1.js"),

  skip in packageJSDependencies := false, // creates app-jsdeps.js with the react JS lib inside
  persistLauncher in Compile := true
).jvmSettings(
  libraryDependencies ++= Seq(
    "com.typesafe.akka" %% "akka-actor" % "2.4.1",
    "com.typesafe.akka" %% "akka-remote" % "2.4.1",
    "org.scalaz" %% "scalaz-core" % "7.1.2",
    "biz.enef" %% "slogging-slf4j" % "0.4.0",
   "org.apache.logging.log4j" % "log4j-slf4j-impl" % "2.7",
    "org.apache.logging.log4j" % "log4j-api" % "2.7",
    "org.apache.logging.log4j" % "log4j-core" % "2.7",
    "com.typesafe.akka" %% "akka-http-experimental" % "2.0.1",
    "com.github.pathikrit" %% "better-files-akka" % "2.16.0",
    "org.scalatest" %% "scalatest" % "3.0.0" % "test"
  )
)

lazy val appJS = app.js.settings(
  resourceDirectory in Compile <<= baseDirectory(_ / "../shared/src/main/resources")

)

lazy val appJVM = app.jvm.settings(

  resourceDirectory in Compile <<= baseDirectory(_ / "../shared/src/main/resources"),

  unmanagedResourceDirectories in Compile <+= baseDirectory(_ / "../jvm/src/main/resources"),
  resourceGenerators in Compile <+= Def.task {

    val mui = baseDirectory(_ / "../../web/.tmp").value
    val muiFiles = (mui ** ("*.js" || "*.css" || "*.eot" || "*.svg" || "*.svg" || "*.ttf" || "*.woff" || "*.html" || "*.png")).filter(_.isFile).get
    import Path.rebase
    val mappings = muiFiles pair rebase(Seq(mui), (resourceManaged in Compile).value / "web")
    IO.copy(mappings, true)
    mappings.map(_._2)
  }
).enablePlugins(JavaAppPackaging)
