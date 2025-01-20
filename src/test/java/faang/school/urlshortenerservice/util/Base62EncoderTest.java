package faang.school.urlshortenerservice.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.LongStream;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@Slf4j
class Base62EncoderTest {

  private final Base62Encoder base62Encoder = new Base62Encoder();

  @Test
  @DisplayName("return list of encoded numbers")
  void encode() {
    List<Long> numbers = LongStream.rangeClosed(1000L, 1010L).boxed().toList();

    List<String> expected = List.of("G8", "G9", "GA", "GB", "GC", "GD", "GE", "GF", "GG", "GH",
        "GI");

    List<String> encoded = base62Encoder.encode(numbers);

    assertEquals(expected, encoded);
  }
}