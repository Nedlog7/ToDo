package ru.yandex.todo.model.repository;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.inject.Inject;

import io.reactivex.observers.DefaultObserver;
import io.reactivex.schedulers.Schedulers;
import ru.yandex.todo.model.api.RequestApi;
import ru.yandex.todo.model.db.dao.SyncTaskDao;
import ru.yandex.todo.model.db.dao.TaskDao;
import ru.yandex.todo.model.db.entity.SyncTaskEntity;
import ru.yandex.todo.model.models.SyncTask;
import ru.yandex.todo.model.models.Task;
import ru.yandex.todo.utils.Constants;
import ru.yandex.todo.utils.Utils;

public class TaskRepository {

    private final TaskDao taskDao;
    private final SyncTaskDao syncTaskDao;

    private LiveData<List<Task>> allTasks;
    private LiveData<List<Task>> completedTasks;
    private MutableLiveData<Boolean> hideDone;

    private final RequestApi requestApi;

    private final Context context;

    private boolean isNetworkAvailable;

    @Inject
    public TaskRepository(Context context, TaskDao taskDao, SyncTaskDao syncTaskDao, RequestApi requestApi) {

        this.context = context;

        this.taskDao = taskDao;
        this.syncTaskDao = syncTaskDao;

        hideDone = new MutableLiveData<>(false);
        allTasks = Transformations.switchMap(hideDone, input -> taskDao.getTasks(input, Instant.now().getEpochSecond()));
        completedTasks = taskDao.getCompletedTasks();

        this.requestApi = requestApi;

        registerNetworkListener();

    }

    public LiveData<List<Task>> getTasks() {
        return allTasks;
    }

    public LiveData<List<Task>> getCompletedTasks() {
        return completedTasks;
    }

    public int getTasksForToday(long startOfDay, long endOfDay) {
        return taskDao.getTasksForToday(startOfDay, endOfDay);
    }

    public MutableLiveData<Boolean> getHideDone() {
        return hideDone;
    }

    public void insert(Task task) {
        CompletableFuture.runAsync(() -> {
            taskDao.insert(task);
            syncTaskDao.insert(new SyncTaskEntity(task.getId(), false));
        });

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

    public void update(Task task) {
        CompletableFuture.runAsync(() -> {
            taskDao.update(task);
            syncTaskDao.insert(new SyncTaskEntity(task.getId(), false));
        });

        requestApi.updateTask(task).observeOn(Schedulers.io())
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

    public void delete(Task task) {
        CompletableFuture.runAsync(() -> {
            taskDao.delete(task);
            syncTaskDao.insert(new SyncTaskEntity(task.getId(), true));
        });

        requestApi.deleteTask(task).observeOn(Schedulers.io())
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

    private List<Long> getTasksId(boolean getDeletedTask) {
        return syncTaskDao.getTasksId(getDeletedTask);
    }

    private List<Task> getTasksForSync() {
        List<Long> idList = getTasksId(false);
        return taskDao.getTasksForSync(idList);
    }

    public void syncTasks() {
        List<Task> list = getTasksForSync();
        List<Long> id = getTasksId(true);

        if (id.size() != 0 || list.size() != 0) {
            SyncTask syncTask = new SyncTask();
            syncTask.setDeletedTasksId(id);
            syncTask.setOtherTasks(list);

            requestApi.syncTasks(syncTask).observeOn(Schedulers.io())
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
        else
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

    private void registerNetworkListener() {

        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkRequest networkRequest = new NetworkRequest.Builder().build();
        connectivityManager.registerNetworkCallback(networkRequest, new ConnectivityManager.NetworkCallback() {
            @Override
            public void onAvailable(Network network) {
                super.onAvailable(network);
                Log.i("Tag", "active connection");
                if (!isNetworkAvailable) {
                    isNetworkAvailable = true;
                    syncTasks();
                }
            }

            @Override
            public void onLosing(@NonNull Network network, int maxMsToLive) {
                super.onLosing(network, maxMsToLive);
                isNetworkAvailable = Utils.isNetworkAvailable(context);
            }

            @Override
            public void onLost(@NonNull Network network) {
                super.onLost(network);
                isNetworkAvailable = Utils.isNetworkAvailable(context);
            }

            @Override
            public void onUnavailable() {
                super.onUnavailable();
                isNetworkAvailable = Utils.isNetworkAvailable(context);
            }

        });
    }

}
