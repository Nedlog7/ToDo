package ru.yandex.todo.di;

import android.content.Context;

import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import io.reactivex.subjects.PublishSubject;
import ru.yandex.todo.model.api.IRequestApi;
import ru.yandex.todo.model.api.RequestApi;
import ru.yandex.todo.model.db.dao.SyncTaskDao;
import ru.yandex.todo.model.db.dao.TaskDao;
import ru.yandex.todo.model.models.Task;
import ru.yandex.todo.model.repository.TaskRepository;

@Module
@InstallIn(SingletonComponent.class)
public class TaskRepositoryModule {

    @Provides
    @Singleton
    public static RequestApi provideRequestApi(IRequestApi requestApi, TaskDao taskDao,
                                               SyncTaskDao syncTaskDao) {
        return new RequestApi(requestApi, taskDao, syncTaskDao);
    }

    @Provides
    @Singleton
    public static TaskRepository provideTaskRepository(@ApplicationContext Context context, TaskDao taskDao,
                                                       SyncTaskDao syncTaskDao, RequestApi requestApi) {
        return new TaskRepository(context, taskDao, syncTaskDao, requestApi);
    }

}
