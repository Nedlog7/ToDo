package ru.yandex.todo.worker;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.jetbrains.annotations.NotNull;

import ru.yandex.todo.db.TaskRepository;

public class SyncWorker extends Worker {

    public SyncWorker(@NonNull @NotNull Context context, @NonNull @NotNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @NotNull
    @Override
    public Result doWork() {

        TaskRepository taskRepository = TaskRepository.getInstance(getApplicationContext());
        taskRepository.syncTasks();

        return Result.success();
    }

}
