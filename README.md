# Url shortener service
Весь функционал реализован только мной. URL Shortener Service — это микросервис, позволяющий преобразовать длинный URL-адрес в более короткий и удобный для использования. Это особенно полезно в социальных сетях, рекламных материалах и других местах, где количество символов ограничено или где длинные ссылки могут выглядеть неаккуратно.

Основной функцией сервиса является сокращение URL-адресов, однако он также обладает функциональностью кэширования, многопоточности, работой с базой данных Postgres и Redis, REST-интерфейсом и планировщиком переиспользования ресурсов.

Для оптимизации работы микросервиса используется локальный кэш прямо в памяти в потокобезопасной структуре данных, что значительно повышает эффективность работы сервиса.

Данная реализация основана на алгоритме BASE62. Внутри используется асинхронизм для оптимизации работы при высоких нагрузках.

[Кэш](https://github.com/CorporationX/url_shortener_service/blob/dragon-master-bc3-BJS2-6686/src/main/java/faang/school/urlshortenerservice/cache/HashCache.java) используется для хранения заранее сгенерированных хэшей для ссылок. Есть коофицент заполнения по истечению которого начинает генерацию хэшей в отдельном потоке

[Контроллер](https://github.com/CorporationX/url_shortener_service/blob/dragon-master-bc3-BJS2-6686/src/main/java/faang/school/urlshortenerservice/controller/UrlController.java) как точка входа в приложение. Используется для выдачи ссылок и редиректа по основной ссылке

[Converter](https://github.com/CorporationX/url_shortener_service/blob/dragon-master-bc3-BJS2-6686/src/main/java/faang/school/urlshortenerservice/encoder/Converter.java) принимает на вход коллекцию чисел и возвращает коллекцию сгенерированных хэшей. По умолчанию используется алгоритм **Base62**, в `application.yaml` можно настроить параметры кодировки.

[Клинер](https://github.com/CorporationX/url_shortener_service/blob/dragon-master-bc3-BJS2-6686/src/main/java/faang/school/urlshortenerservice/service/HashService.java) очищает устаревшие хэши и возвращает их в базу. Используется конфигурирование параметров

[Генератор хэшей](https://github.com/CorporationX/url_shortener_service/blob/dragon-master-bc3-BJS2-6686/src/main/java/faang/school/urlshortenerservice/generator/HashGenerator.java) получает из бд уникальную последовательность, из которой создаёт хэши и сохраняет в другой таблице.

[Сервис](https://github.com/CorporationX/url_shortener_service/blob/dragon-master-bc3-BJS2-6686/src/main/java/faang/school/urlshortenerservice/service/UrlService.java) соединяет всю вышеописаную логику вместе.