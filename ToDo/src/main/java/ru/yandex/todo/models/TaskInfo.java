package ru.yandex.todo.models;

import android.os.Parcel;
import android.os.Parcelable;

public class TaskInfo implements Parcelable {

    private String task;
    private boolean isImportant = false;
    private boolean isCompleted = false;
    private String remindDate;
    private long rDate;
    private String dueDate;
    private long dDate;
    private String createDate;

    public TaskInfo() {}

    public void setTask(String task) {
        this.task = task;
    }

    public void setImportant(boolean important) {
        isImportant = important;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public void setRemindDate(String remindDate) {
        this.remindDate = remindDate;
    }

    public void setrDate(long rDate) {
        this.rDate = rDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public void setdDate(long dDate) {
        this.dDate = dDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getTask() {
        return task;
    }

    public boolean isImportant() {
        return isImportant;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public String getRemindDate() {
        return remindDate;
    }

    public long getrDate() {
        return rDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public long getdDate() {
        return dDate;
    }

    public String getCreateDate() {
        return createDate;
    }

    protected TaskInfo(Parcel in) {
        task = in.readString();
        isImportant = in.readBoolean();
        isCompleted = in.readBoolean();
        remindDate = in.readString();
        rDate = in.readLong();
        dueDate = in.readString();
        dDate = in.readLong();
        createDate = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(task);
        dest.writeBoolean(isImportant);
        dest.writeBoolean(isCompleted);
        dest.writeString(remindDate);
        dest.writeLong(rDate);
        dest.writeString(dueDate);
        dest.writeLong(dDate);
        dest.writeString(createDate);
    }

    public static final Creator<TaskInfo> CREATOR = new Creator<TaskInfo>() {

        @Override
        public TaskInfo createFromParcel(Parcel in) {
            return new TaskInfo(in);
        }

        @Override
        public TaskInfo[] newArray(int size) {
            return new TaskInfo[size];
        }

    };

}
