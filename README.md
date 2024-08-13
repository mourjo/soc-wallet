# soc-wallet
[![Maven Build with Docker](https://github.com/mourjo/soc-wallet/actions/workflows/maven.yml/badge.svg)](https://github.com/mourjo/soc-wallet/actions/workflows/maven.yml)

## Environment variables

- `PG_HOST` defaults to `localhost`
- `PG_PORT` defaults to `5432`
- `PG_USER` defaults to `justin`
- `PG_DB` defaults to `soc_wallet_db`
- `SERVER_PORT` defaults to `8818`

## Compiling with Maven

```bash 
./mvnw clean package
```

## Running with Java 21

```bash 
java -cp target/soc-wallet-1.0-SNAPSHOT.jar soc.wallet.web.Launcher
```

## Compiling and running in one step

```bash
./mvnw clean compile exec:java  -Dexec.mainClass="soc.wallet.web.Launcher"
```

## Running tests

```bash
PG_DB=soc_wallet_test_db mvn test
```
See also: [Github Action file](.github/workflows/maven.yml)
