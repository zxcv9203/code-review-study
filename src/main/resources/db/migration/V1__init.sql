CREATE TABLE IF NOT EXISTS `users`
(
    id         BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    username   VARCHAR(20)  NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP

);

CREATE INDEX idx_username ON users (username);

CREATE TABLE IF NOT EXISTS `posts`
(
    id         BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    author_id  BIGINT       NOT NULL,
    title      VARCHAR(255) NOT NULL,
    content    TEXT         NOT NULL,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (author_id) REFERENCES users (id)
);

CREATE INDEX idx_post_title ON posts (title);
