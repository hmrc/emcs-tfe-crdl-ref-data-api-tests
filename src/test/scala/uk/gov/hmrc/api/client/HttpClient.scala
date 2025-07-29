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

package uk.gov.hmrc.api.client

import play.api.libs.ws.DefaultBodyWritables.*
import play.api.libs.ws.{EmptyBody, StandaloneWSResponse}
import uk.gov.hmrc.api.specs.BaseSpec
import uk.gov.hmrc.apitestrunner.http.HttpClient as TestRunnerHttpClient

import scala.concurrent.Future

trait HttpClient extends TestRunnerHttpClient:
  this: BaseSpec =>

  def get(url: String, headers: (String, String)*): Future[StandaloneWSResponse] =
    mkRequest(url)
      .withHttpHeaders(headers*)
      .get()

  def post(url: String, headers: (String, String)*): Future[StandaloneWSResponse] =
    mkRequest(url)
      .withHttpHeaders(headers*)
      .post(EmptyBody)

  def post(url: String, bodyAsJson: String, headers: (String, String)*): Future[StandaloneWSResponse] =
    mkRequest(url)
      .withHttpHeaders(headers*)
      .post(bodyAsJson)

  def delete(url: String, headers: (String, String)*): Future[StandaloneWSResponse] =
    mkRequest(url)
      .withHttpHeaders(headers*)
      .delete()

  def fetchAuthToken(): String =
    await(
      post(
        authLoginHost,
        s"""{
         |  "credId": "1234",
         |  "affinityGroup": "Organisation",
         |  "credentialStrength": "strong",
         |  "enrolments": [
         |    {
         |      "key": "HMRC-EMCS-ORG",
         |      "state": "Activated",
         |      "identifiers": [{ "key": "ExciseNumber", "value": "GBWK001234569" }]
         |    }
         |  ]
         |}""".stripMargin,
        "Content-Type" -> "application/json"
      )
    ).header("Authorization")
      .getOrElse(throw new RuntimeException("No Authorization Header retrieved from auth login api"))

  def deleteList(list: String): StandaloneWSResponse = await(delete(s"$crdlCacheHost/$list"))

  def deleteLastUpdated(): StandaloneWSResponse = await(delete(s"$crdlCacheHost/last-updated"))

  def importLists(list: String): StandaloneWSResponse = await(post(s"$crdlCacheHost/$list"))

  def getImportStatus(list: String): StandaloneWSResponse = await(get(s"$crdlCacheHost/$list"))

  def importRefData(): StandaloneWSResponse = await(post(s"$testOnlyHost/reference-data"))

  def getImportRefDataStatus(): StandaloneWSResponse = await(get(s"$testOnlyHost/reference-data"))

  def deleteRefData(refData: String): StandaloneWSResponse = await(delete(s"$testOnlyHost/$refData"))

  def fetchRefData(authToken: String, refData: String): StandaloneWSResponse = await(
    get(s"$host/$refData", "Authorization" -> authToken)
  )

  def postRefData(refData: String, bodyAsJson: String, authToken: String): StandaloneWSResponse = await(
    post(s"$host/$refData", bodyAsJson, "Authorization" -> authToken, "Content-Type" -> "application/json")
  )
