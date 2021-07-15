package ru.yandex.todo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

import ru.yandex.todo.util.Constants;
import ru.yandex.todo.worker.NotifyWorker;
import ru.yandex.todo.worker.SyncWorker;

public class MainActivity extends AppCompatActivity implements Constants {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences(TAG, Context.MODE_PRIVATE);
        if (!sharedPreferences.getBoolean(NOTIFICATION_WORK, false)) {
            scheduleNotification();
            scheduleSynchronization();

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(NOTIFICATION_WORK, true);
            editor.apply();
        }

    }

    private void scheduleNotification() {
        OneTimeWorkRequest notificationWork = new OneTimeWorkRequest
                .Builder(NotifyWorker.class).build();

        WorkManager instanceWorkManager = WorkManager.getInstance(this);
        instanceWorkManager.enqueueUniqueWork(NOTIFICATION_WORK, ExistingWorkPolicy.KEEP, notificationWork);
    }

    private void scheduleSynchronization() {
        PeriodicWorkRequest syncWorkRequest = new PeriodicWorkRequest
                .Builder(SyncWorker.class, 8, TimeUnit.HOURS, 470, TimeUnit.MINUTES)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(SYNC_WORK, ExistingPeriodicWorkPolicy.KEEP, syncWorkRequest);
    }

}