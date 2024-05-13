# Tractus-X Backend Service

## API Details Summary

Backend Service is used to validate the transfer. It has the following APIs.

- [Contents API](#contents-api)
- [Transfer API](#transfer-api)

## Contents API

### Generate a random content

- Method : GET
- URL : http://localhost/backend-service/api/v1/contents/random
- Sample Response

```json
{
  "userId": 718895412,
  "title": "tmnx",
  "text": "ivj"
}
```

### Save the generated content

- Method : POST
- URL : http://localhost/backend-service/api/v1/contents
- Request Body:

```json
{
  "userId": 718895412,
  "title": "tmnx",
  "text": "ivj"
}
```

- Sample Response containing content id and url

```json
{
  "id": "3b777103-5e06-461b-90c6-1f99e597f60d",
  "url": "http://localhost:8080/api/v1/contents/3b777103-5e06-461b-90c6-1f99e597f60d"
}
```
This URL will be used as a Endpoint in the transfer API.

### Fetch a Content

- Method : GET
- URL : http://localhost/backend-service/api/v1/contents/{content-id}
- Sample Response

```json
{
  "userId": 718895412,
  "title": "tmnx",
  "text": "ivj"
}
```

### Fetch All Contents

- Method : GET
- URL : http://localhost/backend-service/api/v1/contents
- Response

```json
[
  {
    "userId": 1,
    "id": 1,
    "title": "delectus aut autem",
    "completed": false
  },
  {
    "userId": 1,
    "id": 2,
    "title": "quis ut nam facilis et officia qui",
    "completed": false
  }
]
```


## Transfer API

### Accept the transfer data from the connector

Connector will push something similar to this:
```json
{
  "id": "123456789011",
  "endpoint": "<Content Url>",
  "authKey": "Authorization",
  "authCode": "<Auth Code>",
  "properties": {}
}
```

- Method : POST
- URL : http://localhost/backend-service/api/v1/transfers
- Request/ Response

```json
{
  "id": "123456789011",
  "endpoint": "http://localhost:8080/api/v1/contents/3b777103-5e06-461b-90c6-1f99e597f60d",
  "authKey": "Authorization",
  "authCode": "100000",
  "properties": {}
}
```

### Get transfer data with ID

- Method : GET
- URL : http://localhost/backend-service/api/v1/transfers/{id}
- Sample Response

```json
{
  "id": "123456789011",
  "endpoint": "http://localhost:8080/api/v1/contents/3b777103-5e06-461b-90c6-1f99e597f60d",
  "authKey": "Authorization",
  "authCode": "100000",
  "properties": {}
}
```

### Get Transfer Content
Get the content which is stored at the above endpoint
- Method : GET
- URL : http://localhost/backend-service/api/v1/transfers/{id}/contents
- Sample Response

```json
{
  "userId": 718895412,
  "title": "tmnx",
  "text": "ivj"
}
```

Alternatively, please check out the [Postman collections here](./assets/postman)