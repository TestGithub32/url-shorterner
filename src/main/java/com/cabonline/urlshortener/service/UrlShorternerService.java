package com.cabonline.urlshortener.service;

import com.cabonline.urlshortener.exception.ShortUrlNotFoundException;
import com.cabonline.urlshortener.repository.UrlShorternerRepository;
import com.cabonline.urlshortener.repository.entity.UrlShorternerModel;
import com.google.common.hash.Hashing;
import java.nio.charset.Charset;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@Service
public class UrlShorternerService {

  private final UrlShorternerRepository urlShorternerRepository;
  private final String hostAndPort;

  @Autowired
  public UrlShorternerService(UrlShorternerRepository urlShorternerRepository,
      @Value("${server.hostAndPort}") String hostAndPort) {
    this.urlShorternerRepository = urlShorternerRepository;
    this.hostAndPort = hostAndPort;
  }

  public String generateAndSaveShortUrl(String originalUrl) {
    String shortUrl = generateShortUrl(originalUrl);

    try {
      urlShorternerRepository.save(UrlShorternerModel.builder()
          .originalUrl(originalUrl)
          .shortUrl(shortUrl)
          .build());
    } catch (DuplicateKeyException e) {
      log.info("originalUrl: '{}' and shortUrl: '{}' already exists", originalUrl, shortUrl);
    }

    return buildShortUrlWithHostAndPort(shortUrl);
  }

  public String getOriginalUrl(String shortUrl) {
    return urlShorternerRepository
        .findUrlShorternerModelByShortUrl(shortUrl)
        .orElseThrow(() ->
            new ShortUrlNotFoundException("Unable to find originalUrl by shortUrl: '%s'"
                .formatted(shortUrl)))
        .getOriginalUrl();
  }

  private String generateShortUrl(String originalUrl) {
    return Hashing
        .murmur3_32_fixed()
        .hashString(originalUrl, Charset.defaultCharset())
        .toString();
  }

  private String buildShortUrlWithHostAndPort(String shortUrl) {
    return UriComponentsBuilder
        .fromHttpUrl(hostAndPort)
        .path(shortUrl)
        .toUriString();
  }
}
