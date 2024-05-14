package faang.school.urlshortenerservice.service;

import faang.school.urlshortenerservice.cache.HashCache;
import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.mapper.UrlMapper;
import faang.school.urlshortenerservice.model.Url;
import faang.school.urlshortenerservice.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlShortenerService {

    private final HashCache hashCache;
    private final UrlRepository urlRepository;
    private final UrlMapper urlMapper;

    public UrlDto shortenUrl(UrlDto originUrl){

        //should I validate here if this url already exists in Repo??

        Url url = urlMapper.toEntity(originUrl);
        url.setHash(hashCache.getHash().getHash());

        return urlMapper.toDto(urlRepository.save(url));
    }

}

//Создать POST /url эндпоинт, который будет принимать длинную ссылку в качестве тела запроса,
// а возвращать короткую ссылку, которая редиректит пользователя на длинную при переходе.
//
//Нужно реализовать весь путь: получение запроса, валидация переданного URL
// (что это вообще URL, а не что-то левое или пустота), получение хэша из HashCache бина
// (он будет разрабатываться в отдельной задаче — здесь просто используем его API, мокаем, если нужно),
// сохранение ассоциации хэша и URL в базу и в Redis.
//
//Критерии приема
//Это POST /url эндпоинт, который принимает DTO в качестве тела запроса.
//
//Есть валидация переданных данных в контроллере, что это вообще корректный URL.
//
//Метод-обработчик запроса находится в классе UrlController. Класс UrlController —
// Spring bean с соответствующими аннотациями.
//
//UrlService получается url пользователя, обращается в HashCache за хэшом для него и
// сохраняет ассоциацию хэши и url в БД и в Redis. UrlService — spring bean.
//
//Для сохранения в БД используется UrlRepository, который сохраняет эти данные в таблицу url.
// Это spring bean.
//
//UrlCacheRepository сохраняет данные в Redis. Это spring bean.
//
//Везде используются аннотации lombok.
//
//Все spring-аннотации отражают роли бинов.