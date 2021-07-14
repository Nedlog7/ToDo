package ru.yandex.todo.db.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "deleted_task_table")
public class DeletedTaskEntity {

    @PrimaryKey()
    public long id;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

}
