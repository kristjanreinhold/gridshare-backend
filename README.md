# GridShare EV — backend

Spring Boot 3.5 · Java 21 · PostgreSQL · Flyway. REST API for the EV charger
sharing marketplace (spec §7). **No auth yet** — `CurrentHost` resolves a fixed
seeded host (Mari Tamm); security is a later milestone.

## Prerequisites

- **JDK 21** (the repo's default shell `java` may be newer; this project targets 21).
- **Docker** for the local Postgres.

## 1. Start Postgres (port 1111)

```bash
cd ../local
docker compose up -d
```

DB: `gridshare` / user `gridshare` / pass `gridshare` on `localhost:1111`.

## 2. Run the API (port 8080)

```bash
cd backend
JAVA_HOME=/path/to/jdk-21 ./gradlew bootRun
```

Flyway applies the schema (`V1__init.sql`) and seed data (`V2__seed.sql`) on
startup. The API serves at `http://localhost:8080`.

> The Vite dev proxy in `gridshare-web` forwards `/api/*` → `http://localhost:8080`.
> To use the real backend instead of MSW mocks, set `VITE_USE_MSW_MOCKS=false`
> in `gridshare-web/.env.local`.

## Endpoints (spec §7)

| Method | Path | Notes |
|--------|------|-------|
| POST | `/auth/google` | mock login → `{token, host}` |
| GET/PATCH | `/me/profile` | account details (name, email, phone, IBAN) |
| GET | `/me/listings` `/me/bookings` `/me/payouts` | host-scoped |
| GET | `/listings?near=lat,lng` | public discovery (available + active) |
| GET | `/listings/{id}` | single listing |
| POST | `/listings` · PATCH `/listings/{id}` · PATCH `/listings/{id}/availability` | host |
| POST | `/bookings` | create → `PENDING_HOST` |
| GET | `/bookings/{id}?token=` | status (token-gated once security lands) |
| POST | `/bookings/{id}/accept` `/decline` | host handshake |
| POST | `/bookings/{id}/pay` | → Montonio link |
| POST | `/webhooks/montonio?ref=` | payment confirm → `CONFIRMED` + payout |
| GET | `/disputes` · PATCH `/disputes/{id}` | admin |

## Notes

- Booking state machine enforced (spec §6); invalid transitions return `409`.
- Errors use `{code, message}` matching the frontend `ApiError`.
- IBAN/phone/email are PII — encrypt IBAN at rest before production (spec §8).
