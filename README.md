# TransferWatch

TransferWatch is an Android application for searching football teams and viewing their incoming and outgoing player transfers.

The project consists of:

- A native Android application written in Java
- A Spring Boot backend
- An integration with API-Football
- Automated tests across the API, repository, service, controller, and presentation layers

## Features

- Search for any football team by name
- Display matching teams
- View transfers for a selected team
- Switch between teams
- Pull to refresh transfer data
- Loading, empty, and error states
- Retry after network failures
- Defensive handling of incomplete API responses

## User flow

1. The user enters at least three characters of a team name.
2. The Android app requests matching teams from the backend.
3. The user selects a team.
4. The backend retrieves transfers for the selected team from API-Football.
5. The Android app displays incoming and outgoing transfers.
6. The user can refresh the list or return to search for another team.

## Technology

### Android

- Java 17
- Android SDK 37
- Android ViewModel and LiveData
- Retrofit
- RecyclerView
- SwipeRefreshLayout
- JUnit
- MockWebServer

### Backend

- Java 21
- Spring Boot
- Spring MVC
- Spring RestClient
- Gradle
- JUnit 5
- Mockito
- MockMvc
- MockRestServiceServer
- JaCoCo

## Architecture

### Android

The Android application is organized by layer and feature:

```text
com.example.transferwatch
├── data
│   ├── remote
│   │   ├── ApiClient
│   │   └── TransferApi
│   └── repository
│       └── NetworkFootballRepository
├── domain
│   ├── model
│   │   ├── Team
│   │   └── Transfer
│   └── repository
│       ├── FootballRepository
│       └── RepositoryCallback
└── ui
    ├── main
    │   └── MainActivity
    ├── search
    │   ├── TeamAdapter
    │   ├── TeamSearchState
    │   ├── TeamSearchViewModel
    │   └── TeamSearchViewModelFactory
    └── transfers
        ├── TransferAdapter
        ├── TransferScreenState
        ├── TransferViewModel
        └── TransferViewModelFactory
```

The UI depends on the repository interface rather than Retrofit directly:

```text
UI → domain repository interface ← network repository
```

This keeps network implementation details out of activities and ViewModels.

### Backend

The backend separates application features from the external football provider:

```text
com.example.transferwatchbackend
├── team
│   ├── Team
│   ├── TeamController
│   ├── TeamProvider
│   └── TeamSearchService
├── transfer
│   ├── Transfer
│   ├── TransferController
│   ├── TransferProvider
│   └── TransferService
└── infrastructure
    └── football
        ├── ApiFootballClient
        └── api
            └── API-Football response models
```

Services depend on provider interfaces rather than directly depending on API-Football:

```text
Controller → service → provider interface ← API-Football client
```

A different football data provider can therefore be introduced without rewriting the transfer or team-search services.

## API endpoints

### Search teams

```http
GET /api/teams?query=arsenal
```

Example response:

```json
[
  {
    "id": 42,
    "name": "Arsenal",
    "logo": "https://example.com/arsenal.png"
  }
]
```

Search queries require at least three characters.

### Get transfers for a team

```http
GET /api/teams/42/transfers
```

Example response:

```json
[
  {
    "playerName": "Test Player",
    "fromClub": "Previous Club",
    "toClub": "Arsenal",
    "transferType": "Permanent",
    "date": "2026-08-20"
  }
]
```

## Setup

### Prerequisites

- JDK 21
- Android Studio
- An Android emulator or device
- An API-Football API key

### Configure API-Football

The backend reads the API key from the `API_FOOTBALL_KEY` environment variable.

### Start the backend

From the backend directory:

```bash
cd transferwatch-backend
API_FOOTBALL_KEY="your-api-key" ./gradlew bootRun
```

The backend runs at:

```text
http://localhost:8080
```

Test it from the development computer:

```bash
curl "http://localhost:8080/api/teams?query=arsenal"
```

### Run the Android application

Open the `TransferWatch` directory in Android Studio and run the application on an emulator.

The Android development build connects to:

```text
http://10.0.2.2:8080
```

In the Android emulator, `10.0.2.2` refers to the host development computer. `localhost` inside the emulator refers to the emulator itself.

The backend must remain running while using the Android app.

## Automated tests

### Backend tests

Run:

```bash
cd transferwatch-backend
./gradlew clean test jacocoTestReport
```

The HTML coverage report is generated at:

```text
transferwatch-backend/build/reports/jacoco/test/html/index.html
```

The backend test covers:

- Team-search query normalization and validation
- Transfer mapping, filtering, and sorting
- Incoming and outgoing transfers
- Null and incomplete upstream responses
- Controller-to-service delegation
- HTTP routing and JSON serialization
- Missing and invalid request parameters
- API-Football request paths and authentication headers
- API-Football response mapping

At the time of the final QA pass, the backend report showed approximately:

- 98% instruction coverage
- 90% branch coverage

Coverage is used as a diagnostic tool rather than as the sole measure of test quality.

### Android tests

Run:

```bash
cd TransferWatch
./gradlew clean testDebugUnitTest assembleDebug
```

The Android test covers:

- Retrofit request paths
- Team-search query parameters
- Transfer and team JSON parsing
- Empty and malformed responses
- HTTP and rate-limit errors
- Repository success and failure callbacks
- Search loading, result, empty, and error states
- Transfer loading, content, empty, and error states
- Selected-team transfer requests
- Refreshing the currently selected team

## Manual QA

The following scenarios were verified on an Android emulator:

- Application opens on team search
- Queries shorter than three characters are rejected
- Arsenal can be searched and selected
- The selected team’s transfers are displayed
- Pull to refresh reloads the selected team
- Returning to search preserves the search flow
- A different team can be selected
- Empty search results display a message
- Backend unavailability displays an error
- Retry succeeds after the backend restarts
- Device rotation does not crash the app
- Local-network permission behavior works where applicable


## Extensibility

Future functionality can be introduced through separate feature packages and interfaces.

For example, player-value prediction could define:

```text
prediction
├── PlayerValuation
├── PlayerValuationService
├── StatisticsProvider
└── PredictionController
```

A statistics provider could retrieve player data from another API without modifying the existing team-search or transfer services.

## Known limitations

- The Android base URL is configured for a local emulator development environment.
- A local backend is required while using the Android app.
- The app does not currently paginate large result sets.
- Rapid consecutive searches may allow an older response to arrive after a newer response.
