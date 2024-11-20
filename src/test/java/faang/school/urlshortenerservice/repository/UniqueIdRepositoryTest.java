package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.model.entity.Url;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class UniqueIdRepositoryTest {

    @Autowired
    private UniqueIdRepository uniqueIdRepository;

    @Test
    public void testSaveAndFindById() {
        Url urlEntity = new Url("shortUrl123", "http://example.com");
        uniqueIdRepository.save(urlEntity);

        Url foundEntity = uniqueIdRepository.findById(urlEntity.getId()).orElse(null);
        assertThat(foundEntity).isNotNull();
        assertThat(foundEntity.getShortUrl()).isEqualTo("shortUrl123");
        assertThat(foundEntity.getLongUrl()).isEqualTo("http://example.com");
    }
}