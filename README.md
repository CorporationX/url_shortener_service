# URL Shortener Service

Сервис для сокращения URL-адресов с возможностью кэширования часто используемых ссылок.

## Технологии
- Java 17
- Spring Boot 3.4.3
- React 18
- TypeScript

## Функциональность
- Создание коротких URL
- Перенаправление по короткому URL на оригинальный адрес
- Кэширование часто используемых URL
- REST API с документацией Swagger
- Веб-интерфейс для создания и мониторинга сокращенных ссылок

## Запуск проекта

### Предварительные требования
- JDK 17
- Node.js 16+
- Docker и Docker Compose

### Пошаговая инструкция

1. Клонируйте репозиторий:
```bash
git clone https://github.com/therealadik/url_shortener_service.git
cd url_shortener_service
```

2. Соберите backend:
```bash
./gradlew clean build -x test
```

3. Соберите frontend:
```bash
cd frontend
npm install
cd ..
```

4. Запустите все сервисы через Docker Compose:
```bash
docker-compose up -d
```

### Доступ к приложению

После запуска всех компонентов:
- Веб-интерфейс: http://localhost
- Swagger UI: http://localhost:8080/swagger-ui.html

## Лицензия

Этот проект является открытым исходным кодом и распространяется под лицензией MIT.

Вы можете свободно использовать, изменять и распространять код проекта в соответствии с условиями лицензии MIT.