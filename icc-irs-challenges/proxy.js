/********************************************************************************
 * Copyright (c) 2024 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Apache License, Version 2.0 which is available at
 * https://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 ********************************************************************************/
const express = require('express');
const { createProxyMiddleware } = require('http-proxy-middleware');

const app = express();
// MOCK URL: https://irs-mock-ic.a3fb75c369e540489a65.germanywestcentral.aksapp.io
// Proxy setup
app.use('/api', createProxyMiddleware({
    target: 'https://irs-ic.a3fb75c369e540489a65.germanywestcentral.aksapp.io',
    changeOrigin: true,
    pathRewrite: { '^/api': '' },
    on: {
        proxyReq: (proxyReq, req, res) => {
            console.log('Proxy Request incoming for http://localhost:3000');
            proxyReq.setHeader('X-API-KEY', 'EZzzwfWcwtMJGvqxivalUyMOQhjjTTvv');
        }
    },
}));

app.listen(3000, () => {
    console.log('Proxy server running at http://localhost:3000');
});
