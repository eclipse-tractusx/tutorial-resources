/********************************************************************************
 *  Copyright (c) 2024 SAP SE
 *
 *  This program and the accompanying materials are made available under the
 *  terms of the Apache License, Version 2.0 which is available at
 *  https://www.apache.org/licenses/LICENSE-2.0
 *
 *  SPDX-License-Identifier: Apache-2.0
 *
 *  Contributors:
 *       SAP SE - initial API and implementation
 *
 ********************************************************************************/

package org.eclipse.tractusx.mxd.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.edc.spi.EdcException;

import java.security.SecureRandom;

public class RandomWordUtil {

    public static String generateRandom(int size) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            RandomData randomData = new RandomData();
            randomData.setUserId(generateRandomUserId());
            randomData.setTitle(generateRandomString(8));
            randomData.setText(generateRandomString(size));
            return objectMapper.writeValueAsString(randomData);
        } catch (Exception e) {
            throw new EdcException(e.getMessage());
        }
    }

    private static int generateRandomUserId() {
        return Math.abs(new SecureRandom().nextInt());
    }

    private static String generateRandomString(int length) {
        return StringUtils.repeat("a", length);
    }

    private static class RandomData {
        private int userId;
        private String title;
        private String text;

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }
}
