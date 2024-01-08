# Tractus-X Backend Service

##  API Details Summary

Backend Service is used to validate the transfer. it has the following end points.

## 1 Contents API

### POST API /api/v1/contents to store an assets. It should return a url / id of this content in the response.


 Body:

  {
        "userId": 1,
        "id": 1,
        "title": "delectus aut autem",
        "completed": false
  }
```

which should return something similar to this

```json
{
  "id":"3b777103-5e06-461b-90c6-1f99e597f60d",
  "url":"http://localhost:9000/api/v1/contents/3b777103-5e06-461b-90c6-1f99e597f60d"
}
```

### GET API /api/v1/contents/{id} to fetch the content. This URL will be used as a DataAddress in the assets AP



which should return something similar to this

```json
{
    "userId": 1,
    "id": 1,
    "title": "delectus aut autem",
    "completed": false
}
```

### GET API /api/v1/contents to fetch the All the content. 



which should return something similar to this

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
    },
    {
        "userId": 1,
        "id": 3,
        "title": "fugiat veniam minus",
        "completed": false
    },
    {
        "userId": 1,
        "id": 4,
        "title": "et porro tempora",
        "completed": true
    },
    {
        "userId": 1,
        "id": 5,
        "title": "laboriosam mollitia et enim quasi adipisci quia provident illum",
        "completed": false
    },
    {
        "userId": 1,
        "id": 6,
        "title": "qui ullam ratione quibusdam voluptatem quia omnis",
        "completed": false
    }
]
```
### GET API /api/v1/contents/random to generate some random contents.

which should return something similar to this

```json
{
  "userId": 894688136,
  "title": "fijp",
  "text": "wvfaauux"
}
```


## 2 Transfer API


### POST API /api/v1/transfers to accept the transfer data from the connector. Connector will push data similar to this:

```shell
 Body:

  {
  "id": "123456789011",
  "endpoint": "http://alice-tractusx-connector-dataplane:8081/api/public",
  "authKey": "Authorization",
  "authCode": "<Auth Code>",
  "properties": {}
}
```

Persisting above data with transfer id as primary key.

```json
{
  "id": "123456789011",
  "endpoint": "http://alice-tractusx-connector-dataplane:8081/api/public",
  "authKey": "Authorization",
  "authCode": "100000",
  "properties": {}
}
```

### GET API /api/v1/transfers/{id} to return the above json pushed by connector.


which should return something similar to this

```json
{
  "id": "123456789011",
  "endpoint": "http://alice-tractusx-connector-dataplane:8081/api/public",
  "authKey": "Authorization",
  "authCode": "100000",
  "properties": {}
}
```

### GET API /api/v1/transfers/{id}/contents to return the actual assets content (i.e. the data which is stored at the above endpoint http://alice-tractusx-connector-dataplane:8081/api/public)

which should return something similar to this

```json
  {
        "userId": 123456789011,
        "id": 6,
        "title": "qui ullam ratione quibusdam voluptatem quia omnis",
        "completed": false
  }
```
### GET API /api/v1/transfers/{id}/contents to return the actual assets content (i.e. the data which is stored at the above endpoint http://alice-tractusx-connector-dataplane:8081/api/public)

which should return something similar to this

```json
  {
        "userId": 123456789011,
        "id": 6,
        "title": "qui ullam ratione quibusdam voluptatem quia omnis",
        "completed": false
  }
```
## Database Schema

### following Schema is used for contents
```shell
CREATE TABLE IF NOT EXISTS content
(
    id          text,
    asset       text,
    createddate timestamp(6) DEFAULT CURRENT_TIMESTAMP,
    updateddate timestamp(6) DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT content_pkey PRIMARY KEY (id)
);
```
### following Schema is used for transfer
```shell
CREATE TABLE IF NOT EXISTS transfer
(
    transferid  text,
    asset       text,
    contents    text,
    createddate timestamp(6) DEFAULT CURRENT_TIMESTAMP,
    updateddate timestamp(6) DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT transfer_pkey PRIMARY KEY (transferid)
);
```