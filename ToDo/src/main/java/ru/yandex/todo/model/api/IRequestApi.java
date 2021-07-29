package ru.yandex.todo.model.api;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import ru.yandex.todo.model.models.SyncTask;
import ru.yandex.todo.model.models.Task;

public interface IRequestApi {

    @GET("tasks")
    //@Headers({"Content-Type:application/json", "Authorization:Bearer " + Constants.token})
    Observable<List<Task>> getTasks();

    @POST("tasks")
    Observable<Task> insertTask(@Body Task task);

    @PUT("tasks/{id}")
    Observable<Task> updateTask(@Path("id") long id, @Body Task task);

    @DELETE("tasks/{id}")
    Observable<Task> deleteTask(@Path("id") long id);

    @PUT("tasks")
    Observable<List<Task>> syncTasks(@Body SyncTask task);

}
