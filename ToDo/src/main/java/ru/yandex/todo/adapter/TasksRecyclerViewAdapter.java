package ru.yandex.todo.adapter;

import android.content.Intent;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;

import ru.yandex.todo.MainActivity;
import ru.yandex.todo.R;
import ru.yandex.todo.TaskActivity;
import ru.yandex.todo.database.DatabaseHelper;
import ru.yandex.todo.models.TaskInfo;
import ru.yandex.todo.models.TaskInfoList;
import ru.yandex.todo.utils.Constants;

public class TasksRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final ArrayList<TaskInfo> taskInfoList;
    private final ArrayList<String> createDateList;
    private final DatabaseHelper dbHelper;
    private final MainActivity activity;

    public TasksRecyclerViewAdapter(MainActivity activity, DatabaseHelper dbHelper,
                                    ArrayList<TaskInfo> taskInfoList, ArrayList<String> createDateList) {

        this.activity = activity;
        this.dbHelper = dbHelper;
        this.taskInfoList = taskInfoList;
        this.createDateList = createDateList;

    }

    public void addTask(TaskInfo taskInfo) {

        taskInfoList.add(taskInfo);
        createDateList.add(taskInfo.getCreateDate());
        notifyItemInserted(taskInfoList.size() - 1);

    }

    public void addTaskList(TaskInfoList taskInfoList) {

        int startPosition = getItemCount();

        this.taskInfoList.addAll(taskInfoList.getTaskInfoList());
        createDateList.addAll(taskInfoList.getCreateDateList());

        notifyItemRangeInserted(startPosition, this.taskInfoList.size() - 1);

    }

    public void deleteTask(TaskInfo taskInfo) {

        int index = createDateList.indexOf(taskInfo.getCreateDate());
        taskInfoList.remove(index);
        createDateList.remove(index);

        notifyItemRemoved(index);

    }

    public void deleteCompletedTask() {

        for (int i = 0; i < taskInfoList.size();) {

            TaskInfo taskInfo = taskInfoList.get(i);
            if (taskInfo.isCompleted()) {
                taskInfoList.remove(i);
                createDateList.remove(i);

                notifyItemRemoved(i);
            }
            else i++;

        }

    }

    public TaskInfo getTask(int position) {

        return taskInfoList.get(position);

    }

    public void updateTask(TaskInfo taskInfo) {

        int index = createDateList.indexOf(taskInfo.getCreateDate());
        taskInfoList.set(index, taskInfo);

        notifyItemChanged(index);

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return taskInfoList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ItemViewHolder)holder).bind(taskInfoList.get(position));
    }

    private class ItemViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivCompleted;
        private final ImageView ivImportant;
        private final TextView title;
        private final TextView subTitle;
        private final int defaultColor;
        private final int errorColor;

        public ItemViewHolder(@NonNull View view) {

            super(view);

            ivCompleted = view.findViewById(R.id.ivCompleted);
            ivImportant = view.findViewById(R.id.ivImportant);
            title = view.findViewById(R.id.title);
            subTitle = view.findViewById(R.id.subTitle);
            LinearLayout taskLayout = view.findViewById(R.id.taskLayout);

            defaultColor = subTitle.getCurrentTextColor();
            TypedValue typedValue = new TypedValue();
            activity.getTheme().resolveAttribute(R.attr.colorError, typedValue, true);
            errorColor = typedValue.data;

            ivCompleted.setOnClickListener(v -> {

                int adapterPosition = getAdapterPosition();
                if (adapterPosition >= 0) {

                    TaskInfo taskInfo = taskInfoList.get(adapterPosition);
                    taskInfo.setCompleted(!taskInfo.isCompleted());
                    dbHelper.setCompleted(taskInfo);

                    if (taskInfo.isCompleted()) {
                        title.setPaintFlags(title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    }
                    else {
                        title.setPaintFlags(title.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    }

                    taskInfoList.set(adapterPosition, taskInfo);
                    notifyItemChanged(adapterPosition);

                }

            });

            ivImportant.setOnClickListener(v -> {

                int adapterPosition = getAdapterPosition();
                if (adapterPosition >= 0) {

                    TaskInfo taskInfo = taskInfoList.get(adapterPosition);
                    taskInfo.setImportant(!taskInfo.isImportant());
                    dbHelper.setImportant(taskInfo);

                    taskInfoList.set(adapterPosition, taskInfo);
                    notifyItemChanged(adapterPosition);

                }

            });

            taskLayout.setOnClickListener(v -> {

                int adapterPosition = getAdapterPosition();
                if (adapterPosition >= 0) {

                    TaskInfo taskInfo = taskInfoList.get(adapterPosition);
                    final Intent intent = new Intent(activity, TaskActivity.class);
                    intent.putExtra(Constants.INTENT_EXTRA_IN_TASK_MODEL, taskInfo);

                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }

            });

        }

        private void bind(TaskInfo taskInfo) {
            ivCompleted.setImageResource(taskInfo.isCompleted() ? R.drawable.ic_check_circle
                    : R.drawable.ic_uncheck_circle);

            ivImportant.setImageResource(taskInfo.isImportant() ? R.drawable.ic_favorite
                    : R.drawable.ic_favorite_border);

            title.setText(taskInfo.getTask());

            if (taskInfo.isCompleted()) {
                title.setPaintFlags(title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
            else {
                title.setPaintFlags(title.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            }

            String dueDate = taskInfo.getDueDate();
            if (dueDate != null && !dueDate.isEmpty()) {
                subTitle.setVisibility(View.VISIBLE);

                Date date = new Date();
                long h = date.getTime();
                if (taskInfo.getdDate() < h) {
                    subTitle.setTextColor(errorColor);
                }
                else {
                    subTitle.setTextColor(defaultColor);
                }

            }
            else {
                subTitle.setVisibility(View.GONE);
            }
            subTitle.setText(dueDate);

        }

    }

}
