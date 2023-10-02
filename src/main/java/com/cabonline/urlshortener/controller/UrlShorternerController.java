package com.cabonline.urlshortener.controller;

import com.cabonline.urlshortener.controller.model.GenerateShortUrlRequestBody;
import com.cabonline.urlshortener.controller.model.GenerateShortUrlResponseBody;
import com.cabonline.urlshortener.exception.ShortUrlNotFoundException;
import com.cabonline.urlshortener.service.UrlShorternerService;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UrlShorternerController {

  private final UrlValidator urlValidator;
  private final UrlShorternerService urlShorternerService;

  @PostMapping(value = "/urlshorterner/generate",
      produces = MediaType.APPLICATION_JSON_VALUE,
      consumes = MediaType.APPLICATION_JSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<GenerateShortUrlResponseBody> createShortUrl(@RequestBody GenerateShortUrlRequestBody generateShortUrlRequestBody) {
    if (urlValidator.isValid(generateShortUrlRequestBody.url())) {
      return ResponseEntity.ok(GenerateShortUrlResponseBody.builder()
          .shortUrl(urlShorternerService.generateAndSaveShortUrl(generateShortUrlRequestBody.url()))
          .build());
    } else {
      return ResponseEntity
          .status(HttpStatus.BAD_REQUEST)
          .build();
    }
  }

  @GetMapping(value = "/{shortUrl}")
  @ResponseStatus(HttpStatus.OK)
  public ResponseEntity<String> redirectToOriginalUrl(@PathVariable("shortUrl") String shortUrl) {
    try {
      return ResponseEntity.status(HttpStatus.FOUND)
          .location(URI.create(urlShorternerService.getOriginalUrl(shortUrl)))
          .build();
    } catch (ShortUrlNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(e.getMessage());
    }
  }
}
