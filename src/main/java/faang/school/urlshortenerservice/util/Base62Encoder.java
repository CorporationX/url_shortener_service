package faang.school.urlshortenerservice.util;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.service.HashService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.UrlBase64;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class Base62Encoder {
    private final HashService hashService;

    private int charLength;

    @PostConstruct
    private void setUp() {
        charLength = hashService.getCharLength();
    }

    public List<Hash> encodeList(List<Long> nums) {
        return nums.stream()
                .map(this::encode)
                .map(Hash::new)
                .toList();
    }

    private String encode(long num) {
        byte[] input = String.valueOf(num).getBytes();
        byte[] hash = getSha256Hash(input);

        byte[] encodedBytes = UrlBase64.encode(hash);
        String encoded = new String(encodedBytes);

        return ensureFixedLength(encoded);
    }

    private static byte[] getSha256Hash(byte[] input) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(input);
        } catch (NoSuchAlgorithmException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    private String ensureFixedLength(String str) {
        if (str.length() >= charLength) {
            return str.substring(0, charLength);
        } else {
            return String.format("%1$" + charLength + "s", str).replace(' ', '0');
        }
    }
}
