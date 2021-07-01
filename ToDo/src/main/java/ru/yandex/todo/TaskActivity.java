package ru.yandex.todo;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import ru.yandex.todo.database.DatabaseHelper;
import ru.yandex.todo.models.TaskInfo;
import ru.yandex.todo.utils.Constants;

public class TaskActivity extends AppCompatActivity implements Constants {

    private TaskInfo taskInfo;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_clear_task);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_clear_task);
        }

        final Intent intent = getIntent();
        final Bundle bundle = intent.getExtras();
        taskInfo = bundle != null ? bundle.getParcelable(INTENT_EXTRA_IN_TASK_MODEL)
                : new TaskInfo();

        dbHelper = DatabaseHelper.getInstance(this);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.emptyLayout, new TaskFragment())
                .commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.task_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

            return true;
        } // switch
        else if (item.getItemId() == R.id.task_menu) {

            TextInputEditText etTask = findViewById(R.id.etTask);
            taskInfo.setTask(String.valueOf(etTask.getText()));

            Date date = new Date();
            if (taskInfo.getTask() != null && !taskInfo.getTask().isEmpty()) {
                if (taskInfo.getCreateDate() == null) {
                    taskInfo.setCreateDate(String.valueOf(date.getTime()));
                    dbHelper.addTask(taskInfo);
                }
                else {
                    dbHelper.updateTask(taskInfo);
                }

                setResult(RESULT_CANCELED);
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

                return true;

            }
            else {
                etTask.setError(getString(R.string.SaveTaskError));
            }

        }

        return super.onOptionsItemSelected(item);

    }

    public static class TaskFragment extends Fragment {

        private TaskActivity activity;

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.fragment_task, container, false);

            activity = (TaskActivity) getActivity();

            LinearLayout deleteTaskLayout = view.findViewById(R.id.deleteTaskLayout);
            TextInputEditText etTask = view.findViewById(R.id.etTask);

            LinearLayout completedLayout = view.findViewById(R.id.completedLayout);
            ImageView ivCompleted = view.findViewById(R.id.ivCompleted);

            LinearLayout importantLayout = view.findViewById(R.id.importantLayout);
            ImageView ivImportant = view.findViewById(R.id.ivImportant);

            LinearLayout dueDateLayout = view.findViewById(R.id.dueDateLayout);
            ImageView ivDueDate = view.findViewById(R.id.ivDueDate);
            TextView tvDueDate = view.findViewById(R.id.tvDueDate);
            ImageView ivDeleteDueDate = view.findViewById(R.id.ivDeleteDueDate);

            LinearLayout notificationLayout = view.findViewById(R.id.notificationLayout);
            ImageView ivNotification = view.findViewById(R.id.ivNotification);
            TextView tvNotification = view.findViewById(R.id.tvNotification);
            ImageView ivDeleteNotification = view.findViewById(R.id.ivDeleteNotification);

            int defaultColor = tvDueDate.getCurrentTextColor();

            if (activity.taskInfo.getTask() != null) {

                deleteTaskLayout.setVisibility(View.VISIBLE);
                deleteTaskLayout.setOnClickListener(v -> {

                    activity.dbHelper.deleteTask(activity.taskInfo);

                    activity.setResult(RESULT_CANCELED);
                    activity.finish();
                    activity.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

                });

                etTask.setText(activity.taskInfo.getTask());

                if (activity.taskInfo.isImportant()) {
                    ivImportant.setImageResource(R.drawable.ic_favorite);
                }

                if (activity.taskInfo.isCompleted()) {
                    etTask.setPaintFlags(etTask.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    ivCompleted.setImageResource(R.drawable.ic_check_circle);
                }

                if (activity.taskInfo.getrDate() != 0) {

                    setDate(tvNotification, ivNotification,
                            ivDeleteNotification, true, activity.taskInfo.getRemindDate(), 0);

                }

                if (activity.taskInfo.getdDate() != 0) {

                    setDate(tvDueDate, ivDueDate,
                            ivDeleteDueDate, false, activity.taskInfo.getDueDate(), activity.taskInfo.getdDate());

                }

            }

            importantLayout.setOnClickListener(v -> {

                if (activity.taskInfo.isImportant()) {
                    activity.taskInfo.setImportant(false);
                    ivImportant.setImageResource(R.drawable.ic_favorite_border);
                }
                else {
                    activity.taskInfo.setImportant(true);
                    ivImportant.setImageResource(R.drawable.ic_favorite);
                }

            });

            completedLayout.setOnClickListener(v -> {

                if (activity.taskInfo.isCompleted()) {
                    activity.taskInfo.setCompleted(false);
                    etTask.setPaintFlags(etTask.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    ivCompleted.setImageResource(R.drawable.ic_uncheck_circle);
                }
                else {
                    activity.taskInfo.setCompleted(true);
                    etTask.setPaintFlags(etTask.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    ivCompleted.setImageResource(R.drawable.ic_check_circle);
                }

            });

            dueDateLayout.setOnClickListener(v -> {

                long date = activity.taskInfo.getdDate() == 0 ? MaterialDatePicker.todayInUtcMilliseconds()
                        : activity.taskInfo.getdDate();

                MaterialDatePicker.Builder<Long> builder = setupDateSelectorBuilder(date);
                CalendarConstraints.Builder constraintsBuilder = setupConstraintsBuilder(date);

                builder.setCalendarConstraints(constraintsBuilder.build());
                MaterialDatePicker<Long> picker = builder.build();
                addTextViewListeners(picker, tvDueDate, ivDueDate, ivDeleteDueDate, false);
                picker.show(getChildFragmentManager(), picker.toString());

            });

            ivDeleteDueDate.setOnClickListener(v -> {

                tvDueDate.setText(R.string.TaskAddDueDate);
                tvDueDate.setTextColor(defaultColor);
                ivDeleteDueDate.setVisibility(View.GONE);
                ivDueDate.getDrawable().setTint(activity.getColor(R.color.disable));

                activity.taskInfo.setDueDate("");
                activity.taskInfo.setdDate(0);

            });

            notificationLayout.setOnClickListener(v -> {

                long date = activity.taskInfo.getrDate() == 0 ? MaterialDatePicker.todayInUtcMilliseconds()
                        : activity.taskInfo.getrDate();

                MaterialDatePicker.Builder<Long> builder = setupDateSelectorBuilder(date);
                CalendarConstraints.Builder constraintsBuilder = setupConstraintsBuilder(date);

                builder.setCalendarConstraints(constraintsBuilder.build());
                MaterialDatePicker<Long> picker = builder.build();
                addTextViewListeners(picker, tvNotification, ivNotification, ivDeleteNotification, true);
                picker.show(getChildFragmentManager(), picker.toString());

            });

            ivDeleteNotification.setOnClickListener(v -> {

                tvNotification.setText(R.string.TaskRemind);
                tvNotification.setTextColor(defaultColor);
                ivDeleteNotification.setVisibility(View.GONE);
                ivNotification.getDrawable().setTint(activity.getColor(R.color.disable));

                activity.taskInfo.setRemindDate("");
                activity.taskInfo.setrDate(0);

            });

            return view;

        }

        private MaterialDatePicker.Builder<Long> setupDateSelectorBuilder(long today) {

            MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
            builder.setSelection(today);
            builder.setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR);

            return builder;

        }

        private CalendarConstraints.Builder setupConstraintsBuilder(long today) {

            Calendar calendar = getClearedUtc();
            calendar.setTimeInMillis(today);
            calendar.roll(Calendar.YEAR, 1);

            CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
            constraintsBuilder.setStart(MaterialDatePicker.todayInUtcMilliseconds());
            constraintsBuilder.setEnd(calendar.getTimeInMillis());
            constraintsBuilder.setOpenAt(today);
            constraintsBuilder.setValidator(DateValidatorPointForward.now());

            return constraintsBuilder;

        }

        private Calendar getClearedUtc() {

            Calendar utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            utc.clear();

            return utc;

        }

        private void addTextViewListeners(MaterialDatePicker<Long> materialCalendarPicker,
                                          TextView textView, ImageView imageView,
                                          ImageView imageViewClear, boolean isNotification) {

            materialCalendarPicker.addOnPositiveButtonClickListener(selection -> {

                String date = materialCalendarPicker.getHeaderText();
                if (isNotification) {

                    textView.setText(String.format(getString(R.string.TaskRemindMeAt),
                            date));
                    activity.taskInfo.setRemindDate(date);
                    activity.taskInfo.setrDate(selection);

                }
                else {
                    textView.setText(String.format(getString(R.string.TaskDueDate),
                            date));
                    activity.taskInfo.setDueDate(date);
                    activity.taskInfo.setdDate(selection);

                }

                TypedValue typedValue = new TypedValue();
                activity.getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
                int color  = typedValue.data;
                textView.setTextColor(color);

                imageView.getDrawable().setTint(color);
                imageViewClear.setVisibility(View.VISIBLE);

            });

        }

        private void setDate(TextView textView, ImageView imageView,
                             ImageView imageViewClear, boolean isNotification, String date, long dDate) {

            textView.setText(String.format(getString(isNotification ? R.string.TaskRemindMeAt
                    : R.string.TaskDueDate), date));

            int resid = R.attr.colorPrimary;
            if (!isNotification) {
                Date currentDate = new Date();
                if (dDate < currentDate.getTime()) {
                    resid = R.attr.colorError;
                }
            }

            TypedValue typedValue = new TypedValue();
            activity.getTheme().resolveAttribute(resid, typedValue, true);
            int color = typedValue.data;
            textView.setTextColor(color);

            imageView.getDrawable().setTint(color);
            imageViewClear.setVisibility(View.VISIBLE);

        }

    }

}