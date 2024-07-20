package faang.school.urlshortenerservice.repositoy;

import faang.school.urlshortenerservice.entity.Hash;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HashRepository {

    private final HashJpaRepository hashJpaRepository;

    public void save(List<Hash> hashes) {
        hashJpaRepository.saveAll(hashes);
    }
}
