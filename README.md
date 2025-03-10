# FraudDetectionAPI - test project

At the moment applicaiton works only with *dev mode* (MongoDB works in Dev Services mode)

## Requirements

- Installed Docker (to run MongoDB in Dev Services mode)
- In order to integrate with **MasterCard BIN API**:
  - You must set up/own a developer account
  - Set proper parameters in _src/main/resources/application.properties_:
    - **mastercard.api.consumerKey** - Set here you consumer Key 
    - **mastercard.api.pkcs12Key.filePath** - Path to your signing file
    - **mastercard.api.signingKey.alias** - Signing file alias 
    - **mastercard.api.signingKey.password** - Signing file password

```shell script
./mvnw quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## After Start

First thing you need to do before calling API is to generate JWT Token.
Currently aplication use simple SmallRye JWT.<br>
Both test publicKey and privateKey are already provided.<br>
To access endpoints please generate Bearer token by running:<br>
**src/test/java/com/example/GenerateToken.java**<br>
You can do it by running:<br>

```shell script
mvn exec:java -Dexec.mainClass=com.example.GenerateToken -Dexec.classpathScope=test
```

In the result in terminal will be printed generated token - now add it to Authorization header to gain access to endpoints :)

Endpoints are documented on swaggerUI:

> <http://localhost:8080/q/swagger-ui/>

In file *src/test/resources/requestsExample.http* you can find requests examples

## Tests

In order to run tests run:

```shell script
mvn test
```
The current test coverage **is not 100%** (jacoco), but I wanted to create a general picture of the tests. Both unit tests (e.g. HighRiskCountryRiskTest) and integration tests with the mastercard API (MastercardBinClientTest)

> **_NOTE:_**  Mastercard API tests integrates with remote API and therefore requires firstly provide specific parameters in application.properties file

> parameters in application.properties file