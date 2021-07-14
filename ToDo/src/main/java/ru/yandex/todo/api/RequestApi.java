package ru.yandex.todo.api;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DefaultObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.yandex.todo.model.Task;
import ru.yandex.todo.util.Constants;
import ru.yandex.todo.util.Utils;

public class RequestApi implements Constants {

    private final Context context;
    private Retrofit retrofit;

    public RequestApi(Context context) {
        this.context = context;
        initRetrofit();
    }

    private void initRetrofit() {

        try {

            InputStream caInput = context.getAssets().open("root.cer");

            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Certificate ca = cf.generateCertificate(caInput);
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);

            OkHttpClient.Builder client = new OkHttpClient.Builder()
                    .addInterceptor(provideOfflineInterceptor())
                    .addNetworkInterceptor(provideOnlineInterceptor())
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .cache(provideCache())
                    .followRedirects(false)
                    .sslSocketFactory(sslContext.getSocketFactory(),
                            (X509TrustManager) tmf.getTrustManagers()[0]);

            OkHttpClient httpClient = client.build();

            RxJava2CallAdapterFactory rxAdapter = RxJava2CallAdapterFactory
                    .createWithScheduler(Schedulers.io());

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(Task.class, new TaskTypeAdapter())
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(yandexUrl)
                    .addCallAdapterFactory(rxAdapter)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void getTasksRequest() {

        IRequestApi requestApi = retrofit.create(IRequestApi.class);
        requestApi.getTasks().observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                //.retryWhen(throwableObservable -> throwableObservable.delay(1, TimeUnit.SECONDS))
                .subscribe(new DefaultObserver<List<Task>>() {
                    @Override
                    public void onNext(@NotNull List<Task> tasks) {
                        Log.d(TAG, "onNext");
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

    }

    public void insertTask(Task task) {

        try {

            IRequestApi requestApi = retrofit.create(IRequestApi.class);
            requestApi.insertTask(task).observeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    //.retryWhen(throwableObservable -> throwableObservable.delay(1, TimeUnit.SECONDS))
                    .subscribe(new DefaultObserver<Task>() {
                        @Override
                        public void onNext(@NotNull Task createdTask) {
                            Log.d(TAG, "onNext");

                            if (!task.equals(createdTask)) {

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

            IRequestApi requestApi = retrofit.create(IRequestApi.class);
            requestApi.updateTask(task.getId(), task).observeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    //.retryWhen(throwableObservable -> throwableObservable.delay(1, TimeUnit.SECONDS))
                    .subscribe(new DefaultObserver<Task>() {
                        @Override
                        public void onNext(@NotNull Task createdTask) {
                            Log.d(TAG, "onNext");

                            if (!task.equals(createdTask)) {

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

            IRequestApi requestApi = retrofit.create(IRequestApi.class);
            requestApi.deleteTask(task.getId()).observeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    //.retryWhen(throwableObservable -> throwableObservable.delay(1, TimeUnit.SECONDS))
                    .subscribe(new DefaultObserver<Task>() {
                        @Override
                        public void onNext(@NotNull Task createdTask) {
                            Log.d(TAG, "onNext");

                            if (!task.equals(createdTask)) {

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

    Cache provideCache() {
        return new Cache(new File(context.getCacheDir(), "http-cache"), cacheSize);
    }

    private Interceptor provideOnlineInterceptor() {
        return chain -> {
            okhttp3.Response response = chain.proceed(chain.request());
            CacheControl cacheControl;
            if (Utils.isNetworkAvailable(context)) {
                cacheControl = new CacheControl.Builder().maxAge(0, TimeUnit.SECONDS).build();
            } else {
                cacheControl = new CacheControl.Builder()
                        .maxStale(7, TimeUnit.DAYS)
                        .build();
            }

            return response.newBuilder()
                    .removeHeader("Pragma")
                    .removeHeader("Cache-Control")
                    .header("Cache-Control", cacheControl.toString())
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + token)
                    .build();

        };
    }

    private Interceptor provideOfflineInterceptor() {
        return chain -> {
            Request.Builder builder = chain.request().newBuilder();

            if (!Utils.isNetworkAvailable(context)) {
                CacheControl cacheControl = new CacheControl.Builder()
                        .maxStale(7, TimeUnit.DAYS)
                        .build();

                builder.removeHeader("Pragma")
                        .removeHeader("Cache-Control")
                        .cacheControl(cacheControl);
            }

            builder.addHeader("Content-Type", "application/json")
                    .addHeader("Authorization", "Bearer " + token);

            return chain.proceed(builder.build());
        };
    }

}
