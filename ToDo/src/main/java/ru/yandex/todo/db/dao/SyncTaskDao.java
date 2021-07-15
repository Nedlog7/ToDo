package ru.yandex.todo.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import ru.yandex.todo.db.entity.SyncTaskEntity;

@Dao
public interface SyncTaskDao {

    @Query("SELECT id FROM sync_task_table WHERE :getDeletedTask = isDeleted")
    List<Long> getTasksId(boolean getDeletedTask);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SyncTaskEntity syncTask);

    @Query("DELETE FROM sync_task_table")
    void deleteSyncTable();

    @Query("DELETE FROM sync_task_table WHERE id = :id")
    void delete(long id);

}