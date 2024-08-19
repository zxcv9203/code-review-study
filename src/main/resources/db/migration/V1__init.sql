CREATE TABLE IF NOT EXISTS `users`
(
    id         BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
    username   VARCHAR(20)  NOT NULL,
    password   VARCHAR(255) NOT NULL,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP

);

ALTER TABLE `users`
    ADD INDEX `idx_username` (`username`);

CREATE TABLE IF NOT EXISTS `posts`
(
    id         BIGINT       NOT NULL PRIMARY KEY,
    user_id    BIGINT       NOT NULL,
    title      VARCHAR(255) NOT NULL,
    content    TEXT         NOT NULL,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users (id)
);