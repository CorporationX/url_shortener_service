package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.HashGenerator;
import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * <h2>Задание</h2>
 * <div>Создать бин {@code GeneratorHashScheduler}, который будет запускать джобу для удаления старых ассоциаций в
 * таблице url и сохранять освободившиеся хэши обратно в таблицу hash. Запускается раз в день.</div>
 * <h2>Критерии приема</h2>
 * <li>GeneratorHashScheduler — спринг бин с аннотацией {@code @Scheduled}.
 * Не забыть про {@code @EnableScheduling}.</li>
 * <li>cron лежит в конфиге и получается в @Scheduled через spring expression.</li>
 * <li>Джоба запускается раз в сутки по крону.</li>
 * <li>Джоба удаляет из таблицы url все записи старше 1 года с получением их хэшей (можно сделать
 * одновременно в postgres через returning слово).</li>
 * <li>Эти хэши переносятся в таблицу hash.</li>
 * <li>Все происходит в рамках одной транзакции.</li>
 * <li>Для доступа к данным используются бины UrlRepository и HashRepository.</li>
 * <li>Везде используются lombok аннотации.</li>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GeneratorHashScheduler {

    private final UrlService urlService;
    private final HashGenerator hashGenerator;

    @Value("${app.cleaner.interval:10}")
    private int interval;
    @Value("${app.generator.max_amount:10}")
    private Long hashRange;

    @Scheduled(cron = "${app.cleaner.cron.expression}")
    public void doCleaner() {
        log.info("Cleaner old hashes started");
        urlService.clearOldUrls(interval);
    }

    @Scheduled(cron = "${app.generator.cron.expression}")
    public void generateBatchBySchedule() {
        log.info("Generate batch urls started");
        hashGenerator.generateBatchBySchedule(hashRange);
    }
}
