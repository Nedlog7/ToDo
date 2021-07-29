package ru.yandex.todo.worker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.hilt.work.HiltWorker;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import ru.yandex.todo.R;
import ru.yandex.todo.model.repository.TaskRepository;
import ru.yandex.todo.utils.Constants;
import ru.yandex.todo.view.activity.mainActivity.MainActivity;

@HiltWorker
public class NotifyWorker extends Worker {

    private final TaskRepository taskRepository;

    @AssistedInject
    public NotifyWorker(@NonNull @Assisted Context context, @NonNull @Assisted WorkerParameters params,
                        TaskRepository taskRepository) {
        super(context, params);
        this.taskRepository = taskRepository;
    }

    @NonNull
    @Override
    public Result doWork() {

        Instant instant = Instant.now();
        ZonedDateTime startOfDay = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
                .toLocalDate().atStartOfDay(ZoneId.systemDefault());
        ZonedDateTime endOfDay = startOfDay.plusDays(1);

        int taskCount = taskRepository.getTasksForToday(startOfDay.toEpochSecond(), endOfDay.toEpochSecond());
        if (taskCount > 0)
            triggerNotification(taskCount);

        long delay = endOfDay.plusHours(9).toEpochSecond() - instant.getEpochSecond();

        OneTimeWorkRequest notificationWork = new OneTimeWorkRequest
                .Builder(NotifyWorker.class)
                .setInitialDelay(delay, TimeUnit.SECONDS)
                .addTag(Constants.NOTIFICATION_WORK)
                .build();

        WorkManager.getInstance(getApplicationContext()).enqueue(notificationWork);

        return Result.success();

    }

    private void triggerNotification(int taskCount) {

        Context context = getApplicationContext();

        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        String NOTIFICATION_CHANNEL_ID = NotifyWorker.class.getName();
        String channelName = NOTIFICATION_CHANNEL_ID + "-deadline";

        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                channelName, NotificationManager.IMPORTANCE_HIGH);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        chan.setShowBadge(false);

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(chan);

        String contentText = String.format(context.getString(R.string.NotificationContent), taskCount);

        Notification.Builder notificationBuilder = new Notification.Builder(context, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setSmallIcon(R.drawable.ic_notification)
                .setShowWhen(true)
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(contentText)
                .setStyle(new Notification.BigTextStyle().bigText(contentText))
                .setAutoCancel(true);

        notificationManager.notify(Constants.NOTIFICATION_ID, notificationBuilder.build());

    }

}
