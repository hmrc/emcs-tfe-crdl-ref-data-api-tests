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

    deleteRefData("codelists")
    deleteRefData("excise-products")
    deleteRefData("cn-codes")
    importRefData().status shouldBe 202
    eventually {
      // Wait for the import job to finish
      val importRefDataResponse = getImportRefDataStatus().body[JsValue]
      importRefDataResponse shouldBe Json.obj("status" -> "IDLE")
    }
  }

  Feature("User can test the import of Ref data is done correctly") {
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

    Scenario("To verify whether Post Packaging Types Ref data request executes successfully") {
      Given("The endpoint is accessed")
      val authToken       = fetchAuthToken(): String
      val refDataResponse = postRefData("oracle/packaging-types", """["IN","TC","CY","TK"]""", authToken)
      refDataResponse.status shouldBe 200
      val refDataObject = refDataResponse.body[JsValue].as[JsObject]
      refDataObject("IN").as[String] shouldBe "Ingot"
      refDataObject("TC").as[String] shouldBe "Tea-chest"
      refDataObject("CY")
        .as[String]                  shouldBe "Cylinder"
      refDataObject("TK").as[String] shouldBe "Tank, rectangular"
    }

    Scenario("To verify whether Get EPC Codes Ref data request executes successfully") {
      Given("The endpoint is accessed")
      val authToken       = fetchAuthToken(): String
      val refDataResponse = fetchRefData(authToken, "oracle/epc-codes ")
      refDataResponse.status                                  shouldBe 200
      refDataResponse.body[JsValue].as[List[JsValue]].take(3) shouldBe List(
        Json.parse("""{
             "code": "B000",
            |    "description": "Beer",
            |    "category": "B",
            |    "categoryDescription": "Beer",
            |    "unitOfMeasureCode": 3
            |  }
            |""".stripMargin),
        Json.parse(""" {
          "code": "E200",
            |    "description": "Vegetable and animal oils Products falling within CN codes 1507 to 1518, if these are intended for use as heating fuel or motor fuel (Article 20(1)(a))",
            |    "category": "E",
            |    "categoryDescription": "Energy Products",
            |    "unitOfMeasureCode": 2
            |  }
            |""".stripMargin),
        Json.parse(""" {
            "code": "E300",
            |    "description": "Mineral oils Products falling within CN codes 2707 10, 2707 20, 2707 30 and 2707 50 (Article 20(1)(b))",
            |    "category": "E",
            |    "categoryDescription": "Energy Products",
            |    "unitOfMeasureCode": 2
            |  }
            |""".stripMargin)
      )
    }

    Scenario("To verify whether Retrieve CN Code Information for specific CN Codes request executes successfully") {
      Given("The endpoint is accessed")
      val authToken       = fetchAuthToken(): String
      val refDataResponse = fetchRefData(authToken, "oracle/cn-codes/B000")
      refDataResponse.status                                  shouldBe 200
      refDataResponse.body[JsValue].as[List[JsValue]].take(3) shouldBe List(
        Json.parse("""{
             "cnCode": "22030001",
            |    "cnCodeDescription": "Beer made from malt in bottles holding 10 litres or less",
            |    "exciseProductCode": "B000",
            |    "exciseProductCodeDescription": "Beer",
            |    "unitOfMeasureCode": 3
            |  }
            |""".stripMargin),
        Json.parse(""" {
          "cnCode": "22030009",
            |    "cnCodeDescription": "Beer made from malt in containers other than bottles holding 10 litres or less",
            |    "exciseProductCode": "B000",
            |    "exciseProductCodeDescription": "Beer",
            |    "unitOfMeasureCode": 3
            |  }
            |""".stripMargin),
        Json.parse(""" {
            "cnCode": "22030010",
            |    "cnCodeDescription": "Beer made from malt in containers holding more than 10 litres",
            |    "exciseProductCode": "B000",
            |    "exciseProductCodeDescription": "Beer",
            |    "unitOfMeasureCode": 3
            |  }
            |""".stripMargin)
      )
    }

    Scenario("To verify whether Post CN Code Information Ref data request executes successfully") {
      Given("The endpoint is accessed")
      val authToken       = fetchAuthToken(): String
      val refDataResponse = postRefData(
        "oracle/cn-code-information",
        """{
                                                                    |  "items": [{
                                                                    |    "cnCode": "15081010",
                                                                    |    "productCode": "E200"
                                                                    |  }, {
                                                                    |    "cnCode": "15089010",
                                                                    |    "productCode": "E200"
                                                                    |  }
                                                                    ]
                                                                    |}""".stripMargin,
        authToken
      )
      refDataResponse.status shouldBe 200
      val refDataObject = refDataResponse.body[JsValue].as[JsObject]
      refDataObject("15081010") shouldBe Json.parse("""{
                                                                       |    "cnCode": "15081010",
                                                                       |    "cnCodeDescription": "Groundnut oil, crude, for technical or industrial uses other than the manufacture of foodstuffs for human consumption",
                                                                       |    "exciseProductCode": "E200",
                                                                       |    "exciseProductCodeDescription": "Vegetable and animal oils Products falling within CN codes 1507 to 1518, if these are intended for use as heating fuel or motor fuel (Article 20(1)(a))",
                                                                       |    "unitOfMeasureCode": 2
                                                                       |  }""".stripMargin)
      refDataObject("15089010") shouldBe Json.parse("""{
                                                                       |    "cnCode": "15089010",
                                                                       |    "cnCodeDescription": "Groundnut oil, whether or not refined, not crude, for technical or industrial uses other than the manufacture of foodstuffs for human consumption",
                                                                       |    "exciseProductCode": "E200",
                                                                       |    "exciseProductCodeDescription": "Vegetable and animal oils Products falling within CN codes 1507 to 1518, if these are intended for use as heating fuel or motor fuel (Article 20(1)(a))",
                                                                       |    "unitOfMeasureCode": 2
                                                                       |  }""".stripMargin)
    }

    Scenario("To verify Delete Excise-Products request is successful") {
      Given("The endpoint is accessed")
      val deleteExciseProducts_response = deleteRefData("excise-products")
      deleteExciseProducts_response.status shouldBe 200
      val deleteCnCodes_response = deleteRefData("cn-codes")
      deleteCnCodes_response.status shouldBe 200
      val deleteCodelists_response = deleteRefData("codelists")
      deleteCodelists_response.status shouldBe 200

//      val authToken       = fetchAuthToken(): String
//      val refDataResponse = fetchRefData(authToken, "oracle/wine-operations")
//      refDataResponse.status        shouldBe 200
//      refDataResponse.body[JsValue] shouldBe Json.arr()
    }
  }
