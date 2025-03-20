package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class UrlService {

    private final UrlRepository urlRepository;
    private final HashService hashService;

    @Value("${url-shortener.batch-size}")
    private int batchSize;

    @Transactional
    public void deleteOldUrl() {
        LocalDateTime dateDelete = LocalDateTime.now().minusYears(1);

        int totalCount = urlRepository.countByCreatedAtBefore(dateDelete);
        int countPages = (totalCount + batchSize - 1) / batchSize;

        IntStream.range(0, countPages).forEach(i -> deleteUrlAndSaveHash(dateDelete, i));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void deleteUrlAndSaveHash(LocalDateTime date, int pageNumber) {
        List<Url> oldUrls = urlRepository.findByCreatedAtBefore(date, PageRequest.of(pageNumber, batchSize));
        List<String> hashes = oldUrls.stream().map(Url::getHash).toList();
        urlRepository.deleteAll(oldUrls);
        hashService.saveHashByBatch(hashes.stream()
                .map(id -> Hash.builder().hash(id).build())
                .toList());
    }
}
