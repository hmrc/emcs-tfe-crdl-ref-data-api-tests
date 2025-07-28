/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.api.specs

import org.scalatest.GivenWhenThen
import org.scalatest.concurrent.Eventually
import org.scalatest.featurespec.AnyFeatureSpec
import org.scalatest.matchers.should.Matchers
import org.scalatest.time.Span
import uk.gov.hmrc.api.client.HttpClient
import uk.gov.hmrc.api.conf.TestEnvironment

import scala.concurrent.duration.*
import scala.concurrent.{Await, Awaitable}

trait BaseSpec extends AnyFeatureSpec, GivenWhenThen, Matchers, Eventually, HttpClient:
  val host: String          = TestEnvironment.url("emcs-tfe-reference-data")
  val crdlCacheHost: String = TestEnvironment.url("crdl-cache")
  val testOnlyHost: String  = TestEnvironment.url("test-only")
  val authLoginHost: String = TestEnvironment.url("auth-login-api")

  // This configuration determines how long `eventually` will wait for its assertions to become true
  override given patienceConfig: PatienceConfig =
    PatienceConfig(timeout = 20.seconds, interval = 500.milliseconds)

  def await[T](f: Awaitable[T], timeout: Duration = 10.seconds): T =
    Await.result(f, timeout)
