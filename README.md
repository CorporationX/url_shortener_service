# URL Shortener Service

Микросервис для сокращения URL с использованием Spring Boot, PostgreSQL и Redis.

## Особенности

- **Base62 кодирование** — генерация хэшей длиной от 6 символов
- **Sequence-based генерация** — уникальные хэши через PostgreSQL sequence
- **Предзагрузка хэшей** — асинхронная генерация каждые 5 минут (1000 штук)
- **Двухуровневое кеширование** — Redis + in-memory очередь
- **Автоматическая чистка** — удаление URL старше 1 года (ежедневно в 3:00)
- **Обработка ошибок** — централизованный @RestControllerAdvice

## Технологии

- Java 17
- Spring Boot 3.x
- PostgreSQL (с Liquibase миграциями)
- Redis (кеш URL)
- Lombok
- JUnit 5 + Mockito

## Архитектура

```
POST /shorten → UrlController → UrlService → HashCache → [Postgres + Redis]
GET /{hash}  → UrlController → UrlService → [Redis → Postgres] → 302 Redirect
```

**Компоненты:**
- **HashGenerator** — генерирует хэши через @Scheduled (async)
- **HashCache** — очередь готовых хэшей (thread-safe)
- **CleanerScheduler** — удаляет старые URL (cron: `0 0 3 * * ?`)
- **Base62Encoder** — кодирует sequence numbers в хэши

## API

### Сократить URL
```
POST /shorten
Content-Type: application/json

{
"url": "https://example.com/very/long/url"
}
```

**Response (200 OK):**
```
{
"shortUrl": "http://short.url/abc123"
}
```

**Ошибки:**
- `400 Bad Request` — невалидный URL
- `503 Service Unavailable` — нет доступных хэшей

### Получить оригинальный URL
```
GET /{hash}
```

**Response:** HTTP 302 редирект на оригинальный URL

**Ошибки:**
- `404 Not Found` — хэш не найден

## Переменные окружения

| Переменная | Описание | Пример |
|------------|----------|--------|
| `POSTGRES_URL` | URL PostgreSQL | `jdbc:postgresql://localhost:5432/urlshortener` |
| `POSTGRES_USER` | Пользователь БД | `postgres` |
| `POSTGRES_PASSWORD` | Пароль БД | `password` |
| `REDIS_HOST` | Хост Redis | `localhost` |
| `REDIS_PORT` | Порт Redis | `6379` |
| `BASE_URL` | Базовый URL сервиса | `http://short.url` |
| `HASH_BATCH_SIZE` | Размер batch генерации | `1000` |

## Запуск

### Docker Compose
```
docker-compose up -d
```

### Локально
```
# Запуск БД
docker-compose up -d postgres redis

# Запуск приложения
./gradlew bootRun
```

## Структура проекта

```
src/main/java/
├── controller/       # REST endpoints
├── service/          # Бизнес-логика
├── repository/       # БД + Redis
├── generator/        # Генерация хэшей
├── scheduler/        # Scheduled задачи
├── encoder/          # Base62 кодирование
├── exception/        # Обработка ошибок
└── dto/              # Data Transfer Objects

src/main/resources/
└── db/changelog/     # Liquibase миграции
```

## База данных

**Таблица `url`:**
- `hash` (PK) — 6+ символов
- `url` — оригинальный URL
- `created_at` — timestamp

**Таблица `hash`:**
- `hash` (PK) — свободный хэш

**Sequence `unique_number_seq`:**
- Генератор уникальных ID для хэшей

## Тестирование

```
./gradlew test
```

**Покрытие:**
- Base62EncoderTest — unit-тесты кодирования
- UrlServiceTest — тесты бизнес-логики с моками

## Логирование

Используется SLF4J через Lombok `@Slf4j`:
- **INFO** — основные операции (создание URL, чистка)
- **DEBUG** — детали (cache hit/miss)
- **ERROR** — ошибки (URL not found, нет хэшей)