# Challenge: Industry Core Standard: Developing a Frontend for the IRS API using GenAI on Tractus-X Days

## Introduction
This guide explains how to use the provided AI assistant and how to start the  solution developed in the challenge.
After completing all three phases, you should have a progressive frontend solution that: 
* phase 1: integrates with REST APIs provided by the Item-Relationship-Service
* phase 2: visualizes part chains with relationships between digital twins in a network 
* phase 3: visualizes aspect data linked to digital twins.

## Table of Contents

- [Introduction](#introduction)
- [Prerequisites](#prerequisites)
- [How to use the KI assistance](#how-to-use-the-ki-assistance)
- [IRS-API-Request-Collection-Insomnia](#irs-api-request-collection)
- [Example solution](#example-solution)
- [Example ChatGPT History of Successful Workshop](#example-chatgpt-history-of-successful-workshop)
- [Contact](#contact)

## Prerequisites

1. **Node.js and npm** installed on your system. (Download: [Node.js](https://nodejs.org/))
2. **Proxy Server** configured as shown in the workshop (to avoid CORS issues).
3. The `X-API-KEY` securely provided by the workshop moderator.
4. The 'baseUrl' securely provided by the workshop moderator. 
5. Select your favorite AI chatbot:
   6. ChatGPT access (only premium): [ChatGPT](https://chatgpt.com/))
   7. Claude.ai [Claude.ai](https://claude.ai/new) access (free or premium)


## How to use the KI assistance
1. Copy/Paste [KI assistance](ki-guide.md) into your AI chatbot.
2. Start the KI assistant and follow the instructions. 

## IRS Api Request Collection
[Insomnia Request Collection](irs-api-request-collection.json)

## Execute challenge 
1. Open your favorite IDE.
2. Follow instructions of [KI assistance](ki-guide.md) to set up progressive frontend.
3. Navigate to the directory where proxy server configuration `proxy.js` is located.
4. Ensure the `X-API-KEY` is replaced with the one shared securely by the moderator.
5. Ensure the API `baseUrl` is replaced with the one shared securely by the moderator.
6. Start the proxy server in terminal:
   ```bash
   node proxy.js
7. Run in another terminal:
   ```bash
   npm install
8. Open the 'index.html' in your favourite browser.
9. Register a IRS Job
10. Get the job details
11. Check results

## Contact

For questions or further information, please feel free to reach out:

- **Martin Kanal**  
  [Martin.Kanal@doubleslash.de](mailto:Martin.Kanal@doubleslash.de)

- **Jaro Hartmann**  
  [Jaro.Hartmann@doubleslash.de](mailto:Jaro.Hartmann@doubleslash.de)

- **Maximilian Wesener**  
  [Maximilian.Wesener@doubleslash.de](mailto:Maximilian.Wesener@doubleslash.de)
