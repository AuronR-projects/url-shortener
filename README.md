# URL Shortener

![CI](https://github.com/AuronR-projects/url-shortener/actions/workflows/ci.yml/badge.svg)

A REST API that turns long URLs into short links, redirects visitors to the original address, and tracks how many times each link is used.

Built with Java 21, Spring Boot, Spring Data JPA and JUnit 5.

**Live demo:** https://url-shortener-qzn4.onrender.com

> The demo runs on a free tier, so the first request after a period of inactivity can take up to a minute while the service wakes up. Data is stored in memory and resets when the service restarts.

## Features

- Create a short link for any `http` or `https` URL
- Redirect from the short link to the original URL (HTTP 302)
- Click analytics per link (original URL, creation time, click count)
- Input validation with clear 400 responses, and a 404 for unknown codes
- Unit tests for the encoder, service and controller, run automatically by GitHub Actions on every push
- Multi-stage Docker build, deployed on Render

## Try it

PowerShell:

```powershell
Invoke-RestMethod -Method Post -Uri https://url-shortener-qzn4.onrender.com/api/urls -ContentType "application/json" -Body '{"url":"https://example.com"}'
```

curl:

```bash
curl -X POST https://url-shortener-qzn4.onrender.com/api/urls \
  -H "Content-Type: application/json" \
  -d '{"url":"https://example.com"}'
```

Then open the `shortUrl` from the response in a browser to be redirected.

## Run locally

Requirements: Java 21 and Git.

```bash
git clone https://github.com/AuronR-projects/url-shortener.git
cd url-shortener
./mvnw spring-boot:run
```

On Windows PowerShell, use `.\mvnw.cmd spring-boot:run`. The app starts on `http://localhost:8080` with an in-memory H2 database.

Run the tests with `./mvnw test`.

## API

### Create a short link

`POST /api/urls`

```bash
curl -X POST http://localhost:8080/api/urls \
  -H "Content-Type: application/json" \
  -d '{"url":"https://github.com/AuronR-projects"}'
```

Response (201 Created):

```json
{
  "shortCode": "1",
  "shortUrl": "http://localhost:8080/1",
  "originalUrl": "https://github.com/AuronR-projects"
}
```

### Follow a short link

`GET /{shortCode}` responds with a 302 redirect to the original URL and records the click.

### Get link statistics

`GET /api/urls/{shortCode}/stats`

```json
{
  "shortCode": "1",
  "originalUrl": "https://github.com/AuronR-projects",
  "createdAt": "2026-10-30T16:03:18.267700Z",
  "clickCount": 6
}
```

### Errors

| Case | Status |
|---|---|
| Blank, malformed or non-http(s) URL | 400 Bad Request |
| Unknown short code | 404 Not Found |

## How it works

- **Short codes:** each link is saved first so the database assigns it a numeric ID, then the ID is encoded in Base62 (`0-9`, `a-z`, `A-Z`). IDs are unique, so codes never collide and no random-retry loop is needed. Six characters cover about 56 billion links.
- **Layers:** controller (HTTP), service (business logic, transactions) and repository (database), so each part can be tested on its own.
- **302 instead of 301:** a 301 is cached by browsers, which would stop repeat visits reaching the server and make click counts wrong.
- **DTOs:** request and response records keep the database entity out of the public API.
- **Validation:** Jakarta Validation rejects blank, over-long and non-http(s) URLs, which also blocks schemes like `javascript:`.
- **Testing:** JUnit 5 for the encoder, Mockito for the service, and MockMvc for the controller's HTTP behaviour.
- **Docker:** a multi-stage build compiles with the JDK, then copies only the jar into a smaller JRE image.
- **CI:** GitHub Actions builds the project and runs all tests on every push.

## Project structure

```
src/main/java/com/auron/urlshortener/
  UrlshortenerApplication.java  application entry point
  UrlController.java            HTTP endpoints
  UrlService.java               business logic
  UrlMappingRepository.java     database access (Spring Data JPA)
  UrlMapping.java               entity
  Base62.java                   ID to short-code encoder
  CreateUrlRequest.java         request DTO with validation
  ShortUrlResponse.java         response DTO
  UrlStatsResponse.java         stats DTO
  UrlNotFoundException.java     exception for unknown codes
  GlobalExceptionHandler.java   maps exceptions to HTTP errors
src/test/java/...               unit tests
```

## Limitations and next steps

- The database is in-memory, so data is lost on restart. The PostgreSQL driver is already included for a persistent setup.
- Possible improvements: rate limiting, custom aliases, link expiry and OpenAPI docs.

## Author

Auron Rashica, Second-year Computer Science student.