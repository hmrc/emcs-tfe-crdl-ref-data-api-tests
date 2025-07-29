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
import play.api.libs.ws.JsonBodyReadables.readableAsJson
import uk.gov.hmrc.api.client.HttpClient

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

    importRefData().status shouldBe 202
    eventually {
      // Wait for the import job to finish
      val importRefDataResponse = getImportRefDataStatus().body[JsValue]
      importRefDataResponse shouldBe Json.obj("status" -> "IDLE")
    }
  }

  Feature("User can test the import packaging types Ref data is done correctly") {
    Scenario("To verify whether Get Packaging Types Ref data request executes successfully") {
      Given("The endpoint is accessed")
      val authToken       = fetchAuthToken(): String
      val refDataResponse = fetchRefData(authToken, "oracle/packaging-types")
      refDataResponse.status shouldBe 200
      val refDataObject = refDataResponse.body[JsValue].as[JsObject]
      refDataObject("43").as[String] shouldBe "Bag, super bulk"
      refDataObject("44").as[String] shouldBe "Bag, polybag"
      refDataObject("OJ").as[String] shouldBe "1/4 EURO Pallet"
      refDataObject("OL").as[String] shouldBe "1/8 EURO Pallet"
    }

    Scenario("To verify whether Get Wine Operations Ref data request executes successfully") {
      Given("The endpoint is accessed")
      val authToken       = fetchAuthToken(): String
      val refDataResponse = fetchRefData(authToken, "oracle/wine-operations")
      refDataResponse.status shouldBe 200
      val refDataObject = refDataResponse.body[JsValue].as[JsObject]
      refDataObject("0").as[String] shouldBe "The product has undergone none of the following operations"
      refDataObject("1").as[String] shouldBe "The product has been enriched"
      refDataObject("2").as[String] shouldBe "The product has been acidified"
      refDataObject("3").as[String] shouldBe "The product has been de-acidified"
    }

    Scenario("To verify whether Get Member States Ref data request executes successfully") {
      Given("The endpoint is accessed")
      val authToken       = fetchAuthToken(): String
      val refDataResponse = fetchRefData(authToken, "oracle/member-states")
      refDataResponse.status                                  shouldBe 200
      refDataResponse.body[JsValue].as[List[JsValue]].take(4) shouldBe List(
        Json.parse("""{
            |  "countryCode": "AT",
            |  "country": "Austria"
            | }
            |""".stripMargin),
        Json.parse(""" {
            |  "countryCode": "BE",
            |  "country": "Belgium"
            | }
            |""".stripMargin),
        Json.parse(""" {
            |  "countryCode": "BG",
            |  "country": "Bulgaria"
            | }
            |""".stripMargin),
        Json.parse("""{
            |  "countryCode": "CY",
            |  "country": "Cyprus"
            |  }
            |""".stripMargin)
      )
    }

    Scenario("To verify whether Get Member States and Countries Ref data request executes successfully") {
      Given("The endpoint is accessed")
      val authToken       = fetchAuthToken(): String
      val refDataResponse = fetchRefData(authToken, "oracle/member-states-and-countries")
      refDataResponse.status                                  shouldBe 200
      refDataResponse.body[JsValue].as[List[JsValue]].take(4) shouldBe List(
        Json.parse("""{
            |  "countryCode": "AT",
            |  "country": "Austria"
            | }
            |""".stripMargin),
        Json.parse(""" {
            |  "countryCode": "BE",
            |  "country": "Belgium"
            | }
            |""".stripMargin),
        Json.parse(""" {
            |  "countryCode": "BM",
            |  "country": "Bermuda"
            | }
            |""".stripMargin),
        Json.parse("""{
            |  "countryCode": "BG",
            |  "country": "Bulgaria"
            |  }
            |""".stripMargin)
      )
    }

    Scenario("To verify whether Get transport Units Ref data request executes successfully") {
      Given("The endpoint is accessed")
      val authToken       = fetchAuthToken(): String
      val refDataResponse = fetchRefData(authToken, "oracle/transport-units")
      refDataResponse.status        shouldBe 200
      refDataResponse.body[JsValue] shouldBe Json.parse("""[{
          |    "code": "4",
          |    "description": "Tractor"
          |  },
          |  {
          |    "code": "5",
          |    "description": "Fixed transport installations"
          |  },
          |  {
          |    "code": "1",
          |    "description": "Container"
          |  },
          |  {
          |    "code": "2",
          |    "description": "Vehicle"
          |  },
          |  {
          |    "code": "3",
          |    "description": "Trailer"
          |  }
  ]""".stripMargin)
    }

    Scenario("To verify whether Get Type Of Document Ref data request executes successfully") {
      Given("The endpoint is accessed")
      val authToken       = fetchAuthToken(): String
      val refDataResponse = fetchRefData(authToken, "oracle/type-of-document")
      refDataResponse.status                                  shouldBe 200
      refDataResponse.body[JsValue].as[List[JsValue]].take(4) shouldBe List(
        Json.parse("""{
            |    "code": "8",
            |    "description": "Contract"
            |  }
            |""".stripMargin),
        Json.parse(""" {
            |    "code": "19",
            |    "description": "Certificate of independent small producer of alcoholic beverages"
            |  }
            |""".stripMargin),
        Json.parse(""" {
            |    "code": "4",
            |    "description": "Delivery note"
            |  }
            |""".stripMargin),
        Json.parse("""{
            |    "code": "C624",
            |    "description": "Form 302"
            |  }
            |""".stripMargin)
      )
    }

    Scenario("To verify whether Post Wine Operations Ref data request executes successfully") {
      Given("The endpoint is accessed")
      val authToken       = fetchAuthToken(): String
      val refDataResponse = postRefData("oracle/wine-operations", """["0", "1", "6", "5", "10"]""", authToken)
      refDataResponse.status shouldBe 200
      val refDataObject = refDataResponse.body[JsValue].as[JsObject]
      refDataObject("0").as[String] shouldBe "The product has undergone none of the following operations"
      refDataObject("1").as[String] shouldBe "The product has been enriched"
      refDataObject("6")
        .as[String]                 shouldBe "A product originating in a geographical unit other than that indicated in the description has been added to the product"
      refDataObject("5").as[String] shouldBe "The product has been fortified for distillation"
      refDataObject("10")
        .as[String]                 shouldBe "The product has been made on the basis of experimental use of a new oenological practice"
    }
  }
