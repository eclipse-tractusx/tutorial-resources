--
--  Copyright (c) 2024 SAP SE
--
--  This program and the accompanying materials are made available under the
--  terms of the Apache License, Version 2.0 which is available at
--  https://www.apache.org/licenses/LICENSE-2.0
--
--  SPDX-License-Identifier: Apache-2.0
--
--  Contributors:
--       SAP SE - initial API and implementation
--

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


