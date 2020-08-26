import com.typesafe.sbt.SbtGit
import sbt.Keys._
import sbt._

object Common {
  val defaultSettings = Seq(
    SbtGit.showCurrentGitBranch,
    libraryDependencies ++= Dependencies.Test.Minimal
//    publishTo := {
//      if (isSnapshot.value) {
//        Some("Artifactory Realm" at "https://thegreendata.jfrog.io/thegreendata/sbt-dev-local;build.timestamp=" + new java.util.Date().getTime)
//      } else {
//        Some("Artifactory Realm" at "https://thegreendata.jfrog.io/thegreendata/sbt-release-local")
//      }
//    },
//    resolvers ++= {
//        if (isSnapshot.value) {
//          Seq(
//            "Artifactory Dev" at "https://thegreendata.jfrog.io/thegreendata/sbt-dev/"
//          )
//        }
//        else {
//          Seq()
//        }
//      } ++
//      Seq(
//        "Artifactory Release" at "https://thegreendata.jfrog.io/thegreendata/sbt-release/"
//      ),
//    credentials += Credentials(
//      "Artifactory Realm",
//      "thegreendata.jfrog.io",
//      sys.env.get("TGD_JFROG_PUBLISHER_USER").getOrElse("Unknown JFrog Publisher user"),
//      sys.env.get("TGD_JFROG_PUBLISHER_PASSWORD").getOrElse("Unknown JFrog Publisher password")
//    )
  )
}
