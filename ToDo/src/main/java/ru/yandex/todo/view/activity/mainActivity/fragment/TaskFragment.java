package ru.yandex.todo.view.activity.mainActivity.fragment;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.Calendar;
import java.util.TimeZone;

import dagger.hilt.android.AndroidEntryPoint;
import ru.yandex.todo.R;
import ru.yandex.todo.model.models.Priority;
import ru.yandex.todo.model.models.Task;
import ru.yandex.todo.utils.Utils;
import ru.yandex.todo.viewmodel.TasksViewModel;

@AndroidEntryPoint
public class TaskFragment extends Fragment {

    private Task task;
    private TextInputEditText etTask;
    private TasksViewModel tasksViewModel;

    public TaskFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_task, container, false);

        tasksViewModel = new ViewModelProvider(requireActivity()).get(TasksViewModel.class);

        MaterialToolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_clear_task);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_clear_task);
            actionBar.setTitle("");
        }

        LinearLayout deleteTaskLayout = view.findViewById(R.id.deleteTaskLayout);
        etTask = view.findViewById(R.id.etTask);

        LinearLayout completedLayout = view.findViewById(R.id.completedLayout);
        ImageView ivCompleted = view.findViewById(R.id.ivCompleted);

        LinearLayout importantLayout = view.findViewById(R.id.importantLayout);
        ImageView ivImportant = view.findViewById(R.id.ivImportant);

        LinearLayout dueDateLayout = view.findViewById(R.id.dueDateLayout);
        ImageView ivDueDate = view.findViewById(R.id.ivDueDate);
        TextView tvDueDate = view.findViewById(R.id.tvDueDate);
        ImageView ivDeleteDueDate = view.findViewById(R.id.ivDeleteDueDate);

        int defaultColor = tvDueDate.getCurrentTextColor();

        task = TaskFragmentArgs.fromBundle(requireArguments()).getTask();
        if (task.getText() != null) {

            deleteTaskLayout.setVisibility(View.VISIBLE);
            deleteTaskLayout.setOnClickListener(v -> {

                tasksViewModel.onDeleteTaskClick(task);
                Navigation.findNavController(requireView()).popBackStack();

            });

            etTask.setText(task.getText());

            if (task.getPriority() == Priority.IMPORTANT) {
                ivImportant.setImageResource(R.drawable.ic_favorite);
            }

            if (task.isDone()) {
                etTask.setPaintFlags(etTask.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                ivCompleted.setImageResource(R.drawable.ic_check_circle);
            }

            if (task.getDeadline() != Long.MAX_VALUE) {

                setDate(tvDueDate, ivDueDate,
                        ivDeleteDueDate, task.getDeadline());

            }

        }

        importantLayout.setOnClickListener(v -> {

            if (task.getPriority() == Priority.IMPORTANT) {
                task.setPriority(Priority.LOW);
                ivImportant.setImageResource(R.drawable.ic_favorite_border);
            }
            else {
                task.setPriority(Priority.IMPORTANT);
                ivImportant.setImageResource(R.drawable.ic_favorite);
            }

        });

        completedLayout.setOnClickListener(v -> {

            if (task.isDone()) {
                task.setDone(false);
                etTask.setPaintFlags(etTask.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                ivCompleted.setImageResource(R.drawable.ic_uncheck_circle);
            }
            else {
                task.setDone(true);
                etTask.setPaintFlags(etTask.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                ivCompleted.setImageResource(R.drawable.ic_check_circle);
            }

        });

        dueDateLayout.setOnClickListener(v -> {

            long date = task.getDeadline() == Long.MAX_VALUE ? MaterialDatePicker.todayInUtcMilliseconds()
                    : Instant.ofEpochSecond(task.getDeadline()).toEpochMilli();

            MaterialDatePicker.Builder<Long> builder = setupDateSelectorBuilder(date);
            CalendarConstraints.Builder constraintsBuilder = setupConstraintsBuilder(date);

            builder.setCalendarConstraints(constraintsBuilder.build());
            MaterialDatePicker<Long> picker = builder.build();
            addTextViewListeners(picker, tvDueDate, ivDueDate, ivDeleteDueDate);
            picker.show(getChildFragmentManager(), picker.toString());

        });

        ivDeleteDueDate.setOnClickListener(v -> {

            tvDueDate.setText(R.string.TaskAddDueDate);
            tvDueDate.setTextColor(defaultColor);
            ivDeleteDueDate.setVisibility(View.GONE);
            ivDueDate.getDrawable().setTint(requireActivity().getColor(R.color.disable));

            task.setDeadline(Long.MAX_VALUE);

        });

        return view;

    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.task_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull @NotNull MenuItem item) {

        if (item.getItemId() == R.id.task_menu) {

            closeKeyboard();

            task.setText(String.valueOf(etTask.getText()));

            if (task.getText() != null && !task.getText().isEmpty()) {

                long currentTime = Instant.now().getEpochSecond();
                boolean isNewTask = false;

                task.setUpdatedAt(currentTime);
                if (task.getCreatedAt() == 0) {
                    task.setCreatedAt(currentTime);
                    task.setId(currentTime);

                    isNewTask = true;
                }

                tasksViewModel.onSaveTaskClick(task, isNewTask);

                Navigation.findNavController(requireView()).popBackStack();
                return true;

            }
            else {
                etTask.setError(getString(R.string.SaveTaskError));
            }

        }
        if (item.getItemId() == android.R.id.home) {

            Navigation.findNavController(requireView()).popBackStack();
            return true;

        }

        return super.onOptionsItemSelected(item);

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
                                      ImageView imageViewClear) {

        materialCalendarPicker.addOnPositiveButtonClickListener(selection -> {

            String date = materialCalendarPicker.getHeaderText();
            textView.setText(String.format(getString(R.string.TaskDueDate),
                    date));
            task.setDeadline(selection / 1000);

            TypedValue typedValue = new TypedValue();
            requireActivity().getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
            int color  = typedValue.data;
            textView.setTextColor(color);

            imageView.getDrawable().setTint(color);
            imageViewClear.setVisibility(View.VISIBLE);

        });

    }

    private void setDate(TextView textView, ImageView imageView,
                         ImageView imageViewClear, long deadline) {

        textView.setText(String.format(getString(R.string.TaskDueDate), Utils.formatDate(deadline)));

        int resid = R.attr.colorPrimary;
        if (deadline < Instant.now().getEpochSecond()) {
            resid = R.attr.colorError;
        }

        TypedValue typedValue = new TypedValue();
        requireActivity().getTheme().resolveAttribute(resid, typedValue, true);
        int color = typedValue.data;
        textView.setTextColor(color);

        imageView.getDrawable().setTint(color);
        imageViewClear.setVisibility(View.VISIBLE);

    }

    private void closeKeyboard() {

        if (getContext() != null) {

            InputMethodManager imm = (InputMethodManager) getContext().
                    getSystemService(Context.INPUT_METHOD_SERVICE);

            if (imm != null) {
                imm.hideSoftInputFromWindow(etTask.getWindowToken(), 0);
            } // if

        } // if

    }

}