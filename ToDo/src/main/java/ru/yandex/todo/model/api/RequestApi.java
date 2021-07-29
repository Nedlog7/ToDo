package ru.yandex.todo.model.api;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import ru.yandex.todo.model.models.SyncTask;
import ru.yandex.todo.model.models.Task;

public class RequestApi {

    private final IRequestApi requestApi;

    @Inject
    public RequestApi(IRequestApi requestApi) {
        this.requestApi = requestApi;
    }

    public Observable<List<Task>> getTasks() {
        return requestApi.getTasks();
    }

    public Observable<Task> insertTask(Task task) {
        return requestApi.insertTask(task);
    }

    public Observable<Task> updateTask(Task task) {
       return requestApi.updateTask(task.getId(), task);
    }

    public Observable<Task> deleteTask(Task task) {
        return requestApi.deleteTask(task.getId());
    }

    public Observable<List<Task>> syncTasks(SyncTask task) {
        return requestApi.syncTasks(task);
    }

}
