-- Statements are designed for and tested with Postgres only!
--*******************************************************************************
--*
-- * Copyright (c) 2023 Contributors to the Eclipse Foundation
-- *
-- * See the NOTICE file(s) distributed with this work for additional
-- * information regarding copyright ownership.
-- *
-- * This program and the accompanying materials are made available under the
-- * terms of the Apache License, Version 2.0 which is available at
-- * https://www.apache.org/licenses/LICENSE-2.0
-- *
-- * Unless required by applicable law or agreed to in writing, software
-- * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
-- * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
-- * License for the specific language governing permissions and limitations
-- * under the License.
-- *
-- * SPDX-License-Identifier: Apache-2.0
--*
-- ******************************************************************************/

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


