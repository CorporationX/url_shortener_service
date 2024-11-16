package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Url;

import java.util.List;

public interface UrlService {

    List<String> deleteUnusedHashes();

    void updateUrls(List<Url> urls);
}
