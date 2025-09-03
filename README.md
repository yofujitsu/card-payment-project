# Проект стажировки IT_ONE - Система оплаты картой

## Цель проекта
1. Разработать серверную часть системы оплаты картой на микросервисной архитектуре, реализуя паттерн API FIRST.  
2. Протестировать и обернуть инфраструктуру системы в контейнеры общей сети **Docker**.

## Используемый стек

![Java](https://img.shields.io/badge/Java-17-orange?logo=java&logoColor=white)  
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?logo=spring&logoColor=white)  
![RabbitMQ](https://img.shields.io/badge/RabbitMQ-Message%20Broker-FF6600?logo=rabbitmq&logoColor=white)  
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-316192?logo=postgresql&logoColor=white)  
![MailHog](https://img.shields.io/badge/MailHog-SMTP%20Testing-red?logo=mail.ru&logoColor=white)  
![Docker](https://img.shields.io/badge/Docker-Containerization-2496ED?logo=docker&logoColor=white)  
![Docker Compose](https://img.shields.io/badge/Docker%20Compose-Orchestration-2496ED?logo=docker&logoColor=white)  
![JUnit5](https://img.shields.io/badge/JUnit5-Testing-25A162?logo=junit5&logoColor=white)  
![Mockito](https://img.shields.io/badge/Mockito-Mocking-yellow?logo=java&logoColor=white)  
![OpenAPI](https://img.shields.io/badge/OpenAPI-Codegen-6BA539?logo=openapiinitiative&logoColor=white)  
![Gradle](https://img.shields.io/badge/Gradle-Build%20Tool-02303A?logo=gradle&logoColor=white) 

- **Java 17** — основной язык разработки;
- **Spring Boot** — фреймворк для разработки микросервисов, реализует DI, REST API, интеграцию с внешними системами;
- **RabbitMQ** — брокер сообщений, через который микросервисы обмениваются событиями и результатами обработки;
- **PostgreSQL** — реляционная СУБД для хранения транзакций и логов;
- **MailHog** — локальный SMTP-сервер отправки email-уведомлений;  
- **Docker** — контейнеризация всех сервисов системы;  
- **Docker Compose** — объединение сервисов в единую сеть;  
- **JUnit 5** — модульное тестирование микросервисов;
- **Mockito** — создание моков для имитации зависимостей и проверки взаимодействий в тестах;  
- **OpenAPI Generator (Gradle plugin)** — автоматическая генерация DTO и клиентского кода из спецификаций `.yaml`, предоставленных в заданиях к проекту.  

## Архитектура системы

##№ Схема компонентов системы
![Схема компонентов](https://github.com/user-attachments/assets/4598fca5-40a0-498b-b19c-aebd73d35890)

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

## Контейнеризация
Вся система запускается внутри **Docker Compose**:
- каждый микросервис — отдельный контейнер;
- общая сеть `docker-compose` связывает сервисы;
- RabbitMQ, Postgres и MailHog поднимаются как инфраструктурные контейнеры, для каждого контейнера настроен healthcheck;
- система запускается одной командой:

```bash
docker-compose up -d
