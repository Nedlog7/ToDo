package ru.yandex.todo.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sync_task_table")
public class SyncTaskEntity {

    @PrimaryKey()
    public long id;
    private final boolean isDeleted;

    public SyncTaskEntity(long id, boolean isDeleted) {
        this.id = id;
        this.isDeleted = isDeleted;
    }

    public long getId() {
        return id;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

}
