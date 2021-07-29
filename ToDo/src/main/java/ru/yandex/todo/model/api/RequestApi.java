package ru.yandex.todo.model.api;

import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.observers.DefaultObserver;
import io.reactivex.schedulers.Schedulers;
import ru.yandex.todo.model.db.dao.SyncTaskDao;
import ru.yandex.todo.model.db.dao.TaskDao;
import ru.yandex.todo.model.models.SyncTask;
import ru.yandex.todo.model.models.Task;
import ru.yandex.todo.utils.Constants;

public class RequestApi {

    private final IRequestApi requestApi;
    private final TaskDao taskDao;
    private final SyncTaskDao syncTaskDao;

    @Inject
    public RequestApi(IRequestApi requestApi, TaskDao taskDao, SyncTaskDao syncTaskDao) {
        this.requestApi = requestApi;
        this.taskDao = taskDao;
        this.syncTaskDao = syncTaskDao;
    }

    public void getTasks() {

        requestApi.getTasks().observeOn(Schedulers.io())
                .subscribe(new DefaultObserver<List<Task>>() {
                    @Override
                    public void onNext(@NotNull List<Task> tasks) {
                        Log.d(Constants.TAG, "onNext");
                        syncTaskDao.deleteAll();
                        taskDao.deleteAndInsert(tasks);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Log.e(Constants.TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(Constants.TAG, "onComplete");
                    }
                });

    }

    public void insertTask(Task task) {

        requestApi.insertTask(task).observeOn(Schedulers.io())
                .subscribe(new DefaultObserver<Task>() {
                    @Override
                    public void onNext(@NotNull Task t) {
                        Log.d(Constants.TAG, "onNext");

                        if (task.getUpdatedAt() == t.getUpdatedAt()) {
                            syncTaskDao.delete(t.getId());
                        }
                        else if (task.getUpdatedAt() < t.getUpdatedAt()) {
                            syncTaskDao.delete(t.getId());
                            taskDao.update(t);
                        }
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Log.e(Constants.TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(Constants.TAG, "onComplete");
                    }
                });

    }

    public void updateTask(Task task) {

        requestApi.updateTask(task.getId(), task).observeOn(Schedulers.io())
                .subscribe(new DefaultObserver<Task>() {
                    @Override
                    public void onNext(@NotNull Task t) {
                        Log.d(Constants.TAG, "onNext");

                        if (task.getUpdatedAt() == t.getUpdatedAt()) {
                            syncTaskDao.delete(t.getId());
                        }
                        else if (task.getUpdatedAt() < t.getUpdatedAt()) {
                            syncTaskDao.delete(t.getId());
                            taskDao.update(t);
                        }
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Log.e(Constants.TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(Constants.TAG, "onComplete");
                    }
                });

    }

    public void deleteTask(Task task) {

        requestApi.deleteTask(task.getId()).observeOn(Schedulers.io())
                .subscribe(new DefaultObserver<Task>() {
                    @Override
                    public void onNext(@NotNull Task t) {
                        Log.d(Constants.TAG, "onNext");
                        syncTaskDao.delete(t.getId());
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Log.e(Constants.TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(Constants.TAG, "onComplete");
                    }
                });

    }

    public void syncTasks(SyncTask task) {

        requestApi.syncTasks(task).observeOn(Schedulers.io())
                .subscribe(new DefaultObserver<List<Task>>() {
                    @Override
                    public void onNext(@NotNull List<Task> tasks) {
                        Log.d(Constants.TAG, "onNext");
                        syncTaskDao.deleteAll();
                        taskDao.deleteAndInsert(tasks);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        Log.e(Constants.TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(Constants.TAG, "onComplete");
                    }
                });

    }

}
