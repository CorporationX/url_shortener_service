package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.repository.HashRepository;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Сервис для работы с хэшами коротких URL.
 * <p>
 * Предоставляет методы получения свободных хэшей и сохранения новых хэшей в базу данных.
 * При получении хэшей инициирует фоновую генерацию новых через {@link HashGenerator}.
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class HashService {
    private final HashGenerator hashGenerator;
    private final HashRepository hashRepository;
    @Resource(name = "hashGeneratorExecutor")
    private final ExecutorService executorService;

    /**
     * Возвращает заданное количество свободных хэшей из базы данных.
     * <p>
     * После получения хэшей запускает в фоне проверку и генерацию новых хэшей, если их становится мало.
     *
     * @param count количество хэшей, которое требуется получить
     * @return список строк-хэшей
     */
    public List<String> getHashes(int count) {
        List<String> hashes = hashRepository.getHashes(count);
        executorService.submit(hashGenerator::checkAndGenerateHashesAsync);

        return hashes;
    }

    /**
     * Сохраняет список новых сгенерированных хэшей в базу данных.
     * Используется, например, для пополнения пула свободных хэшей.
     *
     * @param hashList список строк-хэшей для сохранения
     */
    @Transactional
    public void saveFreeHashes(List<String> hashList) {
        log.debug("Adding free hashes count: {}", hashList.size());
        hashRepository.saveHashes(hashList);
    }
}
