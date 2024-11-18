package faang.school.urlshortenerservice.repository.hash;

import faang.school.urlshortenerservice.BaseContextTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FreeHashRepositoryTest extends BaseContextTest {

    private static final String deleteAllHashes = "DELETE FROM hash";
    private static final String findAllHashes = "SELECT hash FROM hash";

    @Autowired
    private FreeHashRepository freeHashRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final List<String> hashes =  Arrays.asList("hash1", "hash2", "hash3", "hash4", "hash5");

    @BeforeEach
    void setUp() {
        jdbcTemplate.execute(deleteAllHashes);
        freeHashRepository.saveHashes(hashes);
    }

    @Test
    void whenMethodCalledThenShouldSaveGivenListHashes() {
        List<String> savedHashes = jdbcTemplate.queryForList(findAllHashes, String.class);

        assertThat(savedHashes).containsExactlyInAnyOrderElementsOf(hashes);
    }

    @Test
    void whenMethodCalledThenShouldReturnExpectedHashes() {
        List<String> savedHashes = freeHashRepository.findAndDeleteFreeHashes(hashes.size());

        assertThat(savedHashes).containsExactlyInAnyOrderElementsOf(hashes);
    }

}
