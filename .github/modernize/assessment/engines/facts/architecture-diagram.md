# Architecture Diagram

Vista resumida de la arquitectura implementada de MYPES Vinto. El cliente móvil prioriza la persistencia local; la API centraliza autenticación, catálogo y recepción de sincronización.

## Application Architecture

<!-- mermaid-checked: no \n, no em-dash/en-dash, no {} in labels, subgraphs are id["label"], arrows are -->|"label"|, all subgraphs closed by end, ids unique -->
~~~mermaid
flowchart TD
    subgraph ClientLayer["Client Layer"]
        Merchant["Merchant"]
        AndroidApp["Android Kotlin Compose"]
    end
    subgraph LocalLayer["Local Application Layer"]
        YapayLocal["Yapay local rules"]
        RoomStore[("Room SQLite SQLCipher")]
        AndroidApp -->|"local commands"| YapayLocal
        AndroidApp -->|"offline records"| RoomStore
        YapayLocal -->|"confirmed local writes"| RoomStore
    end
    subgraph ServerLayer["Server Application Layer"]
        ExpressApi["Node.js 24 Express API"]
        PostgresDb[("PostgreSQL")]
        ExpressApi -->|"SQL transactions"| PostgresDb
    end
    Merchant -->|"app interaction"| AndroidApp
    AndroidApp -->|"REST Bearer JWT"| ExpressApi
    ExpressApi -.->|"business scoped SSE"| AndroidApp
    RoomStore -->|"outbox sync"| ExpressApi
~~~

### Technology Stack Summary

| Layer | Technology | Version | Purpose |
|---|---|---|---|
| Mobile UI | Kotlin, Jetpack Compose | Android compile SDK 35 | Native Android screens and local workflows |
| Local data | Room, SQLite, SQLCipher | Room 2.6.1 | Offline data, sales, and Outbox |
| Backend | Node.js, TypeScript, Express | Node.js 24, Express 5.1 | REST API, authentication, synchronization |
| Server data | PostgreSQL | Docker image 16-alpine for local development | Relational business data and transaction inbox |
| Authentication | jose, JWT HS256, scrypt | JWT lifetime 900 seconds | Bearer token validation and password hashing |

### Data Storage & External Services

Room/SQLite with SQLCipher stores local products, sales, cash movements, and pending operations. PostgreSQL stores users, businesses, products, synchronization transactions, conflicts, simulated invoices, and payment QR data. The current Yapay implementation uses local intent rules; no remote LLM, tax authority integration, external payment processor, cache, or distributed message broker is configured. SSE notifications are held in process memory.

### Key Architectural Decisions

- Uses a local-first Android app and a durable Outbox so sales can be recorded before connectivity returns.
- Uses PostgreSQL for relational constraints, decimal monetary values, and idempotent operation keys scoped by business.
- Treats invoices as simulated PDFs and SSE as single-process development functionality, not production-scale integrations.

## Component Relationships

<!-- mermaid-checked: no \n, no em-dash/en-dash, no {} in labels, subgraphs are id["label"], arrows are -->|"label"|, all subgraphs closed by end, ids unique -->
~~~mermaid
flowchart LR
    subgraph cPresentation["Presentation"]
        cLogin["Android login and registration"]
        cInventory["Android inventory and sales"]
        cChat["Yapay chat"]
    end
    subgraph cBusiness["Business Logic"]
        cViewModels["ViewModels and repositories"]
        cIntent["Local intent classifier"]
        cSyncWorker["WorkManager sync worker"]
    end
    subgraph cPersistence["Data Access"]
        cRoom[("Room database and Outbox")]
        cApi["Retrofit API clients"]
        cPg[("PostgreSQL")]
    end
    subgraph cInfrastructure["Infrastructure"]
        cAuth["Express auth middleware"]
        cRoutes["Express route modules"]
        cSse["In-memory SSE event bus"]
    end
    cLogin -->|"registration and login requests"| cApi
    cInventory -->|"user actions"| cViewModels
    cChat -->|"local text intent"| cIntent
    cIntent -->|"confirmed tool action"| cRoom
    cViewModels -->|"read and write local data"| cRoom
    cSyncWorker -->|"sends pending operations"| cApi
    cApi -->|"REST requests"| cAuth
    cAuth -->|"valid business claims"| cRoutes
    cRoutes -->|"SQL queries"| cPg
    cRoutes -->|"publishes business updates"| cSse
~~~

### Component Inventory

| Component | Layer | Type | Responsibility |
|---|---|---|---|
| Android login and registration | Presentation | Compose screens | Local login and remote account registration flows |
| Android inventory and sales | Presentation | Compose screens | Product list, sale entry, and local fiado workflows |
| Yapay chat | Presentation | Compose screen | Displays chat state and routes local commands |
| ViewModels and repositories | Business Logic | Android state/data adapters | Coordinates UI with Room and application rules |
| Local intent classifier | Business Logic | Kotlin NLU | Classifies supported commands without a remote model |
| WorkManager sync worker | Business Logic | Background worker | Delivers pending Outbox transactions and fetches catalog changes |
| Room database and Outbox | Data Access | Encrypted local database | Persists offline records and queued operations |
| Retrofit API clients | Data Access | HTTP clients | Calls auth and synchronization endpoints |
| Express auth middleware | Infrastructure | Middleware | Validates Bearer JWT and attaches user/business claims |
| Express route modules | Infrastructure | REST routes | Implements auth, product, transaction, and cloud handlers |
| PostgreSQL | Data Access | Relational database | Persists cloud data with constraints and idempotency keys |
| In-memory SSE event bus | Infrastructure | Event publisher | Sends same-business notifications within one backend process |
