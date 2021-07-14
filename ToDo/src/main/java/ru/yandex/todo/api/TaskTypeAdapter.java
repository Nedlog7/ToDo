package ru.yandex.todo.api;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

import ru.yandex.todo.model.Priority;
import ru.yandex.todo.model.Task;

public class TaskTypeAdapter extends TypeAdapter<Task> {

    @Override
    public void write(JsonWriter out, Task task) throws IOException {
        out.beginObject();
        out.name("id").value(String.valueOf(task.getId()));
        out.name("text").value(task.getText());
        out.name("importance").value(task.getPriority().name().toLowerCase());
        out.name("done").value(task.isDone());
        out.name("deadline").value(task.getDeadline());
        out.name("created_at").value(task.getCreatedAt());
        out.name("updated_at").value(task.getUpdatedAt());
        out.endObject();
    }

    @Override
    public Task read(JsonReader in) throws IOException {
        final Task task = new Task();

        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "id":
                    task.setId(Long.parseLong(in.nextString()));
                    break;
                case "text":
                    task.setText(in.nextString());
                    break;
                case "importance":
                    task.setPriority(Priority.valueOf(in.nextString().toUpperCase()));
                    break;
                case "done":
                    task.setDone(in.nextBoolean());
                    break;
                case "deadline":
                    task.setDeadline(in.nextLong());
                    break;
                case "created_at":
                    task.setCreatedAt(in.nextLong());
                    break;
                case "updated_at":
                    task.setUpdatedAt(in.nextLong());
                    break;
            }
        }
        in.endObject();

        return task;
    }

}
