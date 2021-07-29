package ru.yandex.todo.model.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import ru.yandex.todo.model.db.converter.PriorityConverter;
import ru.yandex.todo.model.db.dao.SyncTaskDao;
import ru.yandex.todo.model.db.dao.TaskDao;
import ru.yandex.todo.model.db.entity.SyncTaskEntity;
import ru.yandex.todo.model.models.Task;

@Database(entities = {Task.class, SyncTaskEntity.class}, version = 1, exportSchema = false)
@TypeConverters(PriorityConverter.class)
public abstract class TaskRoomDatabase extends RoomDatabase {

    public abstract TaskDao taskDao();
    public abstract SyncTaskDao syncTaskDao();

}
