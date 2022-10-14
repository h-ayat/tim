scalaVersion := "3.2.2"

name := "tim"

libraryDependencies ++= Seq(
  "io.github.h-ayat" %%% "p752-tiles" % "0.3.0",
  "org.scala-native" %%% "junit-runtime" % "0.4.9"
)

testOptions += Tests.Argument(TestFrameworks.JUnit, "-a", "-s", "-v")

addCompilerPlugin(
  "org.scala-native" % "junit-plugin" % "0.4.9" cross CrossVersion.full
)
enablePlugins(ScalaNativePlugin)

nativeConfig ~= {
  _.withIncrementalCompilation(true)
}

scalacOptions ++= Seq("-explain")