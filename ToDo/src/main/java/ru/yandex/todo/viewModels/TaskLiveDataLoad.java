package ru.yandex.todo.viewModels;

import androidx.lifecycle.LiveData;

import ru.yandex.todo.models.TaskInfoList;

public class TaskLiveDataLoad extends LiveData<TaskInfoList> {

    private static final TaskLiveDataLoad instance = new TaskLiveDataLoad();

    public static TaskLiveDataLoad get() {
        return instance;
    }

    public synchronized void loadTask(TaskInfoList taskInfoList){
        postValue(taskInfoList);
    }

}
