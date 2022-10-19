

## How to run application
1. `./grdlew bootRun`
2. `curl localhost:8080/api/v1/health/hello`

## How to connect to local in memory H2 DB
```
http://localhost:8080/h2-console

jdbc url: jdbc:h2:tcp://localhost:9092/mem:testdb;OLD_INFORMATION_SCHEMA=TRUE
id: sa
password:
```
- The in memory H2 DB can only run while the server is running because the server provides a tcp server wrapped around the in memory H2 DB you can access it.

## How to connect to swagger 3 documentation
```
http://localhost:8080/swagger-ui/index.html
```