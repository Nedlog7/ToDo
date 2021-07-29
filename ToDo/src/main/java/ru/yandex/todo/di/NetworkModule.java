package ru.yandex.todo.di;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.yandex.todo.model.api.IRequestApi;
import ru.yandex.todo.model.api.TaskTypeAdapter;
import ru.yandex.todo.model.models.Task;
import ru.yandex.todo.utils.Constants;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {

    @Provides
    @Singleton
    public static RxJava2CallAdapterFactory provideRxAdapter() {
        return RxJava2CallAdapterFactory
                .createWithScheduler(Schedulers.io());
    }

    @Provides
    @Singleton
    public static Gson provideGson() {
        return new GsonBuilder()
                .registerTypeAdapter(Task.class, new TaskTypeAdapter())
                .create();
    }

    @Provides
    @Singleton
    public static Retrofit.Builder provideRetrofitBuilder(RxJava2CallAdapterFactory rxAdapter,
                                                          Gson gson) {
        return new Retrofit.Builder()
                .baseUrl(Constants.yandexUrl)
                .addCallAdapterFactory(rxAdapter)
                .addConverterFactory(GsonConverterFactory.create(gson));
    }

    @Provides
    @Singleton
    public static OkHttpClient provideOkHttp(@ApplicationContext Context context) {

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

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(interceptor())
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .followRedirects(false)
                    .sslSocketFactory(sslContext.getSocketFactory(),
                            (X509TrustManager) tmf.getTrustManagers()[0]);

            return httpClient.build();

        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }

    @Provides
    @Singleton
    public static Retrofit provideRetrofit(Retrofit.Builder builder, OkHttpClient httpClient) {
        return builder.client(httpClient).build();
    }

    @Provides
    public static IRequestApi provideApiClient(Retrofit retrofit) {
        return retrofit.create(IRequestApi.class);
    }

    @Provides
    public static Interceptor interceptor() {
        return chain -> {
            Request.Builder builder = chain.request().newBuilder();
            builder.header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + Constants.token);

            return chain.proceed(builder.build());
        };
    }

}
