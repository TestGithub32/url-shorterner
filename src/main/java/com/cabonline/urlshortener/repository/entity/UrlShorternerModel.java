package com.cabonline.urlshortener.repository.entity;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Builder
@EqualsAndHashCode
@Document(collection = "cabonline_urls")
public class UrlShorternerModel {

  @Id
  private String id;

  private String originalUrl;

  @Indexed(unique = true)
  private String shortUrl;
}
