package pl.lewica.lewicapl.android.di

import androidx.room.Room
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import pl.lewica.lewicapl.android.data.database.AppDatabase

val appModule = module {
  single {
    Room.databaseBuilder(
      androidContext(),
      AppDatabase::class.java,
      "lewicapl.db"
    ).fallbackToDestructiveMigration().build()
  }
  single { get<AppDatabase>().articleStore() }
  single { get<AppDatabase>().blogPostStore() }
  single { get<AppDatabase>().announcementStore() }
  single { get<AppDatabase>().historyEntryStore() }
  single { get<AppDatabase>().editorStore() }
}
