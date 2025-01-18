package faang.school.urlshortenerservice.utils;

import faang.school.urlshortenerservice.entity.HashEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class Base62EncoderTest {
    @InjectMocks
    private Base62Encoder base62Encoder;

    private static final String BASE62_CHARSET = "4fC8IsoSdr2L79TQiOaek5uNZh1ztmGUvjPwAVpXy3JYclnB0EKqHMxWFDg6Rb";

    @Test
    void shouldEncodeNumbersToBase62() {
        List<Long> numbers = List.of(1L, 123L, 456789L, 987654321L, Long.MAX_VALUE);
        List<HashEntity> encodedHashes = base62Encoder.encode(numbers);

        assertThat(encodedHashes).hasSize(numbers.size());

        for (HashEntity hashEntity : encodedHashes) {
            String hash = hashEntity.getHash();
            assertThat(hash).isNotBlank();
            assertThat(hash).matches("[" + BASE62_CHARSET + "]+");
        }
    }

    @Test
    void shouldGenerateUniqueHashesForUniqueNumbers() {
        List<Long> numbers = LongStream.rangeClosed(1, 1_000_000).boxed().collect(Collectors.toList());

        List<HashEntity> encodedHashes = base62Encoder.encode(numbers);

        Set<String> uniqueHashes = encodedHashes.stream()
                .map(HashEntity::getHash)
                .collect(Collectors.toSet());

        assertThat(uniqueHashes).hasSize(numbers.size());
    }

    @Test
    void shouldNotReturnEmptyHash() {
        List<HashEntity> encoded = base62Encoder.encode(List.of(100L, 1000L, 10000L));

        for (HashEntity hashEntity : encoded) {
            assertThat(hashEntity.getHash()).isNotBlank();
        }
    }
}