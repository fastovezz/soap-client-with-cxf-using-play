import com.ebiznext.sbt.plugins.CxfWsdl2JavaPlugin.cxf

name := """soap-client-with-cxf-using-play"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"
javacOptions ++= Seq("-source", "1.8", "-target", "1.8")

val cxfVersion: String = "3.1.6"

libraryDependencies ++= Seq(
  "org.apache.cxf" % "cxf-rt-frontend-jaxws" % cxfVersion,
  "org.apache.cxf" % "cxf-rt-transports-http" % cxfVersion,
  "org.springframework" % "spring-context" % "4.2.6.RELEASE",

  "org.apache.commons" % "commons-csv" % "1.2",
  "commons-codec" % "commons-codec" % "1.10",

  "org.webjars.bower" % "angularjs" % "1.5.6",
  "org.webjars.bower" % "angular-route" % "1.5.6",

  "org.webjars.bower" % "jquery" % "2.2.4",
  "org.webjars" % "requirejs" % "2.2.0",
  "org.webjars" % "bootstrap" % "3.3.6",

  // Test dependencies
  "junit" % "junit" % "4.12" % "test",
  "org.assertj" % "assertj-core" % "3.4.1" % "test",
  "org.mockito" % "mockito-core" % "1.10.19" % "test"
)

// CXF wsdl2java configuration
Seq(cxf.settings: _*)
cxf.cxfVersion := cxfVersion
cxf.wsdls := Seq(
  cxf.Wsdl((resourceDirectory in Compile).value / "wsdls/globalweather.wsdl", Seq("-mark-generated", "-p", "com.global.weather"), "globalweather"),
  cxf.Wsdl((resourceDirectory in Compile).value / "wsdls/inbound.wsdl", Seq("-mark-generated", "-p", "com.etadirect.api"), "etadirect")
)
