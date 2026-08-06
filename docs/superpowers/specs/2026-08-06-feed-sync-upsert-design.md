# FeedSyncService upsert design

## Problem

Every `*Store` DAO writes synced rows with `@Insert(onConflict = OnConflictStrategy.IGNORE)`. Once a row's id exists locally, any server-side edit to that row is silently dropped — the local copy never updates. Two syncs make this concrete today even without any timestamp support from the API:

- `EditorSyncService` has no cursor at all and re-fetches the full editor list on every sync, yet an edited bio or blog title never overwrites the stale local row.
- `HistoryEntrySyncService` re-fetches the same day's entries every time the app is opened that day, yet a corrected historical event never overwrites the stale local row.
- `ArticleSyncService.refreshCommentCount(articleId)` already fetches the full `ArticleDto` for the article a reader has open, but throws away everything except `commentCount` — title/body/thumbnail/editor comment/author edits are fetched and discarded.

Confirmed via `curl` against `https://lewica.pl/api/v2/articles` and `/articles/{id}`: the live API returns no `updatedAt`/`lastModified` field on any resource, and no `updatedAfter` query parameter exists. There is currently no way to ask the API "what changed since X" — the periodic syncs for Article/BlogPost/Announcement page strictly by id (`newerThan`/`after`), so they structurally cannot discover edits to rows older than the current local max id, regardless of DB conflict strategy. Fixing that requires backend support and is out of scope here (see Follow-up).

## Goals

- Any row a sync path *does* fetch — whether newly created or already locally present — gets its content fields overwritten with the server's current values, instead of being silently ignored on conflict.
- The local-only `opened` (unread) flag on `Article`, `BlogPost`, `Announcement` survives an upsert of that row untouched.
- The article detail screen refreshes the *entire* article (not just `commentCount`) when opened, matching the iOS sibling app's behaviour (commit `5e3f032`, "Request fresh copy of article when viewed") and giving readers the freshest copy of the one article they're actually looking at.

## Non-goals

- Detecting or fetching edits to older articles/blog posts/announcements that the reader never reopens and that fall outside the periodic sync's id cursor. That requires the backend to expose an update-tracking field (e.g. `updatedAt`) and an `updatedAfter`-style cursor; noted as a follow-up, not attempted here.
- Any change to `SearchArticleScreen` — it renders directly from the search API's `ArticleDto` each time and is never Room-backed, so it's already always fresh.
- Adding test infrastructure. The codebase currently has no real test coverage (only unmodified Android Studio template tests); this change is verified manually, consistent with that.

## Architecture

`FeedSyncService<Dto, Model>` (`data/sync/FeedSyncService.kt`) renames its abstract `insert(models: List<Model>)` to `upsert(models: List<Model>)`; `sync()` calls `upsert(models)` in place of `insert(models)`. No other change to this class — it stays a thin fetch → parse → transform → upsert shell, applied uniformly across all five concrete sync services (Article, BlogPost, Announcement, HistoryEntry, Editor) for consistency and to future-proof against any future cursor change, even though today only Editor and HistoryEntry syncs actually re-fetch overlapping rows.

Each `*Store` DAO replaces its `@Insert(onConflict = OnConflictStrategy.IGNORE)` with an `upsert(...)` built from a raw SQL `INSERT ... ON CONFLICT(id) DO UPDATE SET ...` — a single atomic statement per row, no read-before-write, no race window. Room binds raw `@Query` upserts one row at a time, so each DAO gets:

- a private single-row query, e.g. `upsertOne(id, slug, title, body, ...)`
- a `@Transaction`-wrapped default method `upsert(models: List<Model>)` that loops over the list calling `upsertOne` for each — same call shape (`store.upsert(models)`) the sync services already use, same atomicity guarantee the old list-based `@Insert` gave.

## Components

Column lists per entity, derived from the actual Room models:

| Entity | Columns updated on conflict | Column excluded (local-only) |
|---|---|---|
| `Article` | `slug, title, body, categoryId, categorySlug, publishedAt, thumbnailExtension, editorComment, commentCount, authors` | `opened` |
| `BlogPost` | `blogId, blogName, title, body, authorName, publishedAt` | `opened` |
| `Announcement` | `title, place, happeningAt, body, publishedBy, publishedAt` | `opened` |
| `Editor` | `name, bio, blogId, blogTitle` | — (no local-only column) |
| `HistoryEntry` | `year, month, day, event, eventDate` | — (no local-only column) |

`ArticleSyncService.refreshCommentCount(articleId)` is renamed to `refreshArticle(articleId)` and widened: it still fetches `/articles/{id}` and parses the `ArticleDto`, but now calls `store.upsert(listOf(transform(dto)))` instead of `store.updateCommentCount(...)`. `ArticleStore.updateCommentCount` becomes dead code and is deleted. `ArticleScreen`'s `LaunchedEffect(article.id) { viewModel.refreshCommentCount(article.id) }` is updated to call the renamed `refreshArticle`.

## Data flow

**Periodic / pull-to-refresh sync** (News, Blog, Announcements, History, Editors): unchanged in shape. Each service still fetches using its existing cursor (`newerThan`/`after` id for Article/BlogPost/Announcement, full list for Editor, per-day for HistoryEntry) and now upserts instead of inserts. Editor and HistoryEntry benefit immediately — an edited bio or corrected historical event now overwrites the stale local row. Article/BlogPost/Announcement's periodic sync still can't reach edits to rows older than the local max id (that's the id-cursor limitation, not a DB conflict-strategy problem); upserting there is correct and harmless but not the fix for stale old content.

**Article-open refresh** (the main lever here): `ArticleScreen`'s `LaunchedEffect(article.id)` calls `viewModel.refreshArticle(article.id)` as soon as the screen is shown. This fetches the single-article endpoint, transforms the DTO, and upserts it — any server-side edit made since the last sync is applied the moment the reader opens that article. `opened` (already flipped `true` by `markRead` on tap, before navigation) is preserved by the DAO's excluded-column upsert.

## Error handling

- `sync()`'s existing try/catch → `SyncState.Failed(...)` is unchanged; swapping the DB write from `insert` to `upsert` doesn't change what can throw.
- `refreshArticle` keeps the current silent-failure behaviour (`catch (_: Exception) {}`) — a failed best-effort refresh on screen-open leaves the reader looking at the last-synced copy with no error banner, matching today's `refreshCommentCount` behaviour.
- Each DAO's `upsert(models: List<Model>)` stays `@Transaction`-wrapped: a batch fully applies or fully rolls back, same guarantee the old list-based `@Insert` gave.
- Server-side deletions are out of scope and unchanged: a removed article simply stops appearing in future fetches; its stale local copy remains, exactly as today.

## Testing

None added. The codebase has no existing real test coverage; this change is verified by running the app manually (edit an article server-side or via a test fixture, confirm the open article screen picks up the change while `opened` state is preserved across a periodic sync).

## Follow-up (not part of this change)

Catching edits to articles/blog posts/announcements that a reader never reopens, and that fall outside the periodic sync's id cursor, requires the backend to expose an update-tracking field (e.g. `updatedAt`) and an `updatedAfter`-style query parameter. This is a backend change, not something the client can work around, and is noted here only as a known limitation for future work.
