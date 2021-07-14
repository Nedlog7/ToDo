package ru.yandex.todo.db;

import androidx.room.TypeConverter;

import ru.yandex.todo.model.Priority;

public class Converter {

    @TypeConverter
    public static String fromPriority(Priority priority) {
        return priority.name();
    }

    @TypeConverter
    public static Priority toPriority(String priority) {
        return Priority.valueOf(priority);
    }

}
