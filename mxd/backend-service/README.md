# Tractus-X Backend Service

## API Details Summary

The Backend Service is used to simulate a data source or sink enabling the transfer of data and supporting the validation of the end-to-end process. It has the following APIs:
- [Contents API](#contents-api)
- [Transfer API](#transfer-api)

## Contents API

### Generate a random content

- Method: GET
- URL: http://localhost/backend-service/api/v1/contents/random?size={size}
- URL Parameter `size` (optional): Specifies the size of the random content to be generated. The size of the content can be expressed in either kilobytes (KB) or megabytes (MB), ranging from 1 KB to 10 MB.
- Sample response:

```json
{
  "userId": 718895412,
  "title": "tmnx",
  "text": "ivj"
}
```

### Save the generated content

- Method: POST
- URL: http://localhost/backend-service/api/v1/contents
- Request body:

```json
{
  "userId": 718895412,
  "title": "tmnx",
  "text": "ivj"
}
```

- Sample response containing content id and URL:

```json
{
  "id": "3b777103-5e06-461b-90c6-1f99e597f60d",
  "url": "http://localhost:8080/api/v1/contents/3b777103-5e06-461b-90c6-1f99e597f60d"
}
```
This URL will be used as an endpoint in the transfer API.

### Create and save a random content

- Method: GET
- URL: http://localhost/backend-service/api/v1/contents/create/random?size={size}
- URL Parameter `size` (optional): Specifies the size of the random content to be generated. The size of the content can be expressed in either kilobytes (KB) or megabytes (MB), ranging from 1 KB to 10 MB.
- Sample response:

```json
{
  "id": "3b777103-5e06-461b-90c6-1f99e597f60d",
  "url": "http://localhost:8080/api/v1/contents/3b777103-5e06-461b-90c6-1f99e597f60d"
}
```
This URL will be used as an endpoint in the transfer API.

### Fetch a content

- Method: GET
- URL: http://localhost/backend-service/api/v1/contents/{content-id}
- Sample response:

```json
{
  "userId": 718895412,
  "title": "tmnx",
  "text": "ivj"
}
```

### Fetch all contents

- Method: GET
- URL: http://localhost/backend-service/api/v1/contents
- Sample response:

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

### Accept transfer data

- Method: POST
- URL: http://localhost/backend-service/api/v1/transfers
- Request/response:

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

- Method: GET
- URL: http://localhost/backend-service/api/v1/transfers/{id}
- Sample response:

```json
{
  "id": "123456789011",
  "endpoint": "http://localhost:8080/api/v1/contents/3b777103-5e06-461b-90c6-1f99e597f60d",
  "authKey": "Authorization",
  "authCode": "100000",
  "properties": {}
}
```

### Get transfer content
Get the content which is stored at the above endpoint:
- Method: GET
- URL: http://localhost/backend-service/api/v1/transfers/{id}/contents
- Sample response:

```json
{
  "userId": 718895412,
  "title": "tmnx",
  "text": "ivj"
}
```

Alternatively, please check out the [Postman collections here](./assets/postman)