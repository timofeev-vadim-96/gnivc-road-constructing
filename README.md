## Logistics transportation app

This is an application with a micro-service architecture that implements the business logic of **logistics
transportation**.

`Quick start`:  
1. Поднять каскад контейнеров из директории ./docker-compose с помощью команды 
```bash
docker-compose up -d
```
2. run discovery service
3. run gateway service
4. run portal service
5. run logist service
6. run driver service
7. run dwh service

`If this is the first launch`:  
* create a new user by ur: ipaddress:port/portal/v1/user
  * the password will be sent to your email, use it to log in
* get a token at url: ipaddress:port/openid-connect/
* register your company by providing its INN at the url: ipaddress:port/portal/v1/company
* get the token again by url and save it in postman as a variable by url: ipaddress:port/openid-connect/

`Project structure`: 
* **discovery service** - implements the possibility of registering services based on Eureka
* **gateway service** - implements gateway cloud-based `reactive-request` routing to the end microservice, and implements user authentication and authorization logic based on the Keycloak authorization server with an access-token
* **portal service** - implements the ability to register users both as company registrars and as users with roles in the company assigned by the administrator. Users can have different roles in different companies. Passwords after registration are sent to the specified user's `email` address. The possibility of registering a company according to its INN, using the `DaData` service. The necessary functions for changing, deleting, obtaining user data, a list of vehicles and employees, etc. have been implemented for users and the company.
* **logist serivice** - responsible for creating/changing/receiving and deleting messages and flights in the message frame, as well as for working with data in the message frame received by a new topic in `Kafka` from the driver support service.
* **driver service** - implements the ability to receive data on the driver's assignment, create new flights by the driver himself, send flight events and points sent via `Kafka` to the logistics service, as well as send photos on a scheduled flight stored in the `Minio` object data warehouse, receive and delete them.
* **dwh service** - implements the ability to obtain statistics on companies: the number of completed/canceled/started flights from the beginning of the day, the number of tasks from the beginning of the day



`Note` Some controller handles have "@RequestParam companyName". This parameter is required for correct authorization on
the gateway-ms side.

> The project code is adapted to the requirements of the `maven-checkstyle-plugin` 

`Technologies used in the project`:  
1. Keycloak
2. Spring-cloud (Netflix Eureka + Gateway)
3. PostgreSQL
4. Kafka
5. Spring AOP
6. Spring Actuator
7. Spring Security
8. Spring Webflux
