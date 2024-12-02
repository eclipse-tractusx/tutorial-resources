document.getElementById('registerJobBtn').addEventListener('click', registerJob);
document.getElementById('getJobResponseBtn').addEventListener('click', getJobResponse);

let jobId = '';  // To store the job ID for subsequent request

// Function to register a job
function registerJob() {
    const bpn = document.getElementById('bpn').value;
    const globalAssetId = document.getElementById('globalAssetId').value;

    // Prepare the data for the Register Job request
    const data = {
        aspects: [
            "urn:samm:io.catenax.serial_part:3.0.0#SerialPart",
            "urn:samm:io.catenax.just_in_sequence_part:3.0.0#JustInSequencePart",
            "urn:samm:io.catenax.batch:3.0.0#Batch",
            "urn:samm:io.catenax.single_level_bom_as_built:3.0.0#SingleLevelBomAsBuilt"
        ],
        key: {
            globalAssetId: globalAssetId,
            bpn: bpn
        },
        collectAspects: true,
        direction: "downward"
    };

    // Send the POST request to register the job
    fetch('http://localhost:3000/api/irs/jobs', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(data),
    })
        .then(response => response.json())
        .then(data => {
            jobId = data.id;  // Save the job ID for the next request
            document.getElementById('getJobResponseBtn').style.display = 'inline';  // Show the Get Job Response button
            displayResponse(data);  // Display the response from the Register Job request
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

// Function to get the job response details
function getJobResponse() {
    // Send the GET request to retrieve job details
    fetch(`http://localhost:3000/api/irs/jobs/${jobId}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json',
        },
    })
        .then(response => response.json())
        .then(data => {
            displayResponse(data);  // Display the response from the Get Job Details request
        })
        .catch(error => {
            console.error('Error:', error);
        });
}

// Function to display JSON response in the HTML
function displayResponse(data) {
    document.getElementById('responseDisplay').textContent = JSON.stringify(data, null, 2);
}
