package ru.yandex.todo.network;

import android.content.Context;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import ru.yandex.todo.model.SyncTask;
import ru.yandex.todo.model.Task;
import ru.yandex.todo.util.Constants;

public class RequestApi implements Constants {

    private final IRequestApi requestApi;

    private final PublishSubject<Long> deleteSyncTaskSubject;
    private final PublishSubject<Task> updateSyncTaskSubject;
    private final PublishSubject<List<Task>> deleteAndInsertTaskSubject;
    public Observable<Long> deleteSyncTaskEvent;
    public Observable<Task> updateSyncTaskEvent;
    public Observable<List<Task>> deleteAndInsertTaskEvent;

    public RequestApi(Context context) {

        deleteSyncTaskSubject = PublishSubject.create();
        deleteSyncTaskEvent = deleteSyncTaskSubject;

        updateSyncTaskSubject = PublishSubject.create();
        updateSyncTaskEvent = updateSyncTaskSubject;

        deleteAndInsertTaskSubject = PublishSubject.create();
        deleteAndInsertTaskEvent = deleteAndInsertTaskSubject;

        requestApi = ServiceGenerator.createService(IRequestApi.class, context);
    }

    public void getTasks() {
        try {

            requestApi.getTasks().observeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    //.retryWhen(throwableObservable -> throwableObservable.delay(1, TimeUnit.SECONDS))
                    .subscribe(new DefaultObserver<List<Task>>() {
                        @Override
                        public void onNext(@NotNull List<Task> tasks) {
                            Log.d(TAG, "onNext");
                            deleteAndInsertTaskSubject.onNext(tasks);
                        }

                        @Override
                        public void onError(@NotNull Throwable e) {
                            Log.e(TAG, "onError: " + e.getMessage());
                        }

                        @Override
                        public void onComplete() {
                            Log.d(TAG, "onComplete");
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void insertTask(Task task) {

        try {

            requestApi.insertTask(task).observeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    //.retryWhen(throwableObservable -> throwableObservable.delay(1, TimeUnit.SECONDS))
                    .subscribe(new DefaultObserver<Task>() {
                        @Override
                        public void onNext(@NotNull Task t) {
                            Log.d(TAG, "onNext");

                            if (task.getUpdatedAt() == t.getUpdatedAt()) {
                                deleteSyncTaskSubject.onNext(t.getId());
                            }
                            else if (task.getUpdatedAt() < t.getUpdatedAt()) {
                                updateSyncTaskSubject.onNext(t);
                            }
                        }

                        @Override
                        public void onError(@NotNull Throwable e) {
                            Log.e(TAG, "onError: " + e.getMessage());
                        }

                        @Override
                        public void onComplete() {
                            Log.d(TAG, "onComplete");
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void updateTask(Task task) {

        try {

            requestApi.updateTask(task.getId(), task).observeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    //.retryWhen(throwableObservable -> throwableObservable.delay(1, TimeUnit.SECONDS))
                    .subscribe(new DefaultObserver<Task>() {
                        @Override
                        public void onNext(@NotNull Task t) {
                            Log.d(TAG, "onNext");

                            if (task.getUpdatedAt() == t.getUpdatedAt()) {
                                deleteSyncTaskSubject.onNext(t.getId());
                            }
                            else if (task.getUpdatedAt() < t.getUpdatedAt()) {
                                updateSyncTaskSubject.onNext(t);
                            }
                        }

                        @Override
                        public void onError(@NotNull Throwable e) {
                            Log.e(TAG, "onError: " + e.getMessage());
                        }

                        @Override
                        public void onComplete() {
                            Log.d(TAG, "onComplete");
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void deleteTask(Task task) {

        try {

            requestApi.deleteTask(task.getId()).observeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    //.retryWhen(throwableObservable -> throwableObservable.delay(1, TimeUnit.SECONDS))
                    .subscribe(new DefaultObserver<Task>() {
                        @Override
                        public void onNext(@NotNull Task t) {
                            Log.d(TAG, "onNext");
                            deleteSyncTaskSubject.onNext(t.getId());
                        }

                        @Override
                        public void onError(@NotNull Throwable e) {
                            Log.e(TAG, "onError: " + e.getMessage());
                        }

                        @Override
                        public void onComplete() {
                            Log.d(TAG, "onComplete");
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void syncTasks(SyncTask task) {
        try {

            requestApi.syncTasks(task).observeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    //.retryWhen(throwableObservable -> throwableObservable.delay(1, TimeUnit.SECONDS))
                    .subscribe(new DefaultObserver<List<Task>>() {
                        @Override
                        public void onNext(@NotNull List<Task> tasks) {
                            Log.d(TAG, "onNext");
                            deleteAndInsertTaskSubject.onNext(tasks);
                        }

                        @Override
                        public void onError(@NotNull Throwable e) {
                            Log.e(TAG, "onError: " + e.getMessage());
                        }

                        @Override
                        public void onComplete() {
                            Log.d(TAG, "onComplete");
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
