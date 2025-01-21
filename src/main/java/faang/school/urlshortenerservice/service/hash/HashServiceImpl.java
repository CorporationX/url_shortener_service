package faang.school.urlshortenerservice.service.hash;

import faang.school.urlshortenerservice.mapper.hash.HashMapper;
import faang.school.urlshortenerservice.repository.hash.HashRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class HashServiceImpl implements HashService {

    private final HashRepository hashRepository;
    private final HashMapper hashMapper;

    @Override
    public void addHashes(Set<String> hashes) {
        hashRepository.saveAll(hashMapper.toEntity(hashes));
        log.info("Hashes added to the database");
    }
}
