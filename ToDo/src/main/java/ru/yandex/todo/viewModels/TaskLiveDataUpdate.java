package ru.yandex.todo.viewModels;

import androidx.lifecycle.LiveData;

import ru.yandex.todo.models.TaskInfo;

public class TaskLiveDataUpdate extends LiveData<TaskInfo> {

    private static final TaskLiveDataUpdate instance = new TaskLiveDataUpdate();

    public static TaskLiveDataUpdate get() {
        return instance;
    }

    public synchronized void updateTask(TaskInfo taskInfo){
        postValue(taskInfo);
    }

}
