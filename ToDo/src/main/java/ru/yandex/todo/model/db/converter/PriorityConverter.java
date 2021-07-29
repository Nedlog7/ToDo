package ru.yandex.todo.model.db.converter;

import androidx.room.TypeConverter;

import ru.yandex.todo.model.models.Priority;

public class PriorityConverter {

    @TypeConverter
    public static String fromPriority(Priority priority) {
        return priority.name();
    }

    @TypeConverter
    public static Priority toPriority(String priority) {
        return Priority.valueOf(priority);
    }

}
