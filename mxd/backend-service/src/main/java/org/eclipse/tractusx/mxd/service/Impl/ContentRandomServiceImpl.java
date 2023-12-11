
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

package org.eclipse.tractusx.mxd.service.Impl;

import org.eclipse.tractusx.mxd.util.ContentsRandom;
import org.eclipse.tractusx.mxd.service.ContentRandomService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;

import java.util.logging.Logger;

@ApplicationScoped
public class ContentRandomServiceImpl implements ContentRandomService {

    private static final Logger logger = Logger.getLogger(ContentRandomServiceImpl.class.getName());

    private final ContentsRandom contentsRandom = new ContentsRandom();

    @Override
    public Response getRandomContent() {
        try {
            return contentsRandom.getRandomContent();
        } catch (Exception e) {
            logger.info(e.getMessage());
            return Response.ok(e.getMessage()).build();
        }
    }
}
