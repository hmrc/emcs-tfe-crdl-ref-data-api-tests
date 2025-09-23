# emcs-tfe-crdl-ref-data-api-tests

EMCS-TFE-CRDL-REF-DATA API tests.

## Pre-requisites

### Services

Start Mongo Docker container as follows:

Follow the Developer setup instructions in the MDTP Handbook for MongoDB.
Make sure you have MongoDB 7.x or later.

Start `EMCS_TFE_CRDL` services as follows:

```bash
sm2 --start EMCS_TFE_CRDL
```
Please follow below steps in case you want to run the service locally.

Stop the TFE Ref Data microservice from the service manager:

```bash
sm2 --stop EMCS_TFE_CRDL_REFERENCE_DATA
```
Then run the TFE Ref Data microservice locally with test-only endpoint:

```bash
sbt run -Dplay.http.router=testOnlyDoNotUseInAppConf.Routes
```
## Tests

Run tests as follows:

* Argument `<environment>` must be `local`, `dev`, `qa` or `staging`.

```bash
./run-tests.sh <environment>
```

## Scalafmt

Check all project files are formatted as expected as follows:

```bash
sbt scalafmtCheckAll scalafmtCheck
```

Format `*.sbt` and `project/*.scala` files as follows:

```bash
sbt scalafmtSbt
```

Format all project files as follows:

```bash
sbt scalafmtAll
```

## License

This code is open source software licensed under the [Apache 2.0 License]("http://www.apache.org/licenses/LICENSE-2.0.html").
