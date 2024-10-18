package faang.school.urlshortenerservice.service.generator.encoder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Base62EncoderTest {
  private Base62Encoder encoder = new Base62Encoder();

  @Test
  void testSimpleEncoding() {
    // given
    String hashExp = "c";
    int number = 2;
    // when
    String hashActual = encoder.applyBase62Encoding(number);
    // then
    assertEquals(hashExp, hashActual, "hashes do not match");
  }

  @Test
  void testZeroEncoding() {
    // given
    String hashExp = "a";
    int number = 0;
    // when
    String hashActual = encoder.applyBase62Encoding(number);
    // then
    assertEquals(hashExp, hashActual, "hashes do not match");
  }

  @Test
  void testBoundaryValueEncoding() {
    // given
    String hashExp = "ab";
    int number = 62;
    // when
    String hashActual = encoder.applyBase62Encoding(number);
    // then
    assertEquals(hashExp, hashActual, "hashes do not match");
  }
}