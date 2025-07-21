# ShareIt - Сервис Шаринга Вещей

ShareIt - это веб-приложение для обмена вещами между пользователями, построенное на микросервисной архитектуре. Пользователи могут добавлять свои вещи, бронировать вещи других пользователей и оставлять отзывы.

## Архитектура

Проект состоит из двух микросервисов:

### Gateway (Шлюз)
- **Порт**: 8080
- **Назначение**: Валидация входящих запросов, маршрутизация
- **Технологии**: Spring Boot, Spring Web, Spring Validation

### Server (Сервер)
- **Порт**: 9090
- **Назначение**: Бизнес-логика, работа с базой данных
- **Технологии**: Spring Boot, Spring Data JPA, PostgreSQL

### Database (База данных)
- **Порт**: 6543
- **Технология**: PostgreSQL
- **Контейнер**: Docker

## Функциональность

### Управление пользователями
- Создание, просмотр, обновление и удаление пользователей
- Уникальность email адресов

### Управление вещами
- Создание, просмотр, обновление вещей
- Поиск вещей по названию и описанию
- Просмотр доступных для аренды вещей
- Отображение информации о бронированиях для владельцев

### Система бронирования
- Создание запроса на бронирование
- Подтверждение/отклонение бронирования владельцем
- Просмотр бронирований пользователя
- Просмотр бронирований для вещей владельца
- Фильтрация бронирований по статусу (ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED)

### Система отзывов
- Добавление комментариев к вещам
- Только пользователи, бравшие вещь в аренду, могут оставлять отзывы
- Просмотр комментариев при просмотре вещей

### Система запросов на вещи
- Создание запросов на нужные вещи
- Просмотр запросов и ответов на них

## Технический стек

- **Java 21**
- **Spring Boot 3.3.2**
- **Spring Data JPA**
- **Spring Web**
- **Spring Validation**
- **PostgreSQL** (продакшн)
- **H2** (тесты)
- **Maven**
- **Lombok**
- **JUnit 5 + Mockito** (тестирование)
- **Docker & Docker Compose**

## Структура проекта

```
java-shareit/
├── docker-compose.yml         # Docker окружение
├── gateway/                   # Микросервис Gateway
│   ├── src/main/java/ru/practicum/shareit/
│   │   ├── booking/
│   │   │   ├── BookingClient.java      # HTTP клиент для server
│   │   │   ├── BookingController.java  # REST контроллер
│   │   │   ├── BookingDto.java         # DTO для валидации
│   │   │   └── dto/
│   │   │       ├── BookingState.java   # Enum состояний
│   │   │       └── BookItemRequestDto.java
│   │   ├── client/
│   │   │   └── BaseClient.java         # Базовый HTTP клиент
│   │   ├── exception/
│   │   │   └── GlobalExceptionHandler.java # Обработка исключений
│   │   ├── item/
│   │   │   ├── ItemClient.java         # HTTP клиент для server
│   │   │   ├── ItemController.java     # REST контроллер
│   │   │   ├── ItemDto.java            # DTO для валидации
│   │   │   └── CommentDto.java
│   │   ├── request/
│   │   │   ├── ItemRequestClient.java  # HTTP клиент для server
│   │   │   ├── ItemRequestController.java
│   │   │   └── ItemRequestDto.java
│   │   ├── user/
│   │   │   ├── UserClient.java         # HTTP клиент для server
│   │   │   ├── UserController.java     # REST контроллер
│   │   │   └── UserDto.java            # DTO для валидации
│   │   └── ShareItGateway.java         # Main класс Gateway
│   └── pom.xml
├── server/                    # Микросервис Server
│   ├── src/main/java/ru/practicum/shareit/
│   │   ├── booking/
│   │   │   ├── Booking.java            # JPA сущность
│   │   │   ├── BookingController.java  # REST контроллер
│   │   │   ├── BookingState.java       # Enum состояний
│   │   │   ├── BookingStatus.java      # Enum статусов
│   │   │   ├── dto/                    # DTO классы
│   │   │   ├── mapper/                 # Мапперы
│   │   │   ├── repository/             # JPA репозитории
│   │   │   └── service/                # Бизнес-логика
│   │   ├── exception/                  # Обработка исключений
│   │   ├── item/
│   │   │   ├── dto/                    # DTO классы
│   │   │   ├── mapper/                 # Мапперы
│   │   │   ├── model/                  # JPA сущности (Item, Comment)
│   │   │   ├── repository/             # JPA репозитории
│   │   │   ├── service/                # Бизнес-логика
│   │   │   └── ItemController.java     # REST контроллер
│   │   ├── request/
│   │   │   ├── dto/                    # DTO классы
│   │   │   ├── ItemRequest.java        # JPA сущность
│   │   │   ├── ItemRequestController.java
│   │   │   ├── ItemRequestMapper.java  # Маппер
│   │   │   ├── repository/             # JPA репозитории
│   │   │   └── service/                # Бизнес-логика
│   │   ├── user/
│   │   │   ├── controller/             # REST контроллер
│   │   │   ├── dto/                    # DTO классы
│   │   │   ├── mapper/                 # Мапперы
│   │   │   ├── model/                  # JPA сущность User
│   │   │   ├── repository/             # JPA репозиторий
│   │   │   └── service/                # Бизнес-логика
│   │   └── ShareItServer.java          # Main класс Server
│   └── pom.xml
├── postman/                   # Postman коллекция для тестирования
└── pom.xml                    # Родительский POM
```

## API Endpoints

Все запросы отправляются на Gateway (порт 8080), который валидирует их и перенаправляет на Server.

### Пользователи
- `GET /users` - получить всех пользователей
- `GET /users/{userId}` - получить пользователя по ID
- `POST /users` - создать пользователя
- `PATCH /users/{userId}` - обновить пользователя
- `DELETE /users/{userId}` - удалить пользователя

### Вещи
- `GET /items` - получить вещи пользователя (с информацией о бронированиях для владельца)
- `GET /items/{itemId}` - получить вещь по ID
- `POST /items` - создать вещь
- `PATCH /items/{itemId}` - обновить вещь
- `GET /items/search?text={text}` - поиск вещей
- `POST /items/{itemId}/comment` - добавить комментарий

### Бронирования
- `POST /bookings` - создать бронирование
- `PATCH /bookings/{bookingId}?approved={approved}` - подтвердить/отклонить бронирование
- `GET /bookings/{bookingId}` - получить бронирование по ID
- `GET /bookings?state={state}` - получить бронирования пользователя
- `GET /bookings/owner?state={state}` - получить бронирования для вещей владельца

### Запросы на вещи
- `POST /requests` - создать запрос на вещь
- `GET /requests` - получить свои запросы
- `GET /requests/all` - получить все запросы других пользователей
- `GET /requests/{requestId}` - получить запрос по ID

## Запуск приложения

### Через Docker Compose (рекомендуется)
```bash
# Клонируйте репозиторий
git clone <repository-url>
cd java-shareit

# Запуск всех сервисов
docker-compose up -d

# Остановка всех сервисов
docker-compose down

# Просмотр логов
docker-compose logs -f
```

После запуска будут доступны:
- Gateway: http://localhost:8080
- Server: http://localhost:9090
- PostgreSQL: localhost:6543

### Локальный запуск для разработки

#### Предварительные требования
- Java 21
- Maven 3.6+
- PostgreSQL 13+

#### Запуск базы данных
```bash
# Через Docker
docker run -d \
  --name shareit-db \
  -p 6543:5432 \
  -e POSTGRES_DB=shareit \
  -e POSTGRES_USER=shareit \
  -e POSTGRES_PASSWORD=shareit \
  postgres:13
```

#### Запуск Server
```bash
cd server
mvn clean package -DskipTests
java -jar target/shareit-server-0.0.1-SNAPSHOT.jar --server.port=9090
```

#### Запуск Gateway
```bash
cd gateway
mvn clean package -DskipTests
java -jar target/shareit-gateway-0.0.1-SNAPSHOT.jar --server.port=8080
```

## Тестирование

### Автоматические тесты
```bash
# Запуск всех тестов
mvn test

# Запуск тестов только для gateway
cd gateway && mvn test

# Запуск тестов только для server
cd server && mvn test
```

### Postman коллекция
В папке `postman/` находится коллекция для тестирования API. Импортируйте файл `sprint.json` в Postman.

## Настройка окружения

### Конфигурация Gateway
`gateway/src/main/resources/application.properties`:
```properties
server.port=8080
shareit-server.url=http://localhost:9090
logging.level.ru.practicum=DEBUG
```

### Конфигурация Server
`server/src/main/resources/application.properties`:
```properties
server.port=9090
spring.datasource.url=jdbc:postgresql://localhost:6543/shareit
spring.datasource.username=shareit
spring.datasource.password=shareit
spring.jpa.hibernate.ddl-auto=none
spring.sql.init.mode=always
spring.sql.init.schema-locations=classpath:schema.sql
```

### Схема базы данных
Схема создается автоматически при запуске из файла `server/src/main/resources/schema.sql`. Включает таблицы:
- `users` - пользователи
- `items` - вещи
- `bookings` - бронирования
- `comments` - комментарии
- `requests` - запросы на вещи

## Разделение ответственности

### Gateway отвечает за:
- Валидацию входящих запросов
- Авторизацию пользователей (заголовок X-Sharer-User-Id)
- Маршрутизацию запросов на Server
- Обработку ошибок валидации

### Server отвечает за:
- Бизнес-логику приложения
- Работу с базой данных
- Обработку бизнес-исключений
- Управление транзакциями

## Примеры использования API

### Создание пользователя
```bash
POST http://localhost:8080/users
Content-Type: application/json

{
    "name": "John Doe",
    "email": "john@example.com"
}
```

### Создание вещи
```bash
POST http://localhost:8080/items
X-Sharer-User-Id: 1
Content-Type: application/json

{
    "name": "Дрель",
    "description": "Мощная электрическая дрель",
    "available": true
}
```

### Создание бронирования
```bash
POST http://localhost:8080/bookings
X-Sharer-User-Id: 2
Content-Type: application/json

{
    "itemId": 1,
    "start": "2025-07-10T10:00:00",
    "end": "2025-07-12T10:00:00"
}
```

### Подтверждение бронирования
```bash
PATCH http://localhost:8080/bookings/1?approved=true
X-Sharer-User-Id: 1
```

### Добавление комментария
```bash
POST http://localhost:8080/items/1/comment
X-Sharer-User-Id: 2
Content-Type: application/json

{
    "text": "Отличная дрель, рекомендую!"
}
```

### Создание запроса на вещь
```bash
POST http://localhost:8080/requests
X-Sharer-User-Id: 1
Content-Type: application/json

{
    "description": "Нужна дрель для ремонта"
}
```

## Статусы бронирования

- **WAITING** - новое бронирование, ожидает подтверждения
- **APPROVED** - бронирование подтверждено владельцем
- **REJECTED** - бронирование отклонено владельцем

## Состояния для фильтрации бронирований

- **ALL** - все бронирования
- **CURRENT** - текущие бронирования
- **PAST** - завершенные бронирования
- **FUTURE** - будущие бронирования
- **WAITING** - ожидающие подтверждения
- **REJECTED** - отклоненные

## Мониторинг и отладка

### Логи
```bash
# Просмотр логов всех сервисов
docker-compose logs -f

# Просмотр логов конкретного сервиса
docker-compose logs -f gateway
docker-compose logs -f server
```

### Проверка здоровья сервисов
```bash
# Gateway
curl http://localhost:8080/actuator/health

# Server
curl http://localhost:9090/actuator/health
```

### Подключение к базе данных
```bash
docker exec -it shareit-db psql -U shareit -d shareit
```

## Архитектурные решения

1. **Микросервисная архитектура** - разделение на gateway и server позволяет независимо масштабировать валидацию и бизнес-логику
2. **Разделение ответственности** - gateway выполняет валидацию, server - бизнес-логику
3. **HTTP-клиенты** - gateway взаимодействует с server через HTTP REST API
4. **Контейнеризация** - все сервисы запускаются в Docker контейнерах
5. **Единая точка входа** - все клиентские запросы проходят через gateway
