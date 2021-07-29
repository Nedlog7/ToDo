package ru.yandex.todo.worker;

import android.content.Context;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import ru.yandex.todo.utils.Constants;

public class SyncWorkManager {

    @Inject
    public SyncWorkManager() {
    }

    public void scheduleSynchronization(Context context) {

        WorkManager instanceWorkManager = WorkManager.getInstance(context);

        if (!isWorkScheduled(instanceWorkManager)) {

            Constraints constraints = new Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build();

            PeriodicWorkRequest syncWorkRequest = new PeriodicWorkRequest
                    .Builder(SyncWorker.class, 8, TimeUnit.HOURS, 10, TimeUnit.MINUTES)
                    .setConstraints(constraints)
                    .build();

            instanceWorkManager.enqueueUniquePeriodicWork(Constants.SYNC_WORK,
                    ExistingPeriodicWorkPolicy.KEEP, syncWorkRequest);

        }

    }

    private boolean isWorkScheduled(WorkManager instanceWorkManager) {

        try {

            List<WorkInfo> workInfoList = instanceWorkManager
                    .getWorkInfosForUniqueWork(Constants.SYNC_WORK).get();
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
