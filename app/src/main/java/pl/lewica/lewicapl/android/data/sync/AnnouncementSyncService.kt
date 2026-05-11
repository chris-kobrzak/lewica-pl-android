package pl.lewica.lewicapl.android.data.sync

import pl.lewica.lewicapl.android.data.model.Announcement
import pl.lewica.lewicapl.android.data.store.AnnouncementStore
import pl.lewica.lewicapl.android.network.ApiEndpoints
import pl.lewica.lewicapl.android.network.FeedClient
import pl.lewica.lewicapl.android.parsing.FeedParser
import pl.lewica.lewicapl.android.parsing.dto.AnnouncementDto

class AnnouncementSyncService(
  client: FeedClient,
  parser: FeedParser<AnnouncementDto>,
  private val store: AnnouncementStore
) : FeedSyncService<AnnouncementDto, Announcement>(client, parser) {

  override suspend fun buildEndpoint() = ApiEndpoints.announcements(store.getMaxId())

  override fun transform(dto: AnnouncementDto) = Announcement(
    id = dto.id,
    title = dto.title,
    body = dto.body,
    publicationDate = dto.publicationDate
  )

  override suspend fun insert(models: List<Announcement>) = store.insert(models)
}
