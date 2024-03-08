/*******************************************************************************
 *
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
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

package org.eclipse.tractusx.mxd.backendservice.service;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.eclipse.edc.spi.http.EdcHttpClient;
import org.eclipse.edc.spi.monitor.Monitor;
import org.eclipse.tractusx.mxd.backendservice.entity.Transfer;

import java.io.IOException;

public class HttpConnectionService {


    private EdcHttpClient httpClient;

    private Monitor monitor;

    public HttpConnectionService(EdcHttpClient httpClient, Monitor monitor) {
        this.httpClient = httpClient;
        this.monitor = monitor;
    }

    public HttpConnectionService() {
    }

    public String getUrlAssets(Transfer receivedModel,Monitor monitor) {
        var res = "";
        monitor.info("getUrlAssets "+receivedModel);
        Request getRequest = new Request.Builder()
                .url(receivedModel.getEndpoint())
                .header(receivedModel.getAuthKey(), receivedModel.getAuthCode())
                .get()
                .build();
        try (var response = httpClient.execute(getRequest)) {
            if (response.isSuccessful()) {
                if (response.body() != null) {
                    monitor.info("response  " + response.body());
                    return String.valueOf(response.body());
                } else {
                    monitor.warning("Response body is null");
                }
            } else {
                monitor.warning("HTTP request failed with status code: " + response.code());
            }
        } catch (IOException e) {
            monitor.warning("IOException occurred: " + e.getMessage());
        }
        return res;
    }
}
