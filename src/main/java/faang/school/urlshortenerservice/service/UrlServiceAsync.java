package faang.school.urlshortenerservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlServiceAsync {
    private final UrlService urlService;

    @Async("schedulerThreadPool")
    public void deleteOldUrlAsync(){
        urlService.deleteOldUrl();
    }
}
