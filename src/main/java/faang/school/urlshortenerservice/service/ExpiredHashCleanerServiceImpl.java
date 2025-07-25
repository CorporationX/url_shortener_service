package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.config.ConstantsProperties;
import faang.school.urlshortenerservice.repository.HashRepositoryJdbcImpl;
import faang.school.urlshortenerservice.repository.UrlRepositoryJdbcImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpiredHashCleanerServiceImpl implements ExpiredHashCleanerService{
    private final HashRepositoryJdbcImpl hashRepository;
    private final UrlRepositoryJdbcImpl urlRepository;
    private final ConstantsProperties constantsProperties;

    @Override
    @Transactional
    public int cleanUpBatch() {
        List<String> removedHashes = urlRepository.getHashesAndDelete(
                constantsProperties.getExpirationInterval(),
                constantsProperties.getCleanUpBatchSize()
        );
        hashRepository.save(removedHashes);
        return removedHashes.size();
    }
}
