package com.cabonline.urlshortener.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.cabonline.urlshortener.exception.ShortUrlNotFoundException;
import com.cabonline.urlshortener.repository.UrlShorternerRepository;
import com.cabonline.urlshortener.repository.entity.UrlShorternerModel;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;

@ExtendWith(MockitoExtension.class)
class UrlShorternerServiceTest {

  private static final String HOST_AND_PORT = "http://localhost:8080/";

  @Mock
  private UrlShorternerRepository urlShorternerRepository;

  private UrlShorternerService urlShorternerService;

  @BeforeEach
  void setUp() {
    this.urlShorternerService = new UrlShorternerService(urlShorternerRepository, HOST_AND_PORT);
  }

  @Test
  void shouldGenerateAndSaveShortUrlSuccessfully() {
    //Given
    String originalUrl = "originalUrl";
    String shortUrl = "70ab22ba";
    String expectedValue = String.format("%s%s", HOST_AND_PORT, shortUrl);

    UrlShorternerModel urlShorternerModel = UrlShorternerModel.builder()
        .shortUrl(shortUrl)
        .originalUrl(originalUrl)
        .build();

    //When
    String actualValue = urlShorternerService.generateAndSaveShortUrl(originalUrl);

    //Then
    verify(urlShorternerRepository, times(1))
        .save(urlShorternerModel);
    assertThat(actualValue)
        .isEqualTo(expectedValue);
  }

  @Test
  void shouldReturnShortUrlIfAlreadyExistingInDB() {
    //Given
    String originalUrl = "originalUrl";
    String shortUrl = "70ab22ba";
    String expectedValue = String.format("%s%s", HOST_AND_PORT, shortUrl);

    doThrow(DuplicateKeyException.class).when(urlShorternerRepository)
        .save(any(UrlShorternerModel.class));

    //When
    String actualValue = urlShorternerService.generateAndSaveShortUrl(originalUrl);

    //Then
    verify(urlShorternerRepository, times(1))
        .save(any(UrlShorternerModel.class));
    assertThat(actualValue)
        .isEqualTo(expectedValue);
  }

  @Test
  void shouldGetOriginalUrlSuccessfully() {
    //Given
    String expectedValue = "originalUrl";
    String shortUrl = "70ab22ba";

    UrlShorternerModel urlShorternerModel = UrlShorternerModel.builder()
        .shortUrl(shortUrl)
        .originalUrl(expectedValue)
        .build();

    when(urlShorternerRepository.findUrlShorternerModelByShortUrl(shortUrl))
        .thenReturn(Optional.of(urlShorternerModel));

    //When
    String actualValue = urlShorternerService.getOriginalUrl(shortUrl);

    //Then
    assertThat(actualValue)
        .isEqualTo(expectedValue);
  }

  @Test
  void shouldThrowShortUrlNotFoundExceptionWhenMissing() {
    //Given
    String shortUrl = "70ab22ba";

    when(urlShorternerRepository.findUrlShorternerModelByShortUrl(shortUrl))
        .thenReturn(Optional.empty());

    //When Then
    assertThatThrownBy(() -> urlShorternerService.getOriginalUrl(shortUrl))
        .isInstanceOf(ShortUrlNotFoundException.class)
        .hasMessage(String.format("Unable to find originalUrl by shortUrl: '%s'", shortUrl));
  }
}