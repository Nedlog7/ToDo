package ru.yandex.todo.network;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.yandex.todo.model.Task;
import ru.yandex.todo.util.Constants;

public class ServiceGenerator implements Constants {

    private static RxJava2CallAdapterFactory rxAdapter = RxJava2CallAdapterFactory
            .createWithScheduler(Schedulers.io());

    private static Gson gson = new GsonBuilder()
            .registerTypeAdapter(Task.class, new TaskTypeAdapter())
            .create();

    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(yandexUrl)
            .addCallAdapterFactory(rxAdapter)
            .addConverterFactory(GsonConverterFactory.create(gson));

    private static Retrofit retrofit;

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    public static <S> S createService(Class<S> serviceClass, Context context) {

        if (retrofit == null) {

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

                httpClient.addInterceptor(interceptor())
                        .readTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .followRedirects(false)
                        .sslSocketFactory(sslContext.getSocketFactory(),
                                (X509TrustManager) tmf.getTrustManagers()[0]);

                retrofit = builder.client(httpClient.build())
                        .build();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return retrofit.create(serviceClass);

    }

    private static Interceptor interceptor() {
        return chain -> {
            Request.Builder builder = chain.request().newBuilder();
            builder.header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token);

            return chain.proceed(builder.build());
        };
    }

}
