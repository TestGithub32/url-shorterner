package com.cabonline.urlshortener.repository;

import com.cabonline.urlshortener.repository.entity.UrlShorternerModel;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UrlShorternerRepository extends MongoRepository<UrlShorternerModel, String> {
  Optional<UrlShorternerModel> findUrlShorternerModelByShortUrl(String shortUrl);
}
