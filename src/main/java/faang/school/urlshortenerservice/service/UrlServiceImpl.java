package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UrlServiceImpl {

    private final UrlRepository urlRepository;
    private final HashService hashService;
    @Transactional
    public void deleteUrlOlderOneYearAndSaveByHash(int limit) {
        List<String> hashes = urlRepository.findExpiredUrlsHashes(limit);
        urlRepository.deleteAllByIdInBatch(hashes);
    }
    @Transactional(readOnly = true)
    public int countUrlsOlder(){
        return urlRepository.countOfOldUrl();
    }
}
