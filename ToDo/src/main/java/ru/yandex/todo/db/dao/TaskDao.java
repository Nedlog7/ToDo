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
public interface TaskDao {

    @Query("SELECT * FROM task_table WHERE (done = 0 OR done != :hideDone OR updatedAt >= :date) ORDER BY length(priority) DESC, deadline")
    LiveData<List<Task>> getTasks(int hideDone, long date);

    @Query("SELECT * FROM task_table WHERE done = 1")
    LiveData<List<Task>> getCompletedTasks();

    @Query("SELECT Count(*) FROM task_table WHERE (deadline > :startOfDay AND deadline < :endOfDay)")
    int getTasksForToday(long startOfDay, long endOfDay);

    @Query("SELECT * FROM task_table WHERE sync = 0")
    List<Task> getUnsyncTasks();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Task task);

    @Update
    void update(Task task);

    @Delete
    void delete(Task task);

}
