package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UrlVisitServiceTest {

    private UrlRepository urlRepository;
    private UrlVisitService urlVisitService;

    @BeforeEach
    void setUp() {
        urlRepository = mock(UrlRepository.class);
        urlVisitService = new UrlVisitService(urlRepository);
    }

    @Test
    @DisplayName("Should increment visits count successfully")
    void shouldIncrementVisitsCount() {
        String hashValue = "abc123";
        long newCount = 42L;
        when(urlRepository.incrementVisitsCount(hashValue)).thenReturn(Optional.of(newCount));
        urlVisitService.incrementVisits(hashValue);
        verify(urlRepository).incrementVisitsCount(hashValue);
    }

    @Test
    @DisplayName("Should handle empty result when incrementing visits")
    void shouldHandleEmptyResult() {
        String hashValue = "abc123";
        when(urlRepository.incrementVisitsCount(hashValue)).thenReturn(Optional.empty());
        urlVisitService.incrementVisits(hashValue);
        verify(urlRepository).incrementVisitsCount(hashValue);
    }

    @Test
    @DisplayName("Should handle null hash value")
    void shouldHandleNullHashValue() {
        urlVisitService.incrementVisits(null);
        verify(urlRepository).incrementVisitsCount(null);
    }

    @Test
    @DisplayName("Should handle repository exception")
    void shouldHandleRepositoryException() {
        String hashValue = "abc123";
        RuntimeException expectedException = new RuntimeException("Database error");
        when(urlRepository.incrementVisitsCount(hashValue)).thenThrow(expectedException);
        assertThatThrownBy(() -> urlVisitService.incrementVisits(hashValue))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Database error");
        verify(urlRepository).incrementVisitsCount(hashValue);
    }
}