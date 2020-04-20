# myRetail RESTful Service
Cassandra NoSQL backed API that has all necessary CRUD methods for a Price object. Also exposes `/products` path that
integrates with 3rd party API to pull Product information and combine with owned Price information.

## Technology Decisions 
Overview of tech used but not limited to list below.
* SpringBoot
* Spring Data Rest - Looks at data model to build HTTP Resources and exposes them.
* Spring Data Cassandra - Spring Repo support for Cassandra
* Spring Web - Host of features that enforce best practices when dealing with web services
* Spring Actuator - Admin style endpoints exposed about your app. Builtin `/health`
* Swagger - API Documentation
* Lombok - Limits boilerplate code
* Spock - Testing framework
* Docker - Container service for Cassandra


## Docker Hosted Cassandra
Running Cassandra on my local Docker. Pulled the latest Cassandra image, started the image, created a keyspace and table.

Steps:
* `docker run -p 9042:9042 cassandra:latest`
* `create keyspace challenge with replication = {'class':'SimpleStrategy','replication_factor' : 1};`
* `USE challenge;`
* `CREATE TABLE challenge.prices ( id uuid PRIMARY KEY, value decimal, currency_code text, product_id int );`


## API Documentation
API is documented using Swagger. After starting the application, running the Main Method in `ChallengeApplication`, 
Swagger UI is available at: `http://localhost:8080/swagger-ui.html#/`.