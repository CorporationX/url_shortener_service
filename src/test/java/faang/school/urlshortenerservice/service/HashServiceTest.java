package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.HashProperties;
import faang.school.urlshortenerservice.repository.HashRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HashServiceTest {
    @Mock
    private HashRepository hashRepository;
    @Mock
    private HashProperties hashProperties;
    @InjectMocks
    private HashService hashService;

    @BeforeEach
    void setUp() {

    }
}