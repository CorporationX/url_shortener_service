package faang.school.urlshortenerservice.util.uri;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class UriBuilderTest {
    private static final String APP_URL = "http://localhost:8080/sh.com";
    private static final String HASH = "hash";

    private final UriBuilder uriBuilder = new UriBuilder();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(uriBuilder, "appUri", APP_URL);
    }

    @Test
    void testResponse_successful() {
        assertThat(uriBuilder.response(HASH))
                .isEqualTo(APP_URL + HASH);
    }
}