package ru.yandex.todo.model.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SyncTask {

    @SerializedName("deleted")
    @Expose
    private List<Long> deletedTasksId;

    @SerializedName("other")
    @Expose
    private List<Task> otherTasks;

    public void setDeletedTasksId(List<Long> deletedTasksId) {
        this.deletedTasksId = deletedTasksId;
    }

    public void setOtherTasks(List<Task> otherTasks) {
        this.otherTasks = otherTasks;
    }

    public List<Long> getDeletedTasksId() {
        return deletedTasksId;
    }

    public List<Task> getOtherTasks() {
        return otherTasks;
    }

}
