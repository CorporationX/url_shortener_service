package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UrlServiceImpl implements UrlService {

    private final UrlRepository urlRepository;

    @Value("${url.constants.days-to-remove}")
    private int daysToRemove;

    @Override
    @Transactional
    public List<String> deleteUnusedHashes() {
        LocalDate today = LocalDate.now();
        return urlRepository.releaseUnusedHashesFrom(today.minusDays(daysToRemove));
    }

    @Override
    @Transactional
    public void updateUrls(List<Url> urls) {
        urlRepository.saveAll(urls);
    }
}
