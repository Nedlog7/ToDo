package ru.yandex.todo.view.adapter;

import android.graphics.Paint;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.time.Instant;

import io.reactivex.subjects.PublishSubject;
import ru.yandex.todo.R;
import ru.yandex.todo.model.models.Priority;
import ru.yandex.todo.model.models.Task;
import ru.yandex.todo.utils.Utils;

public class TaskListAdapter extends ListAdapter<Task, TaskListAdapter.TaskViewHolder> {

    private final FragmentActivity activity;

    private final PublishSubject<Task> clickTaskSubject;
    private final PublishSubject<Task> clickDoneSubject;
    private final PublishSubject<Task> clickPrioritySubject;

    public TaskListAdapter(@NonNull DiffUtil.ItemCallback<Task> diffCallback,
                           FragmentActivity activity,
                           PublishSubject<Task> clickTaskSubject,
                           PublishSubject<Task> clickDoneSubject,
                           PublishSubject<Task> clickPrioritySubject) {

        super(diffCallback);

        this.activity = activity;
        this.clickTaskSubject = clickTaskSubject;
        this.clickDoneSubject = clickDoneSubject;
        this.clickPrioritySubject = clickPrioritySubject;

    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_item, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public static class TaskDiff extends DiffUtil.ItemCallback<Task> {

        @Override
        public boolean areItemsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
            return oldItem.id == newItem.id;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Task oldItem, @NonNull Task newItem) {
            return oldItem.areContentsTheSame(newItem);
        }
    }

    protected class TaskViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivCompleted;
        private final ImageView ivImportant;
        private final TextView title;
        private final TextView subTitle;
        private final int defaultColor;
        private final int errorColor;

        public TaskViewHolder(View itemView) {

            super(itemView);

            ivCompleted = itemView.findViewById(R.id.ivCompleted);
            ivImportant = itemView.findViewById(R.id.ivImportant);
            title = itemView.findViewById(R.id.title);
            subTitle = itemView.findViewById(R.id.subTitle);
            LinearLayout taskLayout = itemView.findViewById(R.id.taskLayout);

            defaultColor = subTitle.getCurrentTextColor();
            TypedValue typedValue = new TypedValue();
            activity.getTheme().resolveAttribute(R.attr.colorError, typedValue, true);
            errorColor = typedValue.data;

            ivCompleted.setOnClickListener(v -> clickDoneSubject.onNext(getItem(getAdapterPosition())));
            ivImportant.setOnClickListener(v -> clickPrioritySubject.onNext(getItem(getAdapterPosition())));
            taskLayout.setOnClickListener(v -> clickTaskSubject.onNext(getItem(getAdapterPosition())));

        }

        public void bind(Task task) {

            ivCompleted.setImageResource(task.isDone() ? R.drawable.ic_check_circle
                    : R.drawable.ic_uncheck_circle);

            ivImportant.setImageResource(task.getPriority() == Priority.IMPORTANT ? R.drawable.ic_favorite
                    : R.drawable.ic_favorite_border);

            title.setText(task.getText());
            title.setPaintFlags(task.isDone() ?
                    title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG
                    : title.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));

            long deadline = task.getDeadline();
            if (deadline == Long.MAX_VALUE) {
                subTitle.setVisibility(View.GONE);
            }
            else {
                subTitle.setVisibility(View.VISIBLE);
                subTitle.setTextColor(deadline < Instant.now().getEpochSecond() ? errorColor : defaultColor);
                subTitle.setText(Utils.formatDate(deadline));
            }

        }

    }

}
