package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.entity.UrlEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class UrlRepositoryTest {

    @Autowired
    private UrlRepository urlRepository;

    @Test
    public void testSaveAndFindById() {
        UrlEntity urlEntity = new UrlEntity("hash123", "http://example.com");
        urlRepository.save(urlEntity);

        UrlEntity foundEntity = urlRepository.findById("hash123").orElse(null);
        assertThat(foundEntity).isNotNull();
        assertThat(foundEntity.getLongUrl()).isEqualTo("http://example.com");
    }
}