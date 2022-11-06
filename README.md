

## How to run application
1. `./grdlew bootRun`
2. `curl localhost:8080/api/v1/health/hello`

## How to connect to local in memory H2 DB
- browser base console : http://localhost:8080/h2-console
- intellij data console jdbc url : `jdbc:h2:tcp://localhost:9092/mem:testdb;OLD_INFORMATION_SCHEMA=TRUE`
```
id: sa
password:
```
- The in memory H2 DB can only run while the server is running because the server provides a tcp server wrapped around the in memory H2 DB you can access it.

## How to connect to swagger 3 documentation
```
http://localhost:8080/swagger-ui/index.html
```

## How to use Spring Boot Devtools
- Check `Settings > Advanced Settings > Allow auto-make to start...`
- Check `Settings > Build, Execution, Deployment > Compiler > Build project automatically`
- **Before intellj 2021 you should have changed registry**
