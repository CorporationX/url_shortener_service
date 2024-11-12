package faang.school.urlshortenerservice.service.sequence;

import faang.school.urlshortenerservice.repository.sequence.UniqueNumberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UniqueNumberService {

    private final UniqueNumberRepository uniqueNumberRepository;

    @Transactional
    public List<Long> getUniqueNumbers() {
        return uniqueNumberRepository.getUniqueNumbers();
    }
}
