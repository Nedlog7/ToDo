package ru.yandex.todo.view.adapter;

import ru.yandex.todo.model.models.Task;

public interface IComparableItem {
    boolean areContentsTheSame(Task task);
}
