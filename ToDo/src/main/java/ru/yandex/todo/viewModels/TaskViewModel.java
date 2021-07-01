package ru.yandex.todo.viewModels;

import androidx.lifecycle.ViewModel;

public class TaskViewModel extends ViewModel {

    private final TaskLiveDataLoad taskLiveDataLoad;
    private final TaskLiveDataDelete taskLiveDataDelete;
    private final TaskLiveDataUpdate taskLiveDataUpdate;
    private final TaskLiveDataAdd taskLiveDataAdd;

    public TaskViewModel() {
        taskLiveDataLoad = TaskLiveDataLoad.get();
        taskLiveDataDelete = TaskLiveDataDelete.get();
        taskLiveDataUpdate = TaskLiveDataUpdate.get();
        taskLiveDataAdd = TaskLiveDataAdd.get();
    }

    public TaskLiveDataLoad getTaskLiveDataLoad() {
        return taskLiveDataLoad;
    }

    public TaskLiveDataDelete getTaskLiveDataDelete() {
        return taskLiveDataDelete;
    }

    public TaskLiveDataUpdate getTaskLiveDataUpdate() {
        return taskLiveDataUpdate;
    }

    public TaskLiveDataAdd getTaskLiveDataAdd() {
        return taskLiveDataAdd;
    }
}
