import sbtcrossproject.CrossProject
import sbtcrossproject.CrossType
import sbtcrossproject.Platform

/// variables

val groupId = "$group_id$"
val projectName = "$name$"
val rootPkg = s"$groupId.${projectName.replace("-", "")}"
val gitHubOwner = "$github_owner$"

val moduleCrossPlatformMatrix: Map[String, List[Platform]] = Map(
  "core" -> List(JVMPlatform)
)

/// projects

lazy val root = project
  .in(file("."))
  .aggregate(coreJVM)
  .aggregate(readme)
  .settings(commonSettings)
  .settings(noPublishSettings)

lazy val core = myCrossProject("core")
  .settings()

lazy val coreJVM = core.jvm

lazy val readme = project
  .in(file("modules/readme"))
  .settings(commonSettings)
  .settings(noPublishSettings)
  .settings(
    scalacOptions -= "-Ywarn-unused:imports"
  )

/// settings

def myCrossProject(name: String): CrossProject =
  CrossProject(name, file(name))(moduleCrossPlatformMatrix(name): _*)
    .crossType(CrossType.Pure)
    .withoutSuffixFor(JVMPlatform)
    .in(file(s"modules/$name"))
    .settings(moduleName := s"$projectName-$name")
    .settings(commonSettings)

lazy val commonSettings = Def.settings(
  compileSettings,
  metadataSettings,
  scaladocSettings,
  initialCommands += s"""
    import $rootPkg._
  """
)

lazy val compileSettings = Def.settings()

lazy val metadataSettings = Def.settings(
  name := projectName,
  organization := groupId,
  homepage := Some(url(s"https://github.com/$gitHubOwner/$projectName")),
  startYear := Some(2018),
  licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
  headerLicense := Some(HeaderLicense.ALv2("2018", s"$projectName contributors")),
  developers := List(
    Developer(
      id = "fthomas",
      name = "Frank S. Thomas",
      email = "",
      url(s"https://github.com/fthomas")
    )
  )
)

lazy val noPublishSettings = Def.settings(
  skip in publish := true
)

lazy val scaladocSettings = Def.settings()

/// commands

def addCommandsAlias(name: String, cmds: Seq[String]) =
  addCommandAlias(name, cmds.mkString(";", ";", ""))

addCommandsAlias(
  "validate",
  Seq(
    "clean",
    "headerCheck",
    "scalafmtCheck",
    "scalafmtSbtCheck",
    "test:scalafmtCheck",
    "coverage",
    "test",
    "coverageReport",
    "doc",
    "package",
    "packageSrc"
  )
)

addCommandsAlias(
  "formatAll",
  Seq(
    "headerCreate",
    "scalafmt",
    "test:scalafmt",
    "scalafmtSbt"
  )
)
