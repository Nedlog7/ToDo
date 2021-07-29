package ru.yandex.todo.model.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import ru.yandex.todo.utils.Constants;

@Entity(tableName = Constants.SYNC_TASK_TABLE)
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
