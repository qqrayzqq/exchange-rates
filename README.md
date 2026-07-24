# Exchange Rates

A small full-stack app for currency exchange rates. The backend serves rates — either from
a local database or freshly fetched from the Erste (Česká spořitelna) public API — and the
frontend displays them.

```
exchange-rates/
├── backend/    Spring Boot REST API
└── frontend/   vanilla HTML + JavaScript UI
```

## Backend

**Stack:** Java 21, Spring Boot 4.1, Maven, Spring Web + Data JPA, H2 (in-memory),
springdoc/Swagger, JUnit 5 + Mockito.

**Run:**
```bash
cd backend
./mvnw spring-boot:run
```
Starts on `http://localhost:8080`. The database is in-memory, so it starts empty on every
run and needs no setup.

**API:** `GET /api/exchange-rates?usedb=true|false`

| `usedb` | behaviour |
|---------|-----------|
| `false` | fetches current rates from the Erste API, stores them in the DB, and returns them |
| `true`  | returns the rates already stored in the DB |

If the Erste API is unavailable, the endpoint returns `503 Service Unavailable`.

**Explore:**
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- H2 console: `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:exchangerates`, user `sa`, no password)

**Tests:**
```bash
cd backend
./mvnw test
```
Service tests (both `usedb` branches + API failure) and web-layer tests (`@WebMvcTest`, 200/400/503).

## Frontend

Two screens in plain HTML + JavaScript (no build step): a list of all rates, and a detail
view of one rate. It reads the data from the local backend above.

**Run** (the backend must be running first):
```bash
cd frontend
python3 -m http.server 5500
```
Then open `http://localhost:5500`. The backend allows requests from any localhost port (CORS),
so the two run on different ports without issues.

## Design notes

- **`usedb` = the DB as a cache** — calling the Erste API on every request would be slow and
  rate-limited, so the database acts as a cache: `usedb=false` refreshes it from Erste, `usedb=true`
  serves the stored copy. In production I would refresh it on a schedule (e.g. a daily job) and
  always serve reads from the DB, rather than exposing `usedb` to the client.
- **H2 in-memory** — keeps the project runnable with a single command, no external DB or Docker.
- **DTO separate from the entity** — decouples the external API shape from the persistence model.
- **External failures → `503`** via a custom exception, instead of leaking a raw 500.
- **`RestClient` injected** (not built inside the service) so the external call is unit-testable.
