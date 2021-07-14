package ru.yandex.todo;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import ru.yandex.todo.util.Constants;
import ru.yandex.todo.worker.NotifyWorker;

public class MainActivity extends AppCompatActivity implements Constants {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences sharedPreferences = getSharedPreferences(TAG, Context.MODE_PRIVATE);
        if (!sharedPreferences.getBoolean(NOTIFICATION_WORK, false)) {
            scheduleNotification();

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

}