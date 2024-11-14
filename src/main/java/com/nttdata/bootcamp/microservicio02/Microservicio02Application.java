package com.nttdata.bootcamp.microservicio02;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@EnableReactiveMongoRepositories
@SpringBootApplication
public class Microservicio02Application {

  public static void main(String[] args) {
    SpringApplication.run(Microservicio02Application.class, args);
  }

}
