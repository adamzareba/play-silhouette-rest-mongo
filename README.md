Silhouette REST Mongodb Seed
=================================

Example project for Play Framework that uses [Silhouette](https://github.com/mohiva/play-silhouette) for authentication and authorization, exposed REST API for sign-up, sign-in.

[![Build Status](https://travis-ci.org/adamzareba/play-silhouette-rest-mongo.svg)](https://travis-ci.org/adamzareba/play-silhouette-rest-mongo)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/a1b8de985fc54598be017babd09fa1d6)](https://www.codacy.com/app/adamzareba/play-silhouette-rest-mongo?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=adamzareba/play-silhouette-rest-mongo&amp;utm_campaign=Badge_Grade)

## Basic usage

### Sign-up

```bash
curl -X POST http://localhost:9000/api/auth/signup  -H 'Content-Type: application/json' -d '{"identifier": "adam.zareba", "password": "this!Password!Is!Very!Very!Strong!", "email": "adam.zareba@test.pl", "firstName": "Adam", "lastName": "Zaręba"}' -v
```

```
< HTTP/1.1 200 OK
< Content-Type: application/json; charset=utf-8
< X-Auth-Token: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...

{
  "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...",
  "expiresOn": "2017-10-06T07:49:27.238+02:00"
}
```

### Sign-in

_Not necessary just after the sign-up because you already have a valid token._

```bash
curl -X POST http://localhost:9000/api/auth/signin/credentials -H 'Content-Type: application/json' -d '{"identifier": "adam.zareba", "password": "this!Password!Is!Very!Very!Strong!"}' -v
```

```
< HTTP/1.1 200 OK
< Content-Type: application/json; charset=utf-8
< X-Auth-Token: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...

{
  "token": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...",
  "expiresOn": "2017-10-06T07:49:27.238+02:00"
}
```

### Secured Action with autorization

_The token must belong to a user with Admin role_

```bash
curl http://localhost:9000/badPassword -H 'X-Auth-Token:eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9...' -v
```

```
< HTTP/1.1 200 OK
< Content-Type: application/json; charset=utf-8

{"result":"qwerty1234"}
```
## Built-in users

| username    | password        |
| ----------- |:---------------:|
| test1       | test1Password   |
| test2       | test2Password   |

## Database reload

It is possible to reload database with based data with scripts:
[recreate.bat](database/recreate.bat) or [recreate.sh](database/recreate.sh)

## API documentation

Documentation is available under address: [REST API](http://localhost:9000/docs)

# License

The code is licensed under [Apache License v2.0](http://www.apache.org/licenses/LICENSE-2.0). 
