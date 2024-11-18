package faang.school.urlshortenerservice.repository.sequence;

import faang.school.urlshortenerservice.BaseContextTest;
import faang.school.urlshortenerservice.config.sequence.NumberSequenceProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

class UniqueNumberRepositoryTest extends BaseContextTest {

    @Autowired
    private UniqueNumberRepository uniqueNumberRepository;

    @Autowired
    private NumberSequenceProperties numberSequenceProperties;

    @Test
    void whenMethodCalledThenReturnExpectedListSize(){
        assertThat(uniqueNumberRepository.getUniqueNumbers()).hasSize(numberSequenceProperties.getGenerationBatch());
    }
}