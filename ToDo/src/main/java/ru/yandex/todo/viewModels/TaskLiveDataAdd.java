package ru.yandex.todo.viewModels;

import androidx.lifecycle.LiveData;

import ru.yandex.todo.models.TaskInfo;
import ru.yandex.todo.models.TaskInfoList;

public class TaskLiveDataAdd extends LiveData<TaskInfo> {

    private static final TaskLiveDataAdd instance = new TaskLiveDataAdd();

    public static TaskLiveDataAdd get() {
        return instance;
    }

    public synchronized void addTask(TaskInfo taskInfo){
        postValue(taskInfo);
    }

}
