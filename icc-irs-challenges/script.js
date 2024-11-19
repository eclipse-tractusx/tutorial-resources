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
const registerJobButton = document.getElementById('register-job');
const getJobDetailsButton = document.getElementById('get-job-details');
const outputElement = document.getElementById('output');
const structuredOutput = document.getElementById('structured-output');
const canvas = document.getElementById('relationship-graph');
const ctx = canvas.getContext('2d');
let jobId = null;

// Register Job
registerJobButton.addEventListener('click', async () => {
    const response = await fetch('http://localhost:3000/api/irs/jobs', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
            aspects: [
                "urn:samm:io.catenax.serial_part:3.0.0#SerialPart",
                "urn:samm:io.catenax.just_in_sequence_part:3.0.0#JustInSequencePart",
                "urn:samm:io.catenax.batch:3.0.0#Batch",
                "urn:samm:io.catenax.single_level_bom_as_built:3.0.0#SingleLevelBomAsBuilt"
            ],
            key: {
                globalAssetId: "<globalAssetId>",
                bpn: "<BPN>"
            },
            collectAspects: true,
            direction: "downward"
        })
    });

    const data = await response.json();
    jobId = data.id;
    outputElement.textContent = `Job Registered: ${JSON.stringify(data, null, 2)}`;
    getJobDetailsButton.disabled = false;
});

// Get Job Details
getJobDetailsButton.addEventListener('click', async () => {
    if (!jobId) return;
    const response = await fetch(`http://localhost:3000/api/irs/jobs/${jobId}`);
    const data = await response.json();
    outputElement.textContent = `Job Details: ${JSON.stringify(data, null, 2)}`;
    // Display structured data
    displayStructuredData(data);

    // Draw the graph
    drawGraph(data);
});

const displayStructuredData = (data) => {
    const { job, submodels } = data;

    const html = `
    <p><strong>Global Asset ID:</strong> ${job.globalAssetId}</p>
    <p><strong>State:</strong> ${job.state}</p>
    <p><strong>BPN:</strong> ${job.parameter.bpn}</p>
    <p><strong>Direction:</strong> ${job.parameter.direction}</p>
    <h3>Submodels:</h3>
    <ul>
      ${submodels
        .map(
            (submodel) => `
        <li>
          <strong>CatenaX ID:</strong> ${submodel.payload.catenaXId} <br>
          <strong>Local Identifiers:</strong> ${JSON.stringify(
                submodel.payload.localIdentifiers,
                null,
                2
            )}
        </li>`
        )
        .join('')}
    </ul>
  `;
    structuredOutput.innerHTML = html;
};

// Function to draw a simple graph
const drawGraph = (data) => {
    const { submodels, relationships } = data;

    // Clear the canvas
    ctx.clearRect(0, 0, canvas.width, canvas.height);

    const nodePositions = {}; // Track positions for tooltips

    // Draw nodes
    submodels.forEach((submodel, index) => {
        const x = 100 + index * 150;
        const y = canvas.height / 2;

        // Draw nodes
        ctx.beginPath();
        ctx.arc(x, y, 30, 0, Math.PI * 2);
        ctx.fillStyle = '#3498db';
        ctx.fill();
        ctx.stroke();

        // Add labels
        ctx.fillStyle = '#000';
        ctx.font = '12px Arial';
        ctx.fillText(submodel.payload.catenaXId, x - 30, y - 40);

        // Store node position for tooltips
        nodePositions[submodel.payload.catenaXId] = { x, y, details: submodel };
    });

    // Draw relationships
    relationships.forEach((relationship) => {
        const parentNode = nodePositions[relationship.catenaXId];
        const childNode =
            nodePositions[relationship.linkedItem.childCatenaXId];

        if (parentNode && childNode) {
            const { x: x1, y: y1 } = parentNode;
            const { x: x2, y: y2 } = childNode;

            ctx.beginPath();
            ctx.moveTo(x1, y1);
            ctx.lineTo(x2, y2);
            ctx.strokeStyle = '#e74c3c';
            ctx.stroke();

            // Add relationship label
            const midX = (x1 + x2) / 2;
            const midY = (y1 + y2) / 2;
            ctx.fillStyle = '#000';
            ctx.fillText(relationship.aspectType, midX, midY - 10);
        }
    });

    // Add tooltips on hover
    canvas.addEventListener('mousemove', (event) => {
        const rect = canvas.getBoundingClientRect();
        const mouseX = event.clientX - rect.left;
        const mouseY = event.clientY - rect.top;

        // Clear previous tooltip
        ctx.clearRect(0, 0, canvas.width, canvas.height);
        drawGraph(data); // Redraw the graph

        // Check if hovering over a node
        Object.values(nodePositions).forEach(({ x, y, details }) => {
            const distance = Math.sqrt(
                Math.pow(mouseX - x, 2) + Math.pow(mouseY - y, 2)
            );
            if (distance < 30) {
                // Display tooltip
                ctx.fillStyle = '#ffffff';
                ctx.fillRect(x + 10, y - 30, 200, 80);
                ctx.strokeStyle = '#000';
                ctx.strokeRect(x + 10, y - 30, 200, 80);

                ctx.fillStyle = '#000';
                ctx.font = '10px Arial';
                ctx.fillText(
                    `CatenaX ID: ${details.payload.catenaXId}`,
                    x + 15,
                    y - 15
                );
                ctx.fillText(
                    `Part ID: ${details.payload.localIdentifiers[0]?.value || 'N/A'}`,
                    x + 15,
                    y
                );
                ctx.fillText(
                    `Manufacturer: ${
                        details.payload.partTypeInformation?.manufacturerPartId || 'N/A'
                    }`,
                    x + 15,
                    y + 15
                );
            }
        });
    });
};

