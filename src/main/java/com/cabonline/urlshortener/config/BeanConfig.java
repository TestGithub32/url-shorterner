package com.cabonline.urlshortener.config;

import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {

  @Bean
  public UrlValidator urlValidator() {
    String[] schemes = {"http", "https"};
    long allowLocalUrls = UrlValidator.ALLOW_LOCAL_URLS;
    return new UrlValidator(schemes, allowLocalUrls);
  }
}
