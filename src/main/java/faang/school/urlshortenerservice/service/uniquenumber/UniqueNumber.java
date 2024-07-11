package faang.school.urlshortenerservice.service.uniquenumber;

import faang.school.urlshortenerservice.repository.uniquenumber.UniqueNumberRepositoryCustom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.LongStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class UniqueNumber {
    private final UniqueNumberRepositoryCustom uniqueNumberRepository;

    @Transactional
    public List<Long> getUniqueNumbers(long count) {
        log.info("Received query to take n = {} unique numbers", count);
        long lastUniqueNumberInBD = uniqueNumberRepository.getLastUniqueNumber();
        log.debug("Last unique number from DB = {}", lastUniqueNumberInBD);
        long finalNumber = lastUniqueNumberInBD + count;

        List<Long> uniqueNumbers = LongStream.rangeClosed(lastUniqueNumberInBD, finalNumber - 1).boxed().toList();
        uniqueNumberRepository.setLastUniqueNumber(finalNumber);
        log.debug("Update last unique number from DB = {}", finalNumber + 1);

        return uniqueNumbers;
    }
}
