# soc-wallet

[![Maven Build with Docker](https://github.com/mourjo/soc-wallet/actions/workflows/maven.yml/badge.svg)](https://github.com/mourjo/soc-wallet/actions/workflows/maven.yml)

## Scope

This is a repository that implements the following functionalities. It does so in a manner that
does not separate concerns out well. But it still works. Submit a pull request if you see
any improvements!

- Create and fetch users
- Create and fetch wallet accounts
- Users can make internal transfers to other wallets
- Users can transfer money from external accounts to their wallet

## Start dependent services

```bash
docker compose up
```

## Environment variables

- `PG_HOST` defaults to `localhost`
- `PG_PORT` defaults to `5432`
- `PG_USER` defaults to `justin`
- `PG_DB` defaults to `soc_wallet_db`
- `SERVER_PORT` defaults to `8818`
- `API_SECRET` defaults to `no_auth` - this is a value that needs to be passed in every request (a
  placeholder for identifying authenticated clients)

## Compiling with Maven

```bash 
./mvnw clean package
```

## Running with Java 21

```bash 
API_SECRET=humpty_dumpty java -cp target/soc-wallet-1.0-SNAPSHOT.jar soc.wallet.web.Launcher
```

## Compiling and running in one step

```bash
API_SECRET=humpty_dumpty ./mvnw clean compile exec:java  -Dexec.mainClass="soc.wallet.web.Launcher"
```

## Accessing the API

There is a Swagger page that will provide details about the individual APIs
http://localhost:8818/swagger-ui

![](src/test/resources/swagger.png)

## Running tests

```bash
API_SECRET=humpty_dumpty PG_DB=soc_wallet_test_db mvn test
```

See also: [Github Action file](.github/workflows/maven.yml)
