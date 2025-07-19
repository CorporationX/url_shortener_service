package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.model.Hash;
import faang.school.urlshortenerservice.repository.HashRepository;
import faang.school.urlshortenerservice.util.Base62Encoder;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <h2>Задание</h2>
 * <div>Создать бин HashGenerator для генерации и помещения новых хэшей в БД. Метод generateBatch().</div>
 * <h2>Критерии приема</h2>
 * <li>HashGenerator — Spring bean</li>
 * <li>Метод generateBatch сначала обращается к HashRepository, чтобы получить n уникальных
 * чисел из БД. Это n хранится в конфиге, а не захардкожено.</li>
 * <li>Затем generateBatch отдаёт полученный список чисел в Base62Encoder, который возвращает уже список хэшей.</li>
 * <li>Далее generateBatch список этих хэшей сохраняет в БД через HashRepository в таблицу hash.</li>
 * <li>Метод должен быть Async.</li>
 * <li>Async имеет свой кастомный трэд пул.</li>
 * <li>Трэд пул создаётся в конфигурации отдельным бином с соответсвующим именем. Его размер, и размер
 * его очереди задач, задаются через конфиг.</li>
 * <li>Везде используются lombok аннотации.</li>
 */
@Slf4j
@Setter
@Service
@RequiredArgsConstructor
public class HashGenerator {

    @Value("${app.generator.max_amount:10}")
    private Long maxAmount;
    @Value("${app.min-percent:3}")
    private int minPercent;
    @Value("${app.generator.coeff:10}")
    private int coeff;

    private final HashRepository hashRepository;
    private final Base62Encoder base62Encoder;

    private final AtomicBoolean lock = new AtomicBoolean(false);

    @Transactional
    public List<String> getHashes(Long range) {
        do {
            log.debug("Getting hashes for range {}", range);
            List<String> hashes = hashRepository.getPortionOfHashes(range);
            if (hashes.isEmpty()) {
                generateBatch(range * coeff);
            } else {
                return hashes;
            }
        } while (true);
    }


    @Scheduled(cron = "${app.generator.cron.expression}")
    @Transactional
    public void generateBatchBySchedule() {
        log.debug("Try generating batch of {} hashes", maxAmount);
        if (isNeedGenerate()) {
            generateBatch(maxAmount);
        } else {
            log.info("No batch of {} hashes", maxAmount);
        }
    }

    private void generateBatch(Long range) {
        List<Long> numbers = hashRepository.getUniqueNumbers(range);
        List<Hash> hashes = base62Encoder.encode(numbers).stream()
            .map(hash -> Hash.builder().hash(hash).build())
            .toList();
        hashRepository.saveAll(hashes);
    }

    private boolean isNeedGenerate() {
        Long size = hashRepository.countAll();
        log.info("count hash in DB: {}", size);
        return size < (maxAmount * minPercent / 100);
    }
}
