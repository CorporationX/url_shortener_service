package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.entity.Hash;
import org.springframework.stereotype.Service;


@Service
public class UrlServiceImpl implements UrlService {

    @Override
    public String getLongUrlByHash(Hash hash) {
        return "";
    }
}
