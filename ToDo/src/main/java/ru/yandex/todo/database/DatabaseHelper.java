package ru.yandex.todo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import ru.yandex.todo.models.TaskInfo;
import ru.yandex.todo.models.TaskInfoList;
import ru.yandex.todo.viewModels.TaskLiveDataAdd;
import ru.yandex.todo.viewModels.TaskLiveDataLoad;
import ru.yandex.todo.viewModels.TaskLiveDataDelete;
import ru.yandex.todo.viewModels.TaskLiveDataUpdate;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "TasksDatabase";
    public static final String TASKS_TABLE = "TasksTable";
    private static final int DATABASE_VERSION = 1;
    private static DatabaseHelper dbHelper;

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (dbHelper == null) {
            dbHelper = new DatabaseHelper(context);
        }
        return dbHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_FAVORITE_STOCKS_TABLE =
                " CREATE TABLE " + TASKS_TABLE +
                        " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        " task TEXT, " +
                        " isImportant INTEGER, " +
                        " isCompleted INTEGER, " +
                        " remindDate TEXT, " +
                        " rDate INTEGER, " +
                        " dueDate TEXT, " +
                        " dDate INTEGER, " +
                        " createDate TEXT);";

        db.execSQL(SQL_CREATE_FAVORITE_STOCKS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TASKS_TABLE);
            onCreate(db);
        }
    }

    public synchronized void addTask(TaskInfo taskInfo) {

        ContentValues values = new ContentValues();
        values.put("task", taskInfo.getTask());
        values.put("isImportant", taskInfo.isImportant() ? 1 : 0);
        values.put("isCompleted", taskInfo.isCompleted() ? 1 : 0);
        values.put("remindDate", taskInfo.getRemindDate());
        values.put("rDate", taskInfo.getrDate());
        values.put("dueDate", taskInfo.getDueDate());
        values.put("dDate", taskInfo.getdDate());
        values.put("createDate", taskInfo.getCreateDate());

        SQLiteDatabase db = getWritableDatabase();
        db.insertOrThrow(TASKS_TABLE, null, values);

        TaskLiveDataAdd.get().addTask(taskInfo);

    }

    public synchronized void updateTask(TaskInfo taskInfo) {

        ContentValues values = new ContentValues();
        values.put("task", taskInfo.getTask());
        values.put("isImportant", taskInfo.isImportant() ? 1 : 0);
        values.put("isCompleted", taskInfo.isCompleted() ? 1 : 0);
        values.put("remindDate", taskInfo.getRemindDate());
        values.put("rDate", taskInfo.getrDate());
        values.put("dueDate", taskInfo.getDueDate());
        values.put("dDate", taskInfo.getdDate());

        SQLiteDatabase db = getWritableDatabase();
        db.update(TASKS_TABLE, values,  "createDate = ?", new String[] {taskInfo.getCreateDate()});

        TaskLiveDataUpdate.get().updateTask(taskInfo);

    }

    public synchronized void setImportant(TaskInfo taskInfo) {

        ContentValues values = new ContentValues();
        values.put("isImportant", taskInfo.isImportant() ? 1 : 0);

        SQLiteDatabase db = getWritableDatabase();
        db.update(TASKS_TABLE, values,  "createDate = ?", new String[] {taskInfo.getCreateDate()});

    }

    public synchronized void setCompleted(TaskInfo taskInfo) {

        ContentValues values = new ContentValues();
        values.put("isCompleted", taskInfo.isCompleted() ? 1 : 0);

        SQLiteDatabase db = getWritableDatabase();
        db.update(TASKS_TABLE, values,  "createDate = ?", new String[] {taskInfo.getCreateDate()});

    }

    public synchronized void deleteTask(TaskInfo taskInfo) {

        SQLiteDatabase db = getWritableDatabase();
        db.delete(TASKS_TABLE, "createDate = ?", new String[] {taskInfo.getCreateDate()});

        TaskLiveDataDelete.get().deleteTask(taskInfo);

    }

    public void loadTasks(boolean loadCompleted) {

        List<TaskInfo> taskInfoList = new ArrayList<>();
        List<String> createDateList = new ArrayList<>();

        SQLiteDatabase db = getReadableDatabase();
        String[] projection = {
                "_id",
                "task",
                "isImportant",
                "isCompleted",
                "remindDate",
                "rDate",
                "dueDate",
                "dDate",
                "createDate"
        };

        Cursor cursor = db.query(TASKS_TABLE, projection,
                null, null, null, null, null);

        if(cursor != null) {
            while(cursor.moveToNext()) {


                boolean isCompleted = cursor.getInt(3) == 1;

                if (!loadCompleted && !isCompleted || loadCompleted && isCompleted) {

                    TaskInfo taskInfo = new TaskInfo();
                    taskInfo.setTask(cursor.getString(1));
                    taskInfo.setImportant(cursor.getInt(2) == 1);
                    taskInfo.setCompleted(isCompleted);
                    taskInfo.setRemindDate(cursor.getString(4));
                    taskInfo.setrDate(cursor.getLong(5));
                    taskInfo.setDueDate(cursor.getString(6));
                    taskInfo.setdDate(cursor.getLong(7));
                    taskInfo.setCreateDate(cursor.getString(8));

                    taskInfoList.add(taskInfo);
                    createDateList.add(taskInfo.getCreateDate());

                }

            }
            cursor.close();
            TaskLiveDataLoad.get().loadTask(new TaskInfoList(taskInfoList, createDateList));
        }

    }

}
