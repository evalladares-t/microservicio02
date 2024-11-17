package com.nttdata.bootcamp.microservicio02;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@EnableReactiveMongoRepositories
@SpringBootApplication
public class Microservicio02Application {

  private static final Logger log = LoggerFactory.getLogger(Microservicio02Application.class);

  public static void main(String[] args) {
    SpringApplication.run(Microservicio02Application.class, args);
  }

}
