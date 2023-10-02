package com.cabonline.urlshortener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

@Slf4j
public abstract class ContainerClass {

  @Container
  private static final MongoDBContainer mongoDbContainer;

  static {
    mongoDbContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.2.23"))
        .withLogConsumer(new Slf4jLogConsumer(log));
    mongoDbContainer.start();
  }

  @DynamicPropertySource
  static void setDynamicProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongoDbContainer::getConnectionString);
  }
}
