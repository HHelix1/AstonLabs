# User Service

Консольное CRUD-приложение на Java 17, Hibernate ORM 6 и PostgreSQL. Spring не используется.

## Требования

- JDK 17+
- Maven 3.8+
- PostgreSQL 13+

## 1. Создание базы данных

Войдите в PostgreSQL и выполните:

```sql
CREATE DATABASE user_service;
```

Hibernate автоматически создаст/обновит таблицу `users` при запуске (`hibernate.hbm2ddl.auto=update`).

Структура сущности:
- `id` — `BIGINT`, первичный ключ, генерируется БД;
- `name` — `VARCHAR(100)`, обязательное поле;
- `email` — `VARCHAR(150)`, обязательное уникальное поле;
- `age` — `INTEGER`, обязательное поле;
- `created_at` — `TIMESTAMP`, дата и время создания, не меняется при UPDATE.

## 2. Настройка подключения

По умолчанию в `src/main/resources/hibernate.cfg.xml` указаны:

- URL: `jdbc:postgresql://localhost:5432/user_service`
- пользователь: `postgres`
- пароль: `postgres`

Измените эти значения под свою конфигурацию PostgreSQL. Также можно передать переменные окружения — они имеют приоритет над XML:

**Linux/macOS**
```bash
export DB_URL="jdbc:postgresql://localhost:5432/user_service"
export DB_USER="postgres"
export DB_PASSWORD="your_password"
```

**Windows PowerShell**
```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/user_service"
$env:DB_USER="postgres"
$env:DB_PASSWORD="your_password"
```

## 3. Запуск

Из корня проекта:

```bash
mvn clean compile
mvn exec:java
```

При первом запуске Maven скачает зависимости, Hibernate подключится к PostgreSQL и создаст таблицу.

## 4. Возможности

1. Create — создать пользователя;
2. Read — найти пользователя по ID;
3. Read — вывести всех пользователей;
4. Update — изменить имя, email и возраст;
5. Delete — удалить пользователя;
0. Завершить работу.

## Архитектура

```text
Main (консольный интерфейс)
  |
  v
UserDao (CRUD + транзакции)
  |
  v
Hibernate Session / SessionFactory
  |
  v
PostgreSQL

User — JPA-сущность
HibernateUtil — создание и закрытие SessionFactory
```

## Обработка ошибок и транзакции

Каждая DAO-операция выполняется в отдельной Hibernate Session и транзакции. При успешном выполнении вызывается `commit()`. При ошибке выполняется `rollback()`. Нарушение ограничений PostgreSQL (например, повторяющийся email) и ошибки Hibernate преобразуются в `DataAccessException`, после чего консоль сообщает об ошибке и продолжает работу.

Логирование реализовано через SLF4J Simple Logger. Настройки находятся в `simplelogger.properties`.

## Примечание

Для учебного проекта используется `hbm2ddl.auto=update`. В реальных приложениях схему БД обычно изменяют контролируемыми миграциями (например, Flyway или Liquibase), а пароль хранят в переменных окружения или секрет-хранилище.
