package ru.yandex.todo.di;

import android.content.Context;

import androidx.room.Room;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import ru.yandex.todo.model.db.TaskRoomDatabase;
import ru.yandex.todo.model.db.dao.SyncTaskDao;
import ru.yandex.todo.model.db.dao.TaskDao;
import ru.yandex.todo.utils.Constants;

@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {

    @Provides
    @Singleton
    public static TaskRoomDatabase provideDatabase(@ApplicationContext Context context) {
         return Room.databaseBuilder(context.getApplicationContext(),
                 TaskRoomDatabase.class, Constants.TASK_DATABASE)
                 .fallbackToDestructiveMigration()
                 .build();
    }

    @Provides
    public TaskDao provideTaskDao(TaskRoomDatabase database) {
        return database.taskDao();
    }

    @Provides
    public SyncTaskDao provideSyncTaskDao(TaskRoomDatabase database) {
        return database.syncTaskDao();
    }

}
