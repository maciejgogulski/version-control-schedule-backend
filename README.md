# About Version Control Schedule

Version Control Schedule is an application developed as part of a BSc thesis. The app addresses the problem of notifying students about changes in class schedules by maintaining version histories. Its versioning functionality is inspired by professional version control systems like **Git** or **SVN**, where changes must be committed before students are notified.

Students are notified about changes via email. Before a version is committed, any changes are visible only to the person managing the schedule. The application can store multiple schedules, and each schedule has its own list of addressees.

This repository contains the backend source code, developed with Java Spring Boot. The frontend is maintained in a separate repository [here](https://github.com/maciejgogulski/version-control-schedule-frontend).

---

# Local Environment Setup Instructions

## Required Software:

1. PostgreSQL server
2. Docker

## Backend Configuration and Startup

1. Create a schema in the database with any name.
2. Configure the database connection in the file:
   `/version-control-schedule-backend/src/main/resources/application-docker-local.properties`

Example configuration:

```
spring.datasource.url=jdbc:postgresql://host.docker.internal:5432/schema-name
spring.datasource.username=postgres
spring.datasource.password=password
```

`host.docker.internal` is the DNS address used by the container to access the local machine.

3. In the same file, configure the mail server and the account from which notifications will be sent.

Example configuration:

```
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=some-mail@gmail.com
spring.mail.password=password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

4. Make sure that the option `app.cors.allowed-origins` points to the frontend address.
   By default, the frontend runs on port `3000`:

```
app.cors.allowed-origins=http://localhost:3000
```

## Frontend Configuration and Startup

1. In the folder `/version-control-schedule-frontend`, copy the file `env.example` and rename it to `.env`. Make sure the backend address is correctly set.
   By default, it should be: `http://localhost:8080`

## Docker Environment Startup

In the folder `/version-control-schedule-backend` there is a `docker-compose.yaml` file. Make sure:

1. The following option points to the main frontend folder:

```
frontend:
    build:
      context: ../version-control-schedule-frontend
```

2. The `ports` for both backend and frontend match the previously configured ports.
   By default, they should be: `8080:8080` for backend and `3000:3000` for frontend.

3. From the terminal, navigate to `/version-control-schedule-backend` and run:

```
docker-compose up --build -d
```

## Logging into the Application

If everything is set up correctly, the application should be accessible at the previously configured frontend address.
Using the default setup, you can log in at:
`http://localhost:3000/login`

For demonstration purposes, a user has been added to the database:

* **Username:** `admin`
* **Password:** `admin`
