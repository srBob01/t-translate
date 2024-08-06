# My Translator Application

Это приложение позволяет выполнять переводы текста с одного языка на другой с использованием сервиса Yandex Translate API. Приложение построено на Spring Boot и использует базу данных PostgreSQL для хранения данных о переводах.

## Установка и настройка

### Шаг 1: Установка PostgreSQL и psql

1. Установите PostgreSQL на вашей машине, следуя инструкциям на официальном сайте: [PostgreSQL Downloads](https://www.postgresql.org/download/).

2. После установки откройте терминал и подключитесь к серверу PostgreSQL:

    ```bash
    psql -U postgres
    ```

### Шаг 2: Создание пользователя и базы данных

1. Создайте нового пользователя:

    ```sql
    CREATE USER t_user WITH PASSWORD '1234';
    ```

2. Создайте базу данных и назначьте созданного пользователя её владельцем:

    ```sql
    CREATE DATABASE db_translator OWNER t_user;
    ```

Теперь пользователь `t_user` имеет все необходимые права для работы с базой данных `db_translator`.

### Шаг 3: Получение IAM-токена Yandex

1. Получите IAM-токен, следуя инструкциям на сайте: [Yandex IAM Tokens](https://yandex.cloud/ru/docs/iam/operations/#iam-tokens).

2. После получения токена обновите конфигурацию приложения `application.yml`, указав ваш токен и идентификатор папки (folder ID):

    ```yaml
    yandex.api.key: <ваш IAM-токен>
    yandex.api.folder-id: <ваш folderId>
    ```

### Шаг 4: Запуск Spring Boot приложения

#### Запуск через IntelliJ IDEA

1. Откройте проект в IntelliJ IDEA.
2. Найдите класс `TranslatorApplication`.
3. Нажмите правой кнопкой мыши на этот класс и выберите `Run` или используйте горячие клавиши `Shift + F10`.

#### Запуск через командную строку

1. Перейдите в директорию вашего проекта:

    ```bash
    cd /path/to/clone/project
    ```
    Далее перейдите в папку translator:

   ```bash
    cd translator
    ```

3. Соберите проект и запустите его.

   Для Gradle:

    ```bash
    ./gradlew bootRun
    ```

### Шаг 5: Проверка работы приложения

#### Через Swagger UI

1. Откройте браузер и перейдите по адресу: [http://localhost:8080/swagger-ui/index.html#/](http://localhost:8080/swagger-ui/index.html#/).

2. В разделе `translation-controller` найдите метод POST и выполните запрос, изменив параметры на нужные вам:

    ```json
    {
      "inputText": "Hello",
      "inputLang": "en",
      "translatedLang": "ru"
    }
    ```

#### Через командную строку с помощью `curl`

Выполните следующую команду в терминале:

```bash
curl -X 'POST' \
  'http://localhost:8080/api/translate' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "inputText": "Hello",
  "inputLang": "en",
  "translatedLang": "ru"
}'
