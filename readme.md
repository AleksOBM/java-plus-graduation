# Explore with me (микросервисная версия)

![Static Badge](https://img.shields.io/badge/Java-21-green)
![Static Badge](https://img.shields.io/badge/Spring_Boot-3.5.9-green)
![Static Badge](https://img.shields.io/badge/Microservices-8A2BE2)
![Static Badge](https://img.shields.io/badge/SpringCloud-538681)
![Static Badge](https://img.shields.io/badge/Kafka-blue)
![Static Badge](https://img.shields.io/badge/JPA--specification-5d7cc2)
![Static Badge](https://img.shields.io/badge/PostgreSQL-16.1-blue)
![Static Badge](https://img.shields.io/badge/docker_compose-blue)
![Static Badge](https://img.shields.io/badge/Maven-orange)


## Бэкэнд для сервиса поиска мероприятий

### Configuration
configuration files location: infra/config-repo

### Microservice map

<img alt="microservices.svg" src=".img/microservices.svg" width="600"/>

### Calculate similarities map

<img alt="similarity.png" src=".img/similarity.png" width="500"/>

### Matrix example

Матрица ведущих весов (leadingWeightMatrix)  

|     | u1   | u3   | u5   | u7   | u8   | u10  | u11  | u12  | u13  |
|-----|------|------|------|------|------|------|------|------|------|
| e1  |      |      | 0.80 |      |      |      |      |      |      |
| e2  |      |      |      |      |      |      |      |      | 0.80 |
| e4  | 0.40 | 0.80 |      |      |      | 0.40 |      |      |      |
| e5  | 1.00 |      |      |      |      |      |      |      |      |
| e7  |      |      |      |      | 0.80 |      |      |      |      |
| e8  |      |      |      |      |      |      |      | 0.40 |      |
| e9  |      |      |      |      |      |      | 0.80 |      |      |
| e11 |      |      |      |      |      |      |      |      | 0.40 |
| e13 |      |      |      |      |      |      |      |      | 0.40 |
| e14 |      |      |      |      | 0.40 |      |      |      |      |
| e15 |      |      |      | 0.80 |      |      |      |      |      |
| e16 |      |      |      |      |      |      |      | 0.80 |      |
| e17 |      |      |      |      |      | 0.80 |      |      |      |
| e18 |      |      |      |      |      | 0.80 |      | 0.40 |      |
| e20 | 0.80 |      |      |      |      |      |      |      |      |

Матрица числителей (sumOfMinWeightsMatrix)  

|     | e2   | e4   | e5   | e7   | e8   | e11  | e13  | e14  | e16  | e17  | e18  | e20  |
|-----|------|------|------|------|------|------|------|------|------|------|------|------|
| e2  | 0.00 |      |      |      |      | 0.40 | 0.40 |      |      |      |      |      |
| e4  |      | 0.00 | 0.40 |      |      |      |      |      |      | 0.40 | 0.40 | 0.40 |
| e5  |      | 0.40 | 0.00 |      |      |      |      |      |      |      |      | 0.80 |
| e7  |      |      |      | 0.00 |      |      |      | 0.40 |      |      |      |      |
| e8  |      |      |      |      | 0.00 |      |      |      | 0.40 |      | 0.40 |      |
| e11 | 0.40 |      |      |      |      | 0.00 | 0.40 |      |      |      |      |      |
| e13 | 0.40 |      |      |      |      | 0.40 | 0.00 |      |      |      |      |      |
| e14 |      |      |      | 0.40 |      |      |      | 0.00 |      |      |      |      |
| e16 |      |      |      |      | 0.40 |      |      |      | 0.00 |      | 0.40 |      |
| e17 |      | 0.40 |      |      |      |      |      |      |      | 0.00 | 0.80 |      |
| e18 |      | 0.40 |      |      | 0.40 |      |      |      | 0.40 | 0.80 | 0.00 |      |
| e20 |      | 0.40 | 0.80 |      |      |      |      |      |      |      |      | 0.00 |

Таблица знаменателей (eventToLeadingWeightsSum)  

|       | e1   | e2   | e4   | e5   | e7   | e8   | e9   | e11  | e13  | e14  | e15  | e16  | e17  | e18  | e20  |
|-------|------|------|------|------|------|------|------|------|------|------|------|------|------|------|------|
|   Σ   | 0.80 | 0.80 | 1.60 | 1.00 | 0.80 | 0.40 | 0.80 | 0.40 | 0.40 | 0.40 | 0.80 | 0.80 | 0.80 | 1.20 | 0.80 |

### External API
```mermaid
mindmap
  root((API))
    admin
      🌐/categories
        DELETE /admin/categories/:catId
        PATCH /admin/categories/:catId
        POST /admin/categories
      🌐/compilations
        DELETE /admin/compilations/:compId
        PATCH /admin/compilations/:compId
        POST /admin/compilations
      🌐/events
        GET /admin/events
        PATCH /admin/events/:eventId
      🌐/users
        DELETE /admin/users/:userId
        GET /admin/users/:userId
        GET /admin/users
        POST /admin/users
    user
      🌐/events
        GET /users/:userId/events
        GET /users/:userId/events/:eventId
        GET /users/:userId/events/:eventId/requests
        PATCH /users/:userId/events/:eventId
        PATCH /users/:userId/events/:eventId/requests
        POST /users/:userId/events
      🌐/requests
        GET /users/:userId/requests
        PATCH /users/:userId/requests/:requestId/cancel
        POST /users/:userId/requests
      🌐/rating
        PUT /events/:eventId/like
      🌐/recommendations
        GET /events/recommendations
    public
      🌐/categories
        GET /categories
        GET /categories/:catId
      🌐/compilations
        GET /compilations
        GET /compilations/:compId
      🌐/events
        GET /events
        GET /events/:eventId
```

### Internal API
```mermaid
mindmap
  root((API))
    system
      🌐/events
        GET /system/events/:eventId
        GET /system/events/:eventId/initiator
      🌐/requests
        GET /system/requests/count
        GET /system/requests/users/:userId/events/:eventId
        PATCH /system/requests/events/:eventId/status
      🌐/users
        GET /system/users/:userId/check
        GET /system/users/:userId
        GET /system/users/short
```

### Database map
```mermaid
erDiagram
    EVENTS {
        bigint id PK
        varchar annotation
        timestamp created_on
        varchar description
        timestamp event_date
        jsonb location
        boolean paid
        integer participant_limit
        timestamp published_on
        boolean request_moderation
        varchar state
        varchar title
        bigint category_id FK
        bigint initiator_id FK
    }

    CATEGORIES {
        bigint id PK
        varchar name
    }

    COMPILATIONS {
        bigint id PK
        boolean pinned
        varchar title
    }

    COMPILATION_EVENTS {
        bigint compilation_id FK
        bigint events_id FK
    }

    REQUESTS {
        bigint id PK
        timestamp created
        varchar status
        bigint event_id FK
        bigint requester_id FK
    }

    USERS {
        bigint id PK
        varchar email
        varchar name
    }

    INTERACTIONS {
        bigint id PK
        bigint user_id FK
        bigint event_id FK
        numeric action_weight
    }

    SIMILARITIES {
        bigint id PK
        bigint event_a FK
        bigint event_b FK
        numeric score
    }

    CATEGORIES ||--o{ EVENTS : classifies
    USERS ||--o{ EVENTS : initiates
    EVENTS ||--o{ COMPILATION_EVENTS : included_in
    COMPILATIONS ||--o{ COMPILATION_EVENTS : contains
    EVENTS ||--o{ REQUESTS : receives
    USERS ||--o{ REQUESTS : creates
    USERS ||--o{ INTERACTIONS : performs
    EVENTS ||--o{ INTERACTIONS : tracked_in
    EVENTS ||--o{ SIMILARITIES : compared_as_a
    EVENTS ||--o{ SIMILARITIES : compared_as_b
```