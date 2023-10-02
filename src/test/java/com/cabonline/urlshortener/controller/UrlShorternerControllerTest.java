package com.cabonline.urlshortener.controller;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.cabonline.urlshortener.ContainerClass;
import com.cabonline.urlshortener.exception.ShortUrlNotFoundException;
import com.cabonline.urlshortener.service.UrlShorternerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UrlShorternerControllerTest extends ContainerClass {

  @MockBean
  private UrlShorternerService urlShorternerService;

  @Autowired
  private WebApplicationContext webApplicationContext;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
  }

  @Test
  void shouldGenerateShortUrlCorrectly() throws Exception {
    //Given
    String originalUrl = "http://localhost:8080";
    String shortUrl = "shortUrl";
    String postBody = """
        {
            "url" : "%s"
        }
        """.formatted(originalUrl);
    String expectedValue = "{\"shortUrl\":\"shortUrl\"}";

    when(urlShorternerService.generateAndSaveShortUrl(originalUrl))
        .thenReturn(shortUrl);

    //When Then
    mockMvc.perform(post("/urlshorterner/generate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(postBody))
        .andExpect(status().isOk())
        .andExpect(content().string(expectedValue));
  }

  @Test
  void shouldReturnBadRequestIfUrlIsUnValid() throws Exception {
    //Given
    String originalUrl = "originalUrl";
    String postBody = """
        {
            "url" : "%s"
        }
        """.formatted(originalUrl);

    //When Then
    mockMvc.perform(post("/urlshorterner/generate")
            .contentType(MediaType.APPLICATION_JSON)
            .content(postBody))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldRedirectToOriginalUrlSuccessfully() throws Exception {
    //Given
    String originalUrl = "originalUrl";
    String shortUrl = "shortUrl";

    when(urlShorternerService.getOriginalUrl(shortUrl))
        .thenReturn(originalUrl);

    mockMvc.perform(get(String.format("/%s", shortUrl))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isFound())
        .andExpect(header().string("Location", originalUrl));
  }

  @Test
  void shouldReturnNotFoundWhenShortUrlIsMissing() throws Exception {
    //Given
    String shortUrl = "shortUrl";

    doThrow(ShortUrlNotFoundException.class).when(urlShorternerService)
        .getOriginalUrl(shortUrl);

    mockMvc.perform(get(String.format("/%s", shortUrl))
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }
}