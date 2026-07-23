# Exchange Rates API

A small Spring Boot service that serves currency exchange rates — either from a local
database or freshly fetched from the Erste (Česká spořitelna) public API.

## Tech stack

- Java 21, Spring Boot 4.1, Maven
- Spring Web (REST), Spring Data JPA
- H2 (in-memory database)
- springdoc / Swagger UI
- JUnit 5, Mockito, `MockRestServiceServer` (tests)

## Running

```bash
./mvnw spring-boot:run
```

The app starts on `http://localhost:8080`. The database is in-memory, so it starts empty
on every run and needs no setup.

## API

`GET /api/exchange-rates?usedb=true|false`

| `usedb` | behaviour |
|---------|-----------|
| `false` | fetches current rates from the Erste API, stores them in the DB, and returns them |
| `true`  | returns the rates already stored in the DB |

If the Erste API is unavailable, the endpoint returns `503 Service Unavailable`.

## Trying it out

Call `usedb=false` first to populate the database, then `usedb=true` to read it back.

- **Swagger UI:** `http://localhost:8080/swagger-ui/index.html`
- **Browser:**
  - `http://localhost:8080/api/exchange-rates?usedb=false`
  - `http://localhost:8080/api/exchange-rates?usedb=true`
- **H2 console** (inspect stored data): `http://localhost:8080/h2-console`
  JDBC URL `jdbc:h2:mem:exchangerates`, user `sa`, no password

## Tests

```bash
./mvnw test
```

- **Service** (`ExchangeRateServiceTest`) — both `usedb` branches + the API-failure path
- **Web layer** (`ExchangeRateControllerTest`, `@WebMvcTest`) — 200 / 400 / 503

## Design notes

- **H2 in-memory** — keeps the project runnable with a single command, no external DB or Docker.
- **DTO separate from the entity** — decouples the external API shape from the persistence model.
- **External failures → `503`** via a custom exception, instead of leaking a raw 500.
- **`RestClient` injected** (not built inside the service) so the external call is unit-testable.
