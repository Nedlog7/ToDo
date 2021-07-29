package ru.yandex.todo.worker;

import android.content.Context;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import ru.yandex.todo.utils.Constants;

public class NotifyWorkManager {

    @Inject
    public NotifyWorkManager() {
    }

    public void scheduleNotification(Context context) {

        WorkManager instanceWorkManager = WorkManager.getInstance(context);

        if (!isWorkScheduled(instanceWorkManager)) {

            OneTimeWorkRequest notificationWork = new OneTimeWorkRequest
                    .Builder(NotifyWorker.class)
                    //.setInitialDelay(5, TimeUnit.MINUTES)
                    .addTag(Constants.NOTIFICATION_WORK)
                    .build();

            instanceWorkManager.enqueue(notificationWork);

        }

    }

    private boolean isWorkScheduled(WorkManager instanceWorkManager) {

        try {

            List<WorkInfo> workInfoList = instanceWorkManager
                    .getWorkInfosByTag(Constants.NOTIFICATION_WORK).get();
            for (WorkInfo workInfo : workInfoList) {
                WorkInfo.State state = workInfo.getState();
                if (state == WorkInfo.State.RUNNING || state == WorkInfo.State.ENQUEUED)
                    return true;
            }

        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return false;

    }

}
