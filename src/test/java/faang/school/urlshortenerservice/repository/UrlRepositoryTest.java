package faang.school.urlshortenerservice.repository;

import faang.school.urlshortenerservice.entity.Url;
import faang.school.urlshortenerservice.util.BaseRepositoryTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UrlRepositoryTest extends BaseRepositoryTest {

    @Autowired
    private UrlRepository urlRepository;

    @Test
    public void testGetExpiredHashes() {
        List<String> result = urlRepository.getExpiredHashes();
        assertEquals(2, result.size());
    }

    @Test
    public void testFindByUrl() {
        Optional<Url> urlOptional = urlRepository.findByUrl("https://faang-school.com/courses");
        assertTrue(urlOptional.isPresent());
    }

    @Test
    public void testFindByNonExistingUrl() {
        Optional<Url> urlOptional = urlRepository.findByUrl("https://faang-school.com");
        assertTrue(urlOptional.isEmpty());
    }
}
