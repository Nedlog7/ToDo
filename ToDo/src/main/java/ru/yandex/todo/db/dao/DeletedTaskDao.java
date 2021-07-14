package ru.yandex.todo.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import ru.yandex.todo.model.Task;

@Dao
public interface DeletedTaskDao {

    @Query("SELECT * FROM deleted_task_table")
    List<Long> getDeletedTasks();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(long id);

    @Query("DELETE FROM deleted_task_table")
    void delete();

}