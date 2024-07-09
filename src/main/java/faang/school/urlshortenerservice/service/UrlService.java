package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.dto.UrlCreatedRequest;
import faang.school.urlshortenerservice.dto.UrlCreatedResponse;

public interface UrlService {

    UrlCreatedResponse createUrl(UrlCreatedRequest urlCreatedRequest);
}
