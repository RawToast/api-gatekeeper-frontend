resolvers += Resolver.url("hmrc-sbt-plugin-releases",
  url("https://dl.bintray.com/hmrc/sbt-plugin-releases"))(Resolver.ivyStylePatterns)

resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("uk.gov.hmrc" % "sbt-auto-build" % "1.0.0") 

addSbtPlugin("uk.gov.hmrc" % "sbt-git-versioning" % "0.8.0") 

addSbtPlugin("uk.gov.hmrc" % "sbt-distributables" % "1.0.0")

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.5.12")
addSbtPlugin("org.irundaia.sbt" % "sbt-sassify" % "1.4.6")
addSbtPlugin("net.ground5hark.sbt" % "sbt-concat" % "0.1.9")
addSbtPlugin("com.typesafe.sbt" % "sbt-uglify" % "1.0.3")
addSbtPlugin("com.typesafe.sbt" % "sbt-digest" % "1.1.1")

