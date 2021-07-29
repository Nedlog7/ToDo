package ru.yandex.todo.view.activity.mainActivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;
import ru.yandex.todo.R;
import ru.yandex.todo.worker.NotifyWorkManager;
import ru.yandex.todo.worker.SyncWorkManager;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    @Inject SyncWorkManager syncWorkManager;
    @Inject NotifyWorkManager notifyWorkManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        syncWorkManager.scheduleSynchronization(this);
        notifyWorkManager.scheduleNotification(this);
    }

}