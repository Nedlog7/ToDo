package ru.yandex.todo.db;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import io.reactivex.disposables.Disposable;
import ru.yandex.todo.model.SyncTask;
import ru.yandex.todo.network.RequestApi;
import ru.yandex.todo.db.dao.SyncTaskDao;
import ru.yandex.todo.db.dao.TaskDao;
import ru.yandex.todo.db.entity.SyncTaskEntity;
import ru.yandex.todo.model.Task;
import ru.yandex.todo.util.Utils;

public class TaskRepository {

    private static TaskRepository instance;

    private final TaskDao taskDao;
    private final SyncTaskDao syncTaskDao;

    private LiveData<List<Task>> allTasks;
    private LiveData<List<Task>> completedTasks;
    private MutableLiveData<Boolean> hideDone;

    private RequestApi requestApi;
    private Disposable deleteSyncTask;
    private Disposable updateSyncTask;
    private Disposable deleteAndInsertTask;

    private Context context;

    private boolean isNetworkAvailable;

    private TaskRepository(Context context) {
        this.context = context;

        TaskRoomDatabase db = TaskRoomDatabase.getDatabase(context);
        taskDao = db.taskDao();
        syncTaskDao = db.syncTaskDao();

        hideDone = new MutableLiveData<>(false);
        allTasks = Transformations.switchMap(hideDone, input -> taskDao.getTasks(input, Instant.now().getEpochSecond()));
        completedTasks = taskDao.getCompletedTasks();

        requestApi = new RequestApi(context);

        syncTaskConsumer();
        registerNetworkListener();

    }

    public static TaskRepository getInstance(Context context) {
        if (instance == null) {
            synchronized (TaskRepository.class) {
                if (instance == null) {
                    instance = new TaskRepository(context);
                }
            }
        }
        return instance;
    }

    private void syncTaskConsumer() {
        deleteSyncTask = requestApi.deleteSyncTaskEvent.subscribe(aLong ->
                CompletableFuture.runAsync(() -> syncTaskDao.delete(aLong)));

        updateSyncTask = requestApi.updateSyncTaskEvent.subscribe(task -> CompletableFuture.runAsync(() -> {
            syncTaskDao.delete(task.getId());
            taskDao.update(task);
        }));

        deleteAndInsertTask = requestApi.deleteAndInsertTaskEvent.subscribe(tasks -> CompletableFuture.runAsync(() -> {
            syncTaskDao.deleteSyncTable();
            taskDao.deleteAndInsert(tasks);
        }));
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
        requestApi.insertTask(task);
    }

    public void update(Task task) {
        CompletableFuture.runAsync(() -> {
            taskDao.update(task);
            syncTaskDao.insert(new SyncTaskEntity(task.getId(), false));
        });
        requestApi.updateTask(task);
    }

    public void delete(Task task) {
        CompletableFuture.runAsync(() -> {
            taskDao.delete(task);
            syncTaskDao.insert(new SyncTaskEntity(task.getId(), true));
        });
        requestApi.deleteTask(task);
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
            requestApi.syncTasks(syncTask);
        }
        else
            requestApi.getTasks();

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
