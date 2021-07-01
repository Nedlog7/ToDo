package ru.yandex.todo.viewModels;

import androidx.lifecycle.LiveData;

import ru.yandex.todo.models.TaskInfo;

public class TaskLiveDataDelete extends LiveData<TaskInfo> {

    private static final TaskLiveDataDelete instance = new TaskLiveDataDelete();

    public static TaskLiveDataDelete get() {
        return instance;
    }

    public synchronized void deleteTask(TaskInfo taskInfo){
        postValue(taskInfo);
    }

}
