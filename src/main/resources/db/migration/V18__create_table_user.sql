CREATE TABLE _user (
    id BIGSERIAL PRIMARY KEY,
    username varchar NOT NULL UNIQUE,
    password varchar NOT NULL
);

INSERT INTO _user
(username, password)
VALUES
('admin', '$2a$12$FUwELmOkE3AQixWDd6VS2eCgWz03fGZVaZGRKzrGKMmii9./.H8G6');
