package ru.yandex.todo.db;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import ru.yandex.todo.api.RequestApi;
import ru.yandex.todo.db.dao.DeletedTaskDao;
import ru.yandex.todo.db.dao.TaskDao;
import ru.yandex.todo.model.Task;

public class TaskRepository {

    private final TaskDao taskDao;
    private final DeletedTaskDao deletedTaskDao;
    private LiveData<List<Task>> allTasks;
    private LiveData<List<Task>> completedTasks;
    private MutableLiveData<Boolean> hideDone;

    public TaskRepository(Context context) {
        TaskRoomDatabase db = TaskRoomDatabase.getDatabase(context);
        taskDao = db.taskDao();
        deletedTaskDao = db.deletedTaskDao();
        hideDone = new MutableLiveData<>(false);
        allTasks = Transformations.switchMap(hideDone, input -> taskDao.getTasks(input ? 1 : 0, Instant.now().getEpochSecond()));
        completedTasks = taskDao.getCompletedTasks();
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
        CompletableFuture.runAsync(() -> taskDao.insert(task));
    }

    public void update(Task task) {
        CompletableFuture.runAsync(() -> taskDao.update(task));
    }

    public void delete(Task task) {
        CompletableFuture.runAsync(() -> {
            taskDao.delete(task);
            deletedTaskDao.insert(task.getId());
        });
    }

    public List<Long> getDeletedTasks() {
        return deletedTaskDao.getDeletedTasks();
    }

    public void deleteAllDeletedTasks() {
        CompletableFuture.runAsync(deletedTaskDao::delete);
    }

    List<Task> getUnsyncTasks() {
        return taskDao.getUnsyncTasks();
    }

}
