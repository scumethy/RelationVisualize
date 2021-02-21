name := "RelationVisualize"

version := "0.1"

scalaVersion := "2.13.4"

idePackagePrefix := Some("scumethy.visualize.fx")

scalacOptions ++= Seq("-unchecked", "-deprecation", "-Xcheckinit", "-encoding", "utf8")

// Add dependency on ScalaFX library
libraryDependencies += "org.scalafx" %% "scalafx" % "14-R19"
// Add dep on JGraphT
libraryDependencies += "org.jgrapht" % "jgrapht-core" % "1.5.0"

// Add OS specific JavaFX dependencies
val javafxModules = Seq("base", "controls", "fxml", "graphics", "media", "swing", "web")
val osName = System.getProperty("os.name") match {
    case n if n.startsWith("Linux") => "linux"
    case n if n.startsWith("Mac") => "mac"
    case n if n.startsWith("Windows") => "win"
    case _ => throw new Exception("Unknown platform!")
}
libraryDependencies ++= javafxModules.map(m => "org.openjfx" % s"javafx-$m" % "14.0.1" classifier osName)

mainClass in assembly := Some("scumethy.visualize.fx")
assemblyJarName in assembly := "utils.jar"

assemblyMergeStrategy in assembly := {
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case x => MergeStrategy.first
}

// Fork a new JVM for 'run' and 'test:run', to avoid JavaFX double initialization problems
fork := true