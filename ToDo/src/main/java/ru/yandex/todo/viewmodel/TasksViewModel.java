package ru.yandex.todo.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import ru.yandex.todo.db.TaskRepository;
import ru.yandex.todo.db.entity.SyncTaskEntity;
import ru.yandex.todo.model.Task;

public class TasksViewModel extends AndroidViewModel {

    private final TaskRepository taskRepository;
    private LiveData<List<Task>> tasks;
    private MutableLiveData<Boolean> hideDone;
    private LiveData<List<Task>> completedTasks;

    public TasksViewModel(Application application) {
        super(application);

        taskRepository = TaskRepository.getInstance(application);

        hideDone = taskRepository.getHideDone();
        tasks = taskRepository.getTasks();

        completedTasks = taskRepository.getCompletedTasks();
    }

    public LiveData<List<Task>> getTasks() {
        return tasks;
    }

    public LiveData<List<Task>> getCompletedTasks() {
        return completedTasks;
    }

    public MutableLiveData<Boolean> getHideDone() {
        return hideDone;
    }

    public void insert(Task task) {
        taskRepository.insert(task);
    }

    public void update(Task task) {
        taskRepository.update(task);
    }

    public void delete(Task task) {
        taskRepository.delete(task);
    }

}
