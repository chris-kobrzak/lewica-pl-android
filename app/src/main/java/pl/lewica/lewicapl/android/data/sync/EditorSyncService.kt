package pl.lewica.lewicapl.android.data.sync

import pl.lewica.lewicapl.android.data.model.Editor
import pl.lewica.lewicapl.android.data.store.EditorStore
import pl.lewica.lewicapl.android.network.ApiEndpoints
import pl.lewica.lewicapl.android.network.FeedClient
import pl.lewica.lewicapl.android.parsing.FeedParser
import pl.lewica.lewicapl.android.parsing.dto.EditorDto

class EditorSyncService(
  client: FeedClient,
  parser: FeedParser<EditorDto>,
  private val store: EditorStore
) : FeedSyncService<EditorDto, Editor>(client, parser) {

  override suspend fun buildEndpoint() = ApiEndpoints.editors()

  override fun transform(dto: EditorDto) = Editor(
    id = dto.id,
    name = dto.name,
    bio = dto.bio,
    blogId = dto.blog?.id,
    blogTitle = dto.blog?.title
  )

  override suspend fun upsert(models: List<Editor>) = store.upsert(models)
}