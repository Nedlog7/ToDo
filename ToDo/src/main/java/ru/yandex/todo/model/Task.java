package ru.yandex.todo.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "task_table")
public class Task implements Parcelable, Cloneable {

    @PrimaryKey()
    public long id;
    private String text;
    private Priority priority = Priority.LOW;
    private boolean done = false;
    private long deadline = Long.MAX_VALUE;
    private long createdAt;
    private long updatedAt;
    private boolean sync = false;

    public Task() {
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public void setDeadline(long deadline) {
        this.deadline = deadline;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setSync(boolean sync) {
        this.sync = sync;
    }

    public long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public Priority getPriority() {
        return priority;
    }

    public boolean isDone() {
        return done;
    }

    public long getDeadline() {
        return deadline;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public boolean isSync() {
        return sync;
    }

    protected Task(Parcel in) {
        id = in.readLong();
        text = in.readString();
        priority = Priority.valueOf(in.readString());
        done = in.readBoolean();
        deadline = in.readLong();
        createdAt = in.readLong();
        updatedAt = in.readLong();
        sync = in.readBoolean();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(text);
        dest.writeString(priority.name());
        dest.writeBoolean(done);
        dest.writeLong(deadline);
        dest.writeLong(createdAt);
        dest.writeLong(updatedAt);
        dest.writeBoolean(sync);
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {

        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }

    };

    @NonNull
    @Override
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}
