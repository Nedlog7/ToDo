package ru.yandex.todo;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.work.Configuration;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

import dagger.hilt.android.HiltAndroidApp;
import ru.yandex.todo.model.repository.TaskRepository;

@HiltAndroidApp
public class ToDoApp extends Application implements Configuration.Provider {

    @Inject
    HiltWorkerFactory workerFactory;

    @NonNull
    @Override
    public Configuration getWorkManagerConfiguration() {
        return new Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .build();
    }

}
