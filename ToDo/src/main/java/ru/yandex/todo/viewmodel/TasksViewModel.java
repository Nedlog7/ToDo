package ru.yandex.todo.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.time.Instant;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import ru.yandex.todo.model.models.Priority;
import ru.yandex.todo.model.models.Task;
import ru.yandex.todo.model.repository.TaskRepository;

@HiltViewModel
public class TasksViewModel extends ViewModel {

    TaskRepository taskRepository;

    private LiveData<List<Task>> tasks;
    private MutableLiveData<Boolean> hideDone;
    private LiveData<List<Task>> completedTasks;

    @Inject
    public TasksViewModel(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
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

    public void onSaveTaskClick(Task task, boolean isNewTask) {
        if (isNewTask)
            taskRepository.insert(task);
        else
            taskRepository.update(task);
    }

    public void onDeleteTaskClick(Task task) {
        taskRepository.delete(task);
    }

    public void onHideDoneClick(boolean hideDone) {
        this.hideDone.postValue(hideDone);
    }

    public void onDoneClick(Task task) {
        Task clone = (Task) task.clone();
        clone.setDone(!clone.isDone());
        clone.setUpdatedAt(Instant.now().getEpochSecond());

        taskRepository.update(clone);
    }

    public void onPriorityClick(Task task) {
        Task clone = (Task) task.clone();
        clone.setPriority(clone.getPriority() == Priority.IMPORTANT
                ? Priority.LOW : Priority.IMPORTANT);
        clone.setUpdatedAt(Instant.now().getEpochSecond());

        taskRepository.update(clone);
    }

}
