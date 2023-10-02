# Introduction
This README describes how to get url-shorterner up and running

# Local environment

Firstly install docker. A suggestion would be [docker-desktop](https://www.docker.com/get-started/).

## Using docker-compose

There is a compose file containing the required services needed to run url-shorterner 
in the root folder **docker-compose.yaml**.
to execute the docker-compose file, stand in the root folder and hit command: docker compose up.
If you want to run in detatched mode please add -d to docker compose up (docker compose up -d).

## Build and Test

### Build using maven
From the root of the project run the following commands.

To build and test run:
```
mvn clean install
```
To run the application:
```
mvn spring-boot:run
OR
Right click the Application.java file in your editor and chose "Run".
```