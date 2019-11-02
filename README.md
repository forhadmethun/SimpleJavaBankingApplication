## Project Description
A sandbox Java banking application that can be used for:

● Sending money between two predefined accounts with a positive starting balance  
● Requesting account balance and list of transactions

## Live Preview
Go to the following link to get a live preview of API's- 

http://ec2-3-83-201-125.compute-1.amazonaws.com:8080/swagger-ui.html#

## Technology Used
Spring Boot, Maven, JPA, Hibernate, HSQLDB Database(or PostgreSQL) 

## Project Setup
Clone the github repository - 
```
git clone https://github.com/forhadmethun/SimpleJavaBankingApplication.git
```
- Requirement
    - JDK 1.8 or higher
    - Maven

To use PostgreSQL change the following file accordingly - 
```
/src/main/resources/application.properties
pom.xml
```


## Running the server locally ##
Firstly build the application. To build and package a Spring Boot app into a single executable jar file with a Maven, the below command can be used. Run the command from the project folder which contains the pom.xml file.

```
maven package
```
or,

```
mvn install
```

To run the Spring Boot app from a command line in a Terminal window the following java -jar command can be used. This is provided your Spring Boot app was packaged as an executable jar file.

```
java -jar target/banking-0.0.1-SNAPSHOT.jar
```

Maven plugin can also be used to run the app. Use the below example to run Spring Boot app with Maven plugin :

```
mvn spring-boot:run
```

It is also possible to follow any/all of the above commands, or simply use the run configuration provided by any IDE and run/debug the app from there for development purposes. Once the server is setup the admin interface can be accessed at the following URL :

http://localhost:8080

And the REST APIs can be accessed over the following base-path :

http://localhost:8080/api/

Some of the important api endpoints are as follows :

- http://localhost:8080/api/account/send-money (HTTP:POST)
- http://localhost:8080/api/account/statement (HTTP:POST)
- http://localhost:8080/api/account/create (HTTP:POST)
- http://localhost:8080/api/account/all (HTTP:GET)

## API Documentation ##
After setup of the project documentation can be found in the location: 
http://localhost:8080/swagger-ui.html

## Unit Testing
The unit test can be found on the following directory
```
src/test/java/com/forhadmethun/banking/service/impl/
```