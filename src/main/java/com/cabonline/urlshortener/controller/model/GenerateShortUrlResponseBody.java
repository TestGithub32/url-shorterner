package com.cabonline.urlshortener.controller.model;

import lombok.Builder;

@Builder
public record GenerateShortUrlResponseBody(String shortUrl) {

}
