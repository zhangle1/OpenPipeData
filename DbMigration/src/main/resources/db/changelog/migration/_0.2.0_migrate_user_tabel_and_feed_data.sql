CREATE TABLE bas_user
(
    `id`          varchar(255) NOT NULL,
    `create_by`   varchar(255) DEFAULT NULL,
    `create_time` datetime     DEFAULT NULL,
    `update_by`   varchar(255) DEFAULT NULL,
    `update_time` datetime     DEFAULT NULL,
    `permission`  int          DEFAULT NULL,
    `email`       varchar(255) DEFAULT NULL,
    `username`    varchar(255) DEFAULT NULL,
    `password`    varchar(255) DEFAULT NULL,
    `active`      tinyint(1)   DEFAULT NULL,
    `name`        varchar(255) DEFAULT NULL,
    `description` varchar(255) DEFAULT NULL,
    `avatar`      varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;



INSERT INTO bas_user(`id`, `email`, `username`, `password`, `active`, `name`, `description`, `avatar`, `create_time`,
                     `create_by`, `update_time`, `update_by`)
VALUES ('38c0758ee6464c01b98774e214dc245e', '867997639@qq.com', 'demo',
        '$2a$10$qkTNayLvXqdMJW7mQOJOZub/.fmokmMwNll8stmN6MJUffUKRRdRS', 1, 'qq123123', NULL,
        'resources/user/avatar/38c0758ee6464c01b98774e214dc245e/蒙版组 12.png', '2023-05-26 09:31:14',
        '38c0758ee6464c01b98774e214dc245e', '2023-08-02 11:07:34', '38c0758ee6464c01b98774e214dc245e');
