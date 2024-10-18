package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = "spring.liquibase.enabled=false")
class UrlRepositoryTest extends AbstractDBTest {
  private UrlRepository repository;

  @Autowired
  UrlRepositoryTest(UrlRepository repository) {
    this.repository = repository;
  }

  @BeforeAll
  static void beforeAll() {
    POSTGRE_SQL_CONTAINER.start();
  }

  @AfterAll
  static void afterAll() {
    POSTGRE_SQL_CONTAINER.close();
  }

  @Test
  void testSave() {
    // given
    String hash = "hash";
    String url = "http://new_url";
    repository.save(createUrl(hash, url));
    long urlId = 4L;
    // when
    Optional<Url> optionalUrlActual = repository.findById(urlId);
    // then
    assertTrue(optionalUrlActual.isPresent());
    Url urlActual = optionalUrlActual.get();
    assertEquals(hash, urlActual.getHash());
    assertEquals(url, urlActual.getUrl());
  }

  @Test
  void findAndDeleteHashExpired() {
    // given
    LocalDateTime dateExpired = LocalDateTime.of(2024, 10, 16, 11, 0, 0);
    String expiredHash = "c";
    int sizeExp = 1;
    // when
    List<String> hashesExpired = repository.findAndDeleteHashExpired(dateExpired);
    // then
    assertEquals(sizeExp, hashesExpired.size());
    assertEquals(expiredHash, hashesExpired.get(0));
  }

  @Test
  void testFindByHash() {
    // given
    String hashExp = "b";
    // when
    Optional<Url> optionalUrlActual = repository.findByHash(hashExp);
    // then
    assertTrue(optionalUrlActual.isPresent(), "Url should be found for the given hash");
    assertEquals(hashExp, optionalUrlActual.get().getHash());
  }

  private Url createUrl(String hash, String url) {
    return Url.builder()
            .hash(hash)
            .url(url)
            .build();
  }
}