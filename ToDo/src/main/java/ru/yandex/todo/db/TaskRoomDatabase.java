package ru.yandex.todo.db;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.CompletableFuture;

import ru.yandex.todo.db.dao.SyncTaskDao;
import ru.yandex.todo.db.dao.TaskDao;
import ru.yandex.todo.db.entity.SyncTaskEntity;
import ru.yandex.todo.model.Task;

@Database(entities = {Task.class, SyncTaskEntity.class}, version = 1, exportSchema = false)
@TypeConverters(Converter.class)
abstract class TaskRoomDatabase extends RoomDatabase {

    abstract TaskDao taskDao();
    abstract SyncTaskDao syncTaskDao();

    private static volatile TaskRoomDatabase INSTANCE;

    static TaskRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (TaskRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            TaskRoomDatabase.class, "task_database")
                            //.addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            CompletableFuture.runAsync(() -> {

                TaskDao dao = INSTANCE.taskDao();
                for (int i = 0; i < 10; i++) {

                    Task task = new Task();
                    task.setText("task_" + i);

                    dao.insert(task);

                }

            });

        }
    };

}
