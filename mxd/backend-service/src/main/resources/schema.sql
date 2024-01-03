-- Statements are designed for and tested with Postgres only!

CREATE TABLE IF NOT EXISTS content
(
    id          text,
    asset       text,
    createddate timestamp(6) DEFAULT CURRENT_TIMESTAMP,
    updateddate timestamp(6) DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT content_pkey PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS transfer
(
    transferid  text,
    asset       text,
    contents    text,
    createddate timestamp(6) DEFAULT CURRENT_TIMESTAMP,
    updateddate timestamp(6) DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT transfer_pkey PRIMARY KEY (transferid)
);


