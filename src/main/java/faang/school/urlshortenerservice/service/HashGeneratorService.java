package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.HashEntity;
import faang.school.urlshortenerservice.repository.HashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HashGeneratorService {

    private final JdbcTemplate jdbcTemplate;
    private final HashRepository hashRepository;

    private static final String SEQ_QUERY = "SELECT nextval('unique_number_seq')";

    @Transactional
    public List<String> generateHashes(int count) {
        List<HashEntity> hashes = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Long nextVal = jdbcTemplate.queryForObject(SEQ_QUERY, Long.class);
            String hash = encodeBase62(nextVal);
            hashes.add(new HashEntity(hash));
        }
        hashRepository.saveAll(hashes);
        return hashes.stream().map(HashEntity::getHash).toList();
    }

    private static final String ALPHABET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int BASE = ALPHABET.length();

    private String encodeBase62(Long number) {
        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            sb.append(ALPHABET.charAt((int) (number % BASE)));
            number /= BASE;
        }
        while (sb.length() < 6) {
            sb.append('0');
        }
        return sb.reverse().toString();
    }
}
