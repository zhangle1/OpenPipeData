CREATE TABLE meta_dbservice_entity (
                               id VARCHAR(255) NOT NULL,
                               name VARCHAR(255) NOT NULL,
                               servicetype VARCHAR(255),
                               json JSON,
                               updatedat BIGINT,
                               updatedby VARCHAR(255),
                               deleted BOOLEAN,
                               namehash VARCHAR(255),
                               PRIMARY KEY (id)
);