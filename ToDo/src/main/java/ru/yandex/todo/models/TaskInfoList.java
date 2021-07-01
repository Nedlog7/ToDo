package ru.yandex.todo.models;

import java.util.ArrayList;
import java.util.List;

public class TaskInfoList {

    private final List<TaskInfo> taskInfoList;
    private final List<String> createDateList;

    public TaskInfoList(List<TaskInfo> taskInfoList, List<String> createDateList) {
        this.taskInfoList = taskInfoList;
        this.createDateList = createDateList;
    }

    public List<TaskInfo> getTaskInfoList() {
        return taskInfoList;
    }

    public List<String> getCreateDateList() {
        return createDateList;
    }

}
