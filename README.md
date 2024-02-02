# Instrunkcja uruchomienia systemu na środowisku lokalnym

## Wymagane oprogramowanie:
1. Serwer PostgreSQL
2. Docker

## Konfiguracja uruchomienia - backend
1. Utworzyć w bazie danych schemat o dowolnej nazwie.
2. W pliku `/version-control-schedule-backend/src/main/resources/application-docker-local.properties` skonfigurować połączenie z bazą danych.

Przykładowa konfiguracja:
```
spring.datasource.url=jdbc:postgresql://host.docker.internal:5432/nazwa-schematu
spring.datasource.username=postgres
spring.datasource.password=password
```
`host.docker.internal` to adres DNS z którego kontener dostaje się do lokalnej maszyny.

3. W tym samym pliku należy skonfigurować połączenie z serwerem pocztowym i konto z którego będą wysyłane powiadomienia.

Przykładowa konfiguracja: 
```
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=some-mail@gmail.com
spring.mail.password=password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

4. Należy się upewnić jeszcze, czy opcja `app.cors.allowed-origins` wskazuje na adres frontendu.
Domyślnie frontend jest uruchamiany na porcie `3000`
```
app.cors.allowed-origins=http://localhost:3000
```

## Konfiguracja uruchomienia - frontend
1. W folderze `/version-control-schedule-frontend` skopiować plik `env.example` i nazwać go `.env`. Trzeba upewnić się, czy wskazany jest tam adres backendu.
Domyślnie powinien to być `http://localhost:8080`

## Uruchomienie środowiska dockerowego
W folderze `/version-control-schedule-backend` znajduje się plik `docker-compose.yaml` Należy upewnić się:
1. Czy poniższa opcja wskazuje na główny katalog frontendu.
```
frontend:
    build:
      context: ../version-control-schedule-frontend
```
2. Pozycje `ports` zarówno dla backendu jak i frontendu powinny wskazywać na porty poprzednio skonfigurowane. 
Domyślnie to odpowiednio `8080:8080` i `3000:3000`.

3. Z poziomu terminala przejść do katalogu `/version-control-schedule-backend` i uruchomić komendę:
```
docker-compose up --build -d 
```

## Zalogowanie do aplikacji
Jeżeli wszystko poszło w porządku aplikacja powinna być dostępna pod wcześniej wybranym adresem frontendu. 
W przypadku domyślnej można się zalogować pod adresem:
`http://localhost:3000/login`
Na potrzeby demonstracji umieszczono w bazie użytkownika z loginem `admin` i hasłem `admin`.
