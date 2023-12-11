
/*******************************************************************************
 *
 * Copyright (c) 2023 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 *
 ******************************************************************************/

package org.eclipse.tractusx.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import org.eclipse.tractusx.model.ContentRandomResponse;

import java.util.Random;
import java.util.logging.Logger;

@ApplicationScoped
public class ContentsRandom {
    private static final Logger logger = Logger.getLogger(ContentsRandom.class.getName());

    public Response getRandomContent() {
        try {
            Random random = new Random();
            int random_number = random.nextInt(100) + 1;
            String title = RandomWordUtil.generateRandomWord();
            String text = RandomWordUtil.generateRandomWord();
            ContentRandomResponse contentRandomResponse = new ContentRandomResponse();
            contentRandomResponse.setId(random_number);
            contentRandomResponse.setText(text);
            contentRandomResponse.setTitle(title);
            ObjectMapper objectMapper = SingletonObjectMapper.getObjectMapper();
            return Response.ok(objectMapper.writeValueAsString(contentRandomResponse)).build();

        } catch (Exception e) {
            logger.info("error " + e.getMessage());
        }

        return Response.ok("Random Json Not Generated !").build();
    }

}
