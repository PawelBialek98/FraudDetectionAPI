# FraudDetectionAPI - test project

At the moment applicaiton works only with *dev mode* (MongoDB works in Dev Services mode)

## Requirements

- Installed Docker (to run MongoDB)
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
**src/test/java/com/example/GenerateToken.java**
You can do it by running:
**mvn exec:java -Dexec.mainClass=com.example.GenerateToken -Dexec.classpathScope=test**

In the result in terminal will be printed generated token - now add it to Authorization header to gain access to endpoints :)

Endpoints are documented on swaggerUI:

> <http://localhost:8080/q/dev/>.

## Related Guides

- REST ([guide](https://quarkus.io/guides/rest)): A Jakarta REST implementation utilizing build time processing and
  Vert.x. This extension is not compatible with the quarkus-resteasy extension, or any of the extensions that depend on
  it.
- Keycloak Authorization ([guide](https://quarkus.io/guides/security-keycloak-authorization)): Policy enforcer using
  Keycloak-managed permissions to control access to protected resources
- MongoDB with Panache ([guide](https://quarkus.io/guides/mongodb-panache)): Simplify your persistence code for MongoDB
  via the active record or the repository pattern
- REST Client ([guide](https://quarkus.io/guides/rest-client)): Call REST services
- Jacoco - Code Coverage ([guide](https://quarkus.io/guides/tests-with-coverage)): Jacoco test coverage support
- OpenID Connect ([guide](https://quarkus.io/guides/security-openid-connect)): Verify Bearer access tokens and
  authenticate users with Authorization Code Flow

## Provided Code

### REST Client

Invoke different services through REST with JSON

[Related guide section...](https://quarkus.io/guides/rest-client)

### REST

Easily start your REST Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)
