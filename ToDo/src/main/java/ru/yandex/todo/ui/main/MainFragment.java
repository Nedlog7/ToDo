package ru.yandex.todo.ui.main;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.time.Instant;

import io.reactivex.disposables.Disposable;
import ru.yandex.todo.R;
import ru.yandex.todo.adapter.TaskListAdapter;
import ru.yandex.todo.model.Priority;
import ru.yandex.todo.model.Task;
import ru.yandex.todo.util.Utils;
import ru.yandex.todo.viewmodel.TasksViewModel;

public class MainFragment extends Fragment {

    private FragmentActivity activity;

    private AppCompatCheckBox ivVisibility;
    private TaskListAdapter adapter;
    private TasksViewModel tasksViewModel;

    private Disposable subscribeTaskClick = null;
    private Disposable subscribeDoneClick = null;
    private Disposable subscribePriorityClick = null;

    public MainFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_main, container, false);

        activity = requireActivity();
        tasksViewModel = new ViewModelProvider(requireActivity()).get(TasksViewModel.class);

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(v -> Navigation.findNavController(requireView())
                .navigate(MainFragmentDirections.actionMainFragmentToTaskFragment(new Task())));

        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager())
                        .findLastCompletelyVisibleItemPosition();
                if (lastVisibleItem < adapter.getItemCount() - 1 && dy > 0
                        && fab.getVisibility() == View.VISIBLE) {
                    fab.hide();
                } else if (fab.getVisibility() != View.VISIBLE) {
                    fab.show();
                }
            }
        });

        adapter = new TaskListAdapter(new TaskListAdapter.TaskDiff(), activity);
        recyclerView.setAdapter(adapter);

        tasksViewModel.getTasks().observe(getViewLifecycleOwner(), tasks -> adapter.submitList(tasks));

        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(activity) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                tasksViewModel.delete(adapter.getCurrentList().get(viewHolder.getAdapterPosition()));
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setOnClickListener(v -> {
            if (adapter.getItemCount() > 0) {
                fab.show();
                recyclerView.scrollToPosition(0);
            }
        });

        ivVisibility = view.findViewById(R.id.ivVisibility);
        ivVisibility.setOnClickListener(v -> {
            if (ivVisibility.isChecked())
                tasksViewModel.getHideDone().postValue(false);
            else {
                tasksViewModel.getHideDone().postValue(true);
            }

        });

        TextView subtitle = view.findViewById(R.id.subtitle);
        subtitle.setText(String.format(getString(R.string.CompletedTasks), 0));

        tasksViewModel.getCompletedTasks().observe(getViewLifecycleOwner(),
                tasks -> subtitle.setText(String.format(getString(R.string.CompletedTasks),
                        tasks.size())));

        setupDoneClick();
        setupPriorityClick();
        setupTaskClick();

        return view;

    }

    @Override
    public void onPause() {
        super.onPause();

        if (!ivVisibility.isChecked()) {
            tasksViewModel.getHideDone().postValue(true);
        }
    }

    private void setupDoneClick() {
        subscribeDoneClick = adapter.clickDoneEvent
                .subscribe(task -> {
                    Task clone = (Task) task.clone();
                    clone.setDone(!clone.isDone());
                    clone.setUpdatedAt(Instant.now().getEpochSecond());

                    tasksViewModel.update(clone);
                });
    }

    private void setupPriorityClick() {
        subscribePriorityClick = adapter.clickPriorityEvent
                .subscribe(task -> {
                    Task clone = (Task) task.clone();
                    clone.setPriority(clone.getPriority() == Priority.IMPORTANT
                            ? Priority.LOW : Priority.IMPORTANT);
                    clone.setUpdatedAt(Instant.now().getEpochSecond());

                    tasksViewModel.update(clone);
                });
    }

    private void setupTaskClick() {
        subscribeTaskClick = adapter.clickTaskEvent.subscribe(task ->
                Navigation.findNavController(requireView())
                        .navigate(MainFragmentDirections.actionMainFragmentToTaskFragment((Task) task.clone())));
    }

    @Override
    public void onDestroy() {

        if (subscribeDoneClick != null)
            subscribeDoneClick.dispose();

        if (subscribePriorityClick != null)
            subscribePriorityClick.dispose();

        if (subscribeTaskClick != null)
            subscribeTaskClick.dispose();

        super.onDestroy();

    }

}
