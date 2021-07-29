package ru.yandex.todo.worker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.hilt.work.HiltWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.jetbrains.annotations.NotNull;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import ru.yandex.todo.model.repository.TaskRepository;

@HiltWorker
public class SyncWorker extends Worker {

    private final TaskRepository taskRepository;

    @AssistedInject
    public SyncWorker(@NonNull @Assisted Context context, @NonNull @Assisted WorkerParameters workerParams,
                      TaskRepository taskRepository) {
        super(context, workerParams);
        this.taskRepository = taskRepository;
    }

    @NonNull
    @NotNull
    @Override
    public Result doWork() {
        taskRepository.syncTasks();
        return Result.success();
    }

}
