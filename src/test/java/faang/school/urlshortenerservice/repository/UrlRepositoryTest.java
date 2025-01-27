package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.UrlAssociation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class UrlRepositoryTest {

    @Autowired
    private UrlRepository urlRepository;

    @Test
    @DisplayName("Save and find a URL entity")
    void saveAndFindUrlEntity() {

        UrlAssociation entity = new UrlAssociation();
        entity.setHash("abc123");
        entity.setUrl("https://example.com");
        entity.setTimestamp(LocalDateTime.now());


        urlRepository.save(entity);
        Optional<UrlAssociation> foundEntity = urlRepository.findById("abc123");


        assertThat(foundEntity).isPresent();
        assertThat(foundEntity.get().getUrl()).isEqualTo("https://example.com");
        assertThat(foundEntity.get().getHash()).isEqualTo("abc123");
    }

    @Test
    @DisplayName("Delete a URL entity")
    void deleteUrlEntity() {

        UrlAssociation entity = new UrlAssociation();
        entity.setHash("xyz789");
        entity.setUrl("https://anotherexample.com");
        entity.setTimestamp(LocalDateTime.now());

        urlRepository.save(entity);


        urlRepository.deleteById("xyz789");
        Optional<UrlAssociation> deletedEntity = urlRepository.findById("xyz789");


        assertThat(deletedEntity).isNotPresent();
    }
}
