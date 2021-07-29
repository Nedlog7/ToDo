package ru.yandex.todo.view.activity.mainActivity.fragment;

import android.os.Bundle;
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

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import ru.yandex.todo.R;
import ru.yandex.todo.model.models.Task;
import ru.yandex.todo.view.adapter.TaskListAdapter;
import ru.yandex.todo.viewmodel.TasksViewModel;

@AndroidEntryPoint
public class MainFragment extends Fragment {

    private FragmentActivity activity;

    private AppCompatCheckBox ivVisibility;
    private TaskListAdapter adapter;
    private TasksViewModel tasksViewModel;
    private FloatingActionButton fab;

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

        fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(v -> Navigation.findNavController(requireView())
                .navigate(MainFragmentDirections.actionMainFragmentToTaskFragment(new Task())));

        PublishSubject<Task> clickTaskSubject = PublishSubject.create();
        PublishSubject<Task> clickDoneSubject = PublishSubject.create();
        PublishSubject<Task> clickPrioritySubject = PublishSubject.create();

        RecyclerView recyclerView = view.findViewById(R.id.recyclerview);
        initRecyclerView(recyclerView, clickTaskSubject, clickDoneSubject, clickPrioritySubject);
        initSwipeClick(recyclerView);
        
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setOnClickListener(v -> {
            if (adapter.getItemCount() > 0) {
                fab.show();
                recyclerView.scrollToPosition(0);
            }
        });

        ivVisibility = view.findViewById(R.id.ivVisibility);
        ivVisibility.setOnClickListener(v -> tasksViewModel.onHideDoneClick(!ivVisibility.isChecked()));

        TextView subtitle = view.findViewById(R.id.subtitle);
        subtitle.setText(String.format(getString(R.string.CompletedTasks), 0));

        tasksViewModel.getTasks().observe(getViewLifecycleOwner(), tasks -> adapter.submitList(tasks));

        tasksViewModel.getCompletedTasks().observe(getViewLifecycleOwner(),
                tasks -> subtitle.setText(String.format(getString(R.string.CompletedTasks),
                        tasks.size())));

        setupDoneClick(clickDoneSubject);
        setupPriorityClick(clickPrioritySubject);
        setupTaskClick(clickTaskSubject);

        return view;

    }

    private void initRecyclerView(RecyclerView recyclerView,
                                  PublishSubject<Task> clickTaskSubject,
                                  PublishSubject<Task> clickDoneSubject,
                                  PublishSubject<Task> clickPrioritySubject) {

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

        adapter = new TaskListAdapter(new TaskListAdapter.TaskDiff(), activity,
                clickTaskSubject, clickDoneSubject, clickPrioritySubject);
        recyclerView.setAdapter(adapter);

    }

    private void initSwipeClick(RecyclerView recyclerView) {

        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(activity) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                tasksViewModel.onDeleteTaskClick(adapter.getCurrentList().get(viewHolder.getAdapterPosition()));
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(recyclerView);

    }

    private void setupDoneClick(PublishSubject<Task> clickDoneSubject) {
        subscribeDoneClick = clickDoneSubject
                .subscribe(task -> tasksViewModel.onDoneClick(task));
    }

    private void setupPriorityClick(PublishSubject<Task> clickPrioritySubject) {
        subscribePriorityClick = clickPrioritySubject
                .subscribe(task -> tasksViewModel.onPriorityClick(task));
    }

    private void setupTaskClick(PublishSubject<Task> clickTaskSubject) {
        subscribeTaskClick = clickTaskSubject.subscribe(task ->
                Navigation.findNavController(requireView())
                        .navigate(MainFragmentDirections.actionMainFragmentToTaskFragment((Task) task.clone())));
    }

    @Override
    public void onPause() {
        super.onPause();

        if (!ivVisibility.isChecked()) {
            tasksViewModel.onHideDoneClick(true);
        }
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
