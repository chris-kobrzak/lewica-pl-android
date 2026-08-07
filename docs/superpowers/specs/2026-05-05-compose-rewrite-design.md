# LewicaPL Android — Compose Rewrite Design

**Date:** 2026-05-05  
**Branch:** rewrite/compose  
**Author:** Chris Kobrzak

---

## Overview

A full rewrite of the legacy Java/XML Android app in Kotlin + Jetpack Compose, drawing architecture inspiration from the modern SwiftUI iOS counterpart. The new app replaces the Eclipse-era Activity/DAO structure with layered MVVM, Koin DI, Room, and WorkManager.

---

## Constraints & guiding principles

- **Min SDK:** API 26 (Android 8.0) — targets ~90% of active Android devices in Poland
- **Language:** Kotlin throughout
- **UI framework:** Jetpack Compose with Material 3
- **Architecture:** MVVM with StateFlow
- **DI:** Koin (annotation-free, mirrors iOS `ServicesContainer` pattern)
- **Naming:** Generic terms preferred over vendor/library names in class identifiers
- **Indentation:** 2 spaces
- **Spelling:** British English

---

## Project structure

```
app/src/main/java/pl/lewica/lewicapl/android/
├── App.kt                        # Application class, Koin initialisation
├── data/
│   ├── model/                    # Room @Entity classes
│   ├── store/                    # Room DAO interfaces (named *Store)
│   ├── database/                 # AppDatabase class + migrations
│   ├── repository/               # Repository classes (read path)
│   └── sync/                     # SyncService classes (write path)
├── network/
│   ├── FeedClient.kt             # Interface + HttpFeedClient implementation
│   └── ApiEndpoints.kt           # URL definitions
├── parsing/
│   ├── FeedParser.kt             # Generic parser interface
│   ├── dto/                      # Parsing output DTOs
│   └── xml/                      # XmlPullParser implementations
├── ui/
│   ├── app/                      # AppState + root composable + navigation
│   ├── news/                     # NewsScreen + NewsViewModel
│   ├── blogposts/                # BlogPostsScreen + BlogPostsViewModel
│   ├── announcements/            # AnnouncementsScreen + AnnouncementsViewModel
│   ├── history/                  # HistoryScreen + HistoryViewModel
│   ├── more/                     # MoreScreen + SettingsViewModel
│   └── common/                   # Shared composables
└── work/
    └── SyncWorker.kt             # WorkManager worker
```

---

## Key libraries

| Concern | Library |
|---|---|
| UI | Jetpack Compose + Material 3 |
| Navigation | Compose Navigation |
| DI | Koin for Android + Koin Compose |
| Database | Room |
| Async | Kotlin Coroutines + Flow |
| Background sync | WorkManager |
| HTTP | OkHttp |
| XML parsing | Android built-in `XmlPullParser` |
| Preferences | DataStore |
| Image loading | Coil |

---

## Data layer

### Models

Room `@Entity` classes, one per feed type. All use an `Int` primary key (`id`) matching the server ID, used for deduplication on insert. Boolean fields follow the `-able`/`-ible`/`-ing` naming convention (no `is`/`has` prefix).

| Entity | Key fields |
|---|---|
| `Article` | `id`, `title`, `lead`, `body`, `categoryId`, `publicationDate`, `editorCommented` (Boolean flag), `opened` (read status) |
| `BlogPost` | `id`, `title`, `lead`, `body`, `authorName`, `publicationDate`, `opened` |
| `Announcement` | `id`, `title`, `body`, `publicationDate`, `opened` |
| `HistoryEntry` | `id`, `title`, `body`, `eventDate` |

### Stores (Room DAOs)

One interface per entity. All read methods return `Flow<List<T>>` so Room pushes updates to collectors automatically. All inserts use `OnConflictStrategy.IGNORE` for deduplication.

- `ArticleStore` — `getAll()`, `getByCategory(categoryId: Int)`, `insert(articles: List<Article>)`, `markRead(id: Int)`
- `BlogPostStore`, `AnnouncementStore`, `HistoryEntryStore` — same shape

### Database

Single `AppDatabase : RoomDatabase` registered as a Koin singleton, exposing all four Stores.

### News vs Articles

`Article` is a single entity. The News tab filters articles by category ID. One `ArticleStore`, one `ArticleRepository` — `NewsViewModel` and any future `ArticlesViewModel` both use the same repository with different category filters.

---

## Network layer

```kotlin
interface FeedClient {
  suspend fun fetchFeed(url: String): ByteArray
}

class HttpFeedClient(private val client: OkHttpClient) : FeedClient { ... }
```

Registered in Koin as a singleton. The OkHttp reference is confined to `HttpFeedClient`'s constructor; nothing outside the Koin module ever sees it.

`ApiEndpoints` is an object with functions returning URL strings — e.g. `ApiEndpoints.articles(lastId: Int)`, `ApiEndpoints.blogPosts(lastId: Int)`. Supports incremental fetching via `lastId`.

---

## Parsing layer

One parser per feed type, all implementing:

```kotlin
interface FeedParser<T> {
  fun parse(data: ByteArray): List<T>
}
```

Each implementation uses `XmlPullParser` internally and produces DTOs (`ArticleDto`, `BlogPostDto`, `AnnouncementDto`, `HistoryEntryDto`) — plain data classes with no Room annotations. A mapper function converts each DTO to its `@Entity` counterpart before insertion, keeping the parsing layer independent of the database layer.

---

## Sync services

One service per feed type, mirroring the iOS `*SyncService` pattern. Registered as Koin singletons.

```kotlin
class ArticleSyncService(
  private val client: FeedClient,
  private val parser: FeedParser<ArticleDto>,
  private val store: ArticleStore
) {
  val state: StateFlow<SyncState> = ...

  suspend fun sync() {
    // 1. Fetch last known ID from store (incremental fetch)
    // 2. client.fetchFeed(ApiEndpoints.articles(lastId))
    // 3. parser.parse(response) → List<ArticleDto>
    // 4. Map DTOs → Article entities
    // 5. store.insert(articles) — IGNORE deduplicates
    // 6. Update state
  }
}
```

`SyncState` sealed class:

```kotlin
sealed class SyncState {
  data object Idle : SyncState()
  data object Syncing : SyncState()
  data class Failed(val message: String) : SyncState()
}
```

### App-level coordination

`AppState` sealed class drives the root composable:

```kotlin
sealed class AppState {
  data object Loading : AppState()
  data object Ready : AppState()
  data class Failed(val message: String) : AppState()
}
```

`AppViewModel` runs all four sync services in parallel via `async`/`awaitAll` on launch, then transitions to `Ready`. On failure it transitions to `Failed` with a retry action — mirrors the iOS `AppRouter` state machine.

### Background sync

`SyncWorker` (WorkManager) runs at a 1-hour interval. It calls each service's `suspend fun sync()` sequentially, reusing the same service instances registered in Koin. No new logic lives in the worker itself.

---

## Repository layer

Thin read-path wrappers over the Stores, registered as Koin singletons:

```kotlin
class ArticleRepository(private val store: ArticleStore) {
  fun getAll(): Flow<List<Article>> = store.getAll()
  fun getByCategory(categoryId: Int): Flow<List<Article>> = store.getByCategory(categoryId)
  fun markRead(id: Int) = store.markRead(id)
}
```

Repositories are the only dependency ViewModels take for reading data. They keep ViewModels decoupled from Room.

`SettingsRepository` wraps DataStore and exposes a `Flow<Settings>` for text size and theme preferences.

---

## ViewModels

One per tab screen, each exposing a single `StateFlow<UiState>`. Injected via Koin's `viewModel {}` block — no annotations.

```kotlin
class NewsViewModel(
  private val repository: ArticleRepository,
  private val syncService: ArticleSyncService
) : ViewModel() {

  val uiState: StateFlow<NewsUiState> = repository
    .getByCategory(NEWS_CATEGORY_ID)
    .map { articles -> NewsUiState.Ready(articles) }
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), NewsUiState.Loading)

  fun refresh() {
    viewModelScope.launch { syncService.sync() }
  }
}
```

`UiState` sealed classes per screen (`NewsUiState`, `BlogPostsUiState`, etc.) with `Loading`, `Ready(items)`, and `Failed` variants.

`MoreScreen` has no ViewModel — it uses `SettingsViewModel` (root-scoped Koin singleton) directly.

---

## Navigation & UI structure

### Root composable

```kotlin
when (appState) {
  AppState.Loading -> SplashScreen()
  AppState.Ready -> MainScreen()
  is AppState.Failed -> ErrorScreen(onRetry = { appViewModel.retry() })
}
```

### Tab navigation

`MainScreen` uses Compose Navigation with a `BottomNavigationBar`. Five tabs: News, Blog Posts, Announcements, History, More. Each tab has its own `NavHost` back stack, preserving scroll position and detail screen state when switching tabs.

### Destinations per tab

```
NewsTab
  ├── NewsList           ← NewsScreen
  └── NewsDetail/{id}    ← ArticleScreen

BlogPostsTab
  ├── BlogPostsList
  └── BlogPostDetail/{id}

AnnouncementsTab
  ├── AnnouncementsList
  └── AnnouncementDetail/{id}

HistoryTab
  ├── HistoryList
  └── HistoryDetail/{id}

MoreTab
  └── MoreScreen
```

### Shared composables (`ui/common/`)

- `FeedListItem` — title, lead, date, unread indicator
- `FeedDetailScreen` — scrollable body, share action, marks item as read on open
- `ErrorBanner` — inline error with retry button (shown within tab, not full-screen)
- `PullToRefreshContainer` — wraps Material 3 `PullToRefreshBox`

### Settings

`SettingsRepository` (DataStore-backed) exposes `Flow<Settings>`. A root-scoped `SettingsViewModel` provides text size and theme to any composable that needs it via Koin.

---

## Koin module structure

```
AppModule       — AppDatabase, all Stores
NetworkModule   — OkHttpClient, HttpFeedClient
ParsingModule   — all FeedParser implementations
SyncModule      — all SyncService singletons
RepositoryModule — all Repository singletons
ViewModelModule — all viewModel { } declarations
```
