# soc-wallet

## Compiling with Maven

```bash 
./mvnw clean package
```

## Running with Java 21

```bash 
java -cp target/soc-wallet-1.0-SNAPSHOT.jar web.controller.Main
```

## Compiling and running in one step

```bash
./mvnw clean compile exec:java  -Dexec.mainClass="web.controller.Main"
```
