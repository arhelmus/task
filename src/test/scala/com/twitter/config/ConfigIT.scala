package com.twitter.config

import com.twitter.config.TestConfig._
import org.scalatest.{Matchers, WordSpec}

class ConfigParserIT extends WordSpec with Matchers {

  "Config parser" should {

    "parse test config" in {
      val config =
        Config.load[TestConfig]("src/test/resources/testConfig.ini", Nil)

      config shouldBe TestConfig(
        CommonConfig(
          26214400,
          52428800,
          2147483648L,
          "/srv/var/tmp/"
        ),
        FtpConfig(
          "hello there, ftp uploading",
          "/tmp/",
          false
        ),
        HttpConfig(
          "http uploading",
          "/tmp/",
          Seq("array", "of", "values")
        )
      )
    }

    "parse test config with overrides" in {
      val config =
        Config.load[TestConfig]("src/test/resources/testConfig.ini",
                                Seq("itscript", "ubuntu", "production"))

      config shouldBe TestConfig(
        CommonConfig(
          26214400,
          52428800,
          2147483648L,
          "/srv/tmp/"
        ),
        FtpConfig(
          "hello there, ftp uploading",
          "/etc/var/uploads",
          false
        ),
        HttpConfig(
          "http uploading",
          "/srv/var/tmp/",
          Seq("array", "of", "values")
        )
      )
    }

  }

}

final case class TestConfig(common: CommonConfig,
                            ftp: FtpConfig,
                            http: HttpConfig)

object TestConfig {
  final case class CommonConfig(basicSizeLimit: Int,
                                studentSizeLimit: Int,
                                paidUsersSizeLimit: Long,
                                path: String)
  final case class FtpConfig(name: String, path: String, enabled: Boolean)
  final case class HttpConfig(name: String, path: String, params: Seq[String])
}
