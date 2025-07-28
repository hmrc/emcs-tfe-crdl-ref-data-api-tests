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

import org.scalatest.BeforeAndAfterAll
import play.api.libs.json.Reads.*
import play.api.libs.json.{JsObject, JsValue, Json}
import play.api.libs.ws.DefaultBodyReadables.*
import play.api.libs.ws.JsonBodyReadables.readableAsJson
import play.api.libs.ws.StandaloneWSResponse
import uk.gov.hmrc.api.client.HttpClient

import java.time.Instant

class ImportRefDataAPISpec extends BaseSpec, HttpClient, BeforeAndAfterAll:
  override def beforeAll(): Unit = {
    deleteList("codelists")
    deleteLastUpdated()
    deleteList("correspondence-lists")
    importLists("codelists").status            shouldBe 202
    importLists("correspondence-lists").status shouldBe 202
    eventually {
      // Wait for the import job to finish
      val codelistResponse = getImportStatus("codelists").body[JsValue]
      codelistResponse shouldBe Json.obj("status" -> "IDLE")
      val correspondenceListResponse = getImportStatus("correspondence-lists").body[JsValue]
      correspondenceListResponse shouldBe Json.obj("status" -> "IDLE")
    }
    importRefData().status                     shouldBe 202
    eventually {
      // Wait for the import job to finish
      val codelistResponse = getImportStatus("codelists").body[JsValue]
      codelistResponse shouldBe Json.obj("status" -> "IDLE")
      val correspondenceListResponse = getImportStatus("correspondence-lists").body[JsValue]
      correspondenceListResponse shouldBe Json.obj("status" -> "IDLE")
      val importRefDataResponse = getImportRefDataStatus().body[JsValue]
      importRefDataResponse shouldBe Json.obj("status" -> "IDLE")
    }
  }

  Feature("User can test the import Ref data is done correctly") {
    Scenario("To verify whether Get Ref data request executes successfully") {
      Given("The endpoint is accessed")

      def fetchRefData(authToken: String): StandaloneWSResponse = await(
        get(s"$host/oracle/packaging-types", "Authorization" -> authToken)
      )
      fetchRefData(authToken: String)
      fetchRefData(authToken: String).status        shouldBe 200
      fetchRefData(authToken: String).body[JsValue] shouldBe Json.parse("""[{
            |   "key": "BL",
            |  "value": "Saint Barthélemy",
            |  "properties": {
            |    "actionIdentification": "823"
            |  }
            | } ,
            | {
            |  "key": "BM",
            |  "value": "Bermuda",
            |  "properties": {
            |    "actionIdentification": "824"
            |  }
            | },
            | {
            |  "key": "CX",
            |  "value": "Christmas Island",
            |  "properties": {
            |    "actionIdentification": "848"
            |  }
            | },
            | {
            |  "key": "CY",
            |  "value": "Cyprus",
            |  "properties": {
            |    "actionIdentification": "849"
            |  }
            |  }
]""".stripMargin)
    }
  }
