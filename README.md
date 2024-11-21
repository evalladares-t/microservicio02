# Account Microservice (microservicio02)

## Description

The Account Service is responsible for operations related to bank accounts, such as creation, modification, balance inquiries and status updates. Work closely with Customer Service to associate accounts with registered customers.

## Pre - requisites

- **Java 20**
- **Maven**
- **Docker** (for Docker execution only)
- Red Docker compartida: `my-network`

## Initial Configuration

### Run Locally

#### Modify the file`bootstrap.yml`

Configure the active profile to use Docker:

```yaml
spring:
  profiles:
    active: local
```

#### Execute the Main Class

```yaml
mvn spring-boot:run
```

Alternatively, if using an IDE (e.g., IntelliJ IDEA, Eclipse), navigate to the Microservicio02Application class and run it directly.

### Run Docker

#### Modify the file`bootstrap.yml`

Configure the active profile to use Docker:

```yaml
spring:
  profiles:
    active: docker
```
#### Compile and generate the Docker container
Build the Docker image with:

```yaml
docker build -t microservicio02:0.0.1-SNAPSHOT .
```

####  Run the microservice Docker container
Start the container with:

```yaml
docker run --name microservicio02 --network my-network -p 8082:8082 microservicio02:0.0.1-SNAPSHOT
```

## Resources:
- **Resource link  - https://github.com/evalladares-t/resource-bootcamp57**
- **Link github  - https://github.com/evalladares-t**

## Notes:
- **If you need to change ports or other settings, edit the corresponding application.yml and Dockerfile files.**
- **To debug errors, check the container logs:**
    ```yaml
    docker logs microservicio02
    ``` 
- **Make sure that the Config Server and Registry Server are running before starting other services.**
