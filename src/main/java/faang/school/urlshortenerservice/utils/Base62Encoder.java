package faang.school.urlshortenerservice.utils;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class Base62Encoder {
    private static final String BASE_62_CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private final HashRepository hashRepository;

    @Transactional
    public void encode(List<Long> numbers) {
        List<Hash> hashes = numbers.stream()
                .map(this::base62Encode)
                .map(Hash::new)
                .toList();
        hashRepository.saveAll(hashes);
    }

    private String base62Encode(long number) {
        StringBuilder builder = new StringBuilder();
        while (number > 0) {
            builder.append(BASE_62_CHARACTERS.charAt((int) (number % 62)));
            number /= 62;
        }
        return builder.toString();
    }
}
