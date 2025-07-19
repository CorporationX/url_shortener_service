package faang.school.urlshortenerservice.scheduler;

import faang.school.urlshortenerservice.service.UrlService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * <h2>Задание</h2>
 * <div>Создать бин {@code CleanerScheduler}, который будет запускать джобу для удаления старых ассоциаций в
 * таблице url и сохранять освободившиеся хэши обратно в таблицу hash. Запускается раз в день.</div>
 * <h2>Критерии приема</h2>
 * <li>CleanerScheduler — спринг бин с аннотацией {@code @Scheduled}. Не забыть про {@code @EnableScheduling}.</li>
 * <li>cron лежит в конфиге и получается в @Scheduled через spring expression.</li>
 * <li>Джоба запускается раз в сутки по крону.</li>
 * <li>Джоба удаляет из таблицы url все записи старше 1 года с получением их хэшей (можно сделать
 * одновременно в postgres через returning слово).</li>
 * <li>Эти хэши переносятся в таблицу hash.</li>
 * <li>Все происходит в рамках одной транзакции.</li>
 * <li>Для доступа к данным используются бины UrlRepository и HashRepository.</li>
 * <li>Везде используются lombok аннотации.</li>
 */
@Service
@RequiredArgsConstructor
public class CleanerScheduler {

    private final UrlService urlService;

    @Scheduled(cron = "${app.cleaner.cron.expression}")
    public void doCleaner() {
        urlService.clearOldUrls();
    }
}
