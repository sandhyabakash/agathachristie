PreRequisite
1. Redis server should be installed
Redis host and port details to be updated in the application.properties file

Build 
1. Run `gradlew bootJar` from the project root directory

Execute
1. Navigate to <project root dir >/build/libs
2. Run `java -jar api-rate-limiter-1.0.jar`

Use following curl command to test
```
curl -X GET \
  'http://localhost:8080/hello?name=Sandhya' \
  -H 'x-api-key: testKey'
```

The api key is configurable via the application.properties. 

