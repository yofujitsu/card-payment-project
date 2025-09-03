# Проект стажировки IT_ONE - Система оплаты картой

## Цель проекта
1. Разработать серверную часть системы оплаты картой на микросервисной архитектуре, реализуя паттерн API FIRST.  
2. Протестировать и обернуть инфраструктуру системы в контейнеры общей сети **Docker**.

## Используемый стек

### Java + Spring
- ![Java](https://raw.githubusercontent.com/devicons/devicon/master/icons/java/java-original.svg) **Java 17** — основной язык разработки.  
- ![Spring Boot](https://raw.githubusercontent.com/devicons/devicon/master/icons/spring/spring-original.svg) **Spring Boot** — каркас для создания микросервисов, реализует DI, REST API, интеграцию с брокером сообщений и базой данных.  

### Брокер сообщений
- ![RabbitMQ](https://www.rabbitmq.com/img/logo-rabbitmq.svg) **RabbitMQ** — брокер сообщений, через который микросервисы обмениваются событиями и результатами обработки.  

### БД
- ![PostgreSQL](https://raw.githubusercontent.com/devicons/devicon/master/icons/postgresql/postgresql-original.svg) **PostgreSQL** — реляционная СУБД для хранения транзакций и логов.  

### ✉SMTP-сервер
- ![MailHog](https://raw.githubusercontent.com/mailhog/MailHog/master/assets/MailHog.png) **MailHog** — тестовый SMTP-сервер для отладки и проверки отправки email-уведомлений.  

### Контейнеризация
- ![Docker](https://raw.githubusercontent.com/devicons/devicon/master/icons/docker/docker-original.svg) **Docker** — контейнеризация всех сервисов.  
- **Docker Compose** — управление многоконтейнерной системой, объединение сервисов в единую сеть.  

### Тестирование
- ![JUnit5](https://junit.org/junit5/assets/img/junit5-logo.png) **JUnit 5** — модульное тестирование микросервисов.  
- ![Mockito](https://avatars.githubusercontent.com/u/1515293?s=200&v=4) **Mockito** — создание моков для имитации зависимостей и проверки взаимодействий в тестах.  

### ⚙Генерация кода
- ![OpenAPI](https://avatars.githubusercontent.com/u/16343502?s=200&v=4) **OpenAPI Generator (Gradle plugin)** — автоматическая генерация DTO и клиентского кода из спецификаций `.yaml`, предоставленных для каждого микросервиса.  

## Стек проекта
- **Spring Boot** — ;
- **RabbitMQ** — брокер сообщений для обмена данными между микросервисами;
- **Postgres** — SQL БД для хранения данных транзакций и логов;
- **MailHog** — локальный SMTP-сервер для имитации отправки email-писем;
- **Junit, Mockito** —

## Архитектура системы
Основные микросервисы:
1. **payment-gateway** — принимает HTTP POST запросы на оплату.
2. **card-validation** — проверяет корректность введённых данных карты.
3. **bank-payment** — выполняет авторизацию платежа в банке.
4. **payment-processing** — формирует финальный статус транзакции.
5. **transaction-recording** — сохраняет данные транзакций в БД.
6. **notification-service** — отправляет email-уведомления о статусе транзакции (имитация через MailHog).
7. **logging-service** — собирает логи всех операций и сохраняет их в БД.

## Сценарий работы системы:
1. Клиент отправляет **HTTP POST** запрос на `payment-gateway` c банковскими данными по URI `/payment/authoriize`.
2. `payment-gateway` через **RabbitMQ** вызывает `card-validation` для проверки данных карты.
3. `card-validation` возвращает результат (валидные данные / ошибка).
4. `payment-gateway` отвечает клиенту:  
   - ошибка в данных → вернуть сообщение «Введите данные корректно»;  
   - успех → «Платёж принят в обработку».
5. Далее `payment-gateway` отправляет данные в `bank-payment` для авторизации.
6. `bank-payment` проверяет сумму платежа и возвращает результат в `payment-processing`.
7. `payment-processing` формирует финальный статус транзакции и отправляет:  
   - в `transaction-recording` → событие о необходимости записи данных в БД;  
   - в `notification-service` → уведомление о необходимости отправки email-письма.
8. `notification-service` через MailHog имитирует отправку письма на email, указанный клиентом.
9. Все сервисы параллельно отправляют **логи событий** в `logging-service`, который сохраняет их в БД.

## Схема компонентов системы
![Схема компонентов](https://github.com/user-attachments/assets/4598fca5-40a0-498b-b19c-aebd73d35890)

## Контейнеризация
Вся система запускается внутри **Docker Compose**:
- каждый микросервис — отдельный контейнер;
- общая сеть `docker-compose` связывает сервисы;
- RabbitMQ, Postgres и MailHog поднимаются как инфраструктурные контейнеры, для каждого контейнера настроен healthcheck;
- система запускается одной командой:

```bash
docker-compose up -d
