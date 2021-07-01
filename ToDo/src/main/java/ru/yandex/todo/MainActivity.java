package ru.yandex.todo;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import ru.yandex.todo.adapter.TasksRecyclerViewAdapter;
import ru.yandex.todo.database.DatabaseHelper;
import ru.yandex.todo.viewModels.TaskViewModel;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.emptyLayout, new MainFragment())
                .commit();

    }

    public static class MainFragment extends Fragment {

        private AppCompatCheckBox ivVisibility;
        private TasksRecyclerViewAdapter adapter;
        boolean isPause = false;

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                                 @Nullable Bundle savedInstanceState) {

            View view = inflater.inflate(R.layout.fragment_main, container, false);

            MainActivity activity = (MainActivity) getActivity();

            DatabaseHelper dbHelper = DatabaseHelper.getInstance(activity);
            dbHelper.loadTasks(false);

            FloatingActionButton fab = view.findViewById(R.id.fab);
            fab.setOnClickListener(v -> {
                Intent intent = new Intent(activity, TaskActivity.class);
                startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });

            RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
            ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
            recyclerView.setHasFixedSize(true);
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (dy > 0 && fab.getVisibility() == View.VISIBLE) {
                        fab.hide();
                    } else if (dy < 0 && fab.getVisibility() != View.VISIBLE) {
                        fab.show();
                    }
                }
            });

            adapter = new TasksRecyclerViewAdapter(activity, dbHelper,
                    new ArrayList<>(), new ArrayList<>());
            recyclerView.setAdapter(adapter);

            TaskViewModel taskViewModel = new ViewModelProvider(this).get(TaskViewModel.class);
            taskViewModel.getTaskLiveDataLoad().observe(getViewLifecycleOwner(), adapter::addTaskList);
            taskViewModel.getTaskLiveDataAdd().observe(getViewLifecycleOwner(), adapter::addTask);
            taskViewModel.getTaskLiveDataDelete().observe(getViewLifecycleOwner(), adapter::deleteTask);
            taskViewModel.getTaskLiveDataUpdate().observe(getViewLifecycleOwner(), adapter::updateTask);

            SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(activity) {
                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    dbHelper.deleteTask(adapter.getTask(viewHolder.getAdapterPosition()));
                }
            };

            ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
            itemTouchhelper.attachToRecyclerView(recyclerView);

            Toolbar toolbar = view.findViewById(R.id.toolbar);
            toolbar.setOnClickListener(v -> {
                if (adapter.getItemCount() > 0)
                    recyclerView.scrollToPosition(0);
            });

            ivVisibility = view.findViewById(R.id.ivVisibility);
            ivVisibility.setOnClickListener(v -> {
                if (ivVisibility.isChecked()) {
                    dbHelper.loadTasks(true);
                }
                else {
                    adapter.deleteCompletedTask();
                }
            });

            return view;

        }

        @Override
        public void onPause() {
            isPause = true;
            super.onPause();
        }

        @Override
        public void onResume() {

            super.onResume();

            if (isPause) {
                isPause = false;
                if (!ivVisibility.isChecked()) {
                    adapter.deleteCompletedTask();
                }
            }

        }

    }

}