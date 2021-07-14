package ru.yandex.todo.worker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.TimeUnit;

import ru.yandex.todo.MainActivity;
import ru.yandex.todo.R;
import ru.yandex.todo.db.TaskRepository;
import ru.yandex.todo.util.Constants;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotifyWorker extends Worker implements Constants {

    public NotifyWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        Instant instant = Instant.now();
        ZonedDateTime startOfDay = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault())
                .toLocalDate().atStartOfDay(ZoneId.systemDefault());
        ZonedDateTime endOfDay = startOfDay.plusDays(1);

        TaskRepository taskRepository = new TaskRepository(getApplicationContext());
        int taskCount = taskRepository.getTasksForToday(startOfDay.toEpochSecond(), endOfDay.toEpochSecond());
        if (taskCount > 0)
            triggerNotification(taskCount);

        long delay = endOfDay.plusHours(9).toEpochSecond() - instant.getEpochSecond();

        OneTimeWorkRequest notificationWork = new OneTimeWorkRequest
                .Builder(NotifyWorker.class)
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag(NOTIFICATION_WORK)
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
                context.getSystemService(NOTIFICATION_SERVICE);
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

        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());

    }

}
