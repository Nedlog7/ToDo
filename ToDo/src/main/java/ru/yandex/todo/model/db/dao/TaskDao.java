package ru.yandex.todo.model.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

import ru.yandex.todo.model.models.Task;
import ru.yandex.todo.utils.Constants;

@Dao
public interface TaskDao {

    @Query("SELECT * FROM " + Constants.TASK_TABLE + " WHERE (done = 0 OR done != :hideDone OR updatedAt >= :date) ORDER BY length(priority) DESC, deadline")
    LiveData<List<Task>> getTasks(boolean hideDone, long date);

    @Query("SELECT * FROM " + Constants.TASK_TABLE + " WHERE done = 1")
    LiveData<List<Task>> getCompletedTasks();

    @Query("SELECT Count(*) FROM " + Constants.TASK_TABLE + " WHERE (deadline > :startOfDay AND deadline < :endOfDay)")
    int getTasksForToday(long startOfDay, long endOfDay);

    @Query("SELECT * FROM " + Constants.TASK_TABLE + " WHERE id IN (:idList)")
    List<Task> getTasksForSync(List<Long> idList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

    @Transaction
    default void deleteAndInsert(List<Task> tasks) {
        deleteAll();
        insertAll(tasks);
    }

    @Query("DELETE FROM " + Constants.TASK_TABLE)
    void deleteAll();

    @Insert
    void insertAll(List<Task> tasks);

}
