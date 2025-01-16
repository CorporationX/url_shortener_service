package faang.school.urlshortenerservice.service;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentLinkedQueue;

@Component
public class HashCache {

    private final ConcurrentLinkedQueue<String> cache = new ConcurrentLinkedQueue<>();
}
