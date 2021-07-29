package ru.yandex.todo.model.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import ru.yandex.todo.model.db.entity.SyncTaskEntity;
import ru.yandex.todo.utils.Constants;

@Dao
public interface SyncTaskDao {

    @Query("SELECT id FROM " + Constants.SYNC_TASK_TABLE + " WHERE :getDeletedTask = isDeleted")
    List<Long> getTasksId(boolean getDeletedTask);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SyncTaskEntity syncTask);

    @Query("DELETE FROM " + Constants.SYNC_TASK_TABLE)
    void deleteAll();

    @Query("DELETE FROM " + Constants.SYNC_TASK_TABLE + " WHERE id = :id")
    void delete(long id);

}