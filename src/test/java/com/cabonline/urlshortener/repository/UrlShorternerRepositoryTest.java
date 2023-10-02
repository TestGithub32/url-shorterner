package com.cabonline.urlshortener.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.cabonline.urlshortener.ContainerClass;
import com.cabonline.urlshortener.repository.entity.UrlShorternerModel;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

@SpringBootTest(properties = "spring.main.lazy-initialization=true")
class UrlShorternerRepositoryTest extends ContainerClass {

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private UrlShorternerRepository urlShorternerRepository;

  @BeforeEach
  void setup() {
    mongoTemplate.insert(List.of(urlShorternerModel()), UrlShorternerModel.class);
  }

  @AfterEach
  void tearDown() {
    mongoTemplate.remove(new Query(), UrlShorternerModel.class);
  }

  private static Stream<Arguments> shortUrlAndExpectedValue() {
    return Stream.of(
        Arguments.of("shortUrl", urlShorternerModel()),
        Arguments.of("unexcitingShortUrl", null)
    );
  }

  @ParameterizedTest
  @MethodSource("shortUrlAndExpectedValue")
  void shouldFindUrlShorternerModelByShortUrlCorrectly(String shortUrl, UrlShorternerModel expectedValue) {
    //When
    Optional<UrlShorternerModel> actualValue =
        urlShorternerRepository.findUrlShorternerModelByShortUrl(shortUrl);

    //Then
    assertThat(actualValue)
        .usingRecursiveComparison()
        .isEqualTo(Optional.ofNullable(expectedValue));
  }

  private static UrlShorternerModel urlShorternerModel() {
    return UrlShorternerModel.builder()
        .id("1")
        .originalUrl("originalUrl")
        .shortUrl("shortUrl")
        .build();
  }
}