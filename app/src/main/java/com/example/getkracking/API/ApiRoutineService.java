package com.example.getkracking.API;

import androidx.lifecycle.LiveData;

import com.example.getkracking.API.model.PagedListModel;
import com.example.getkracking.API.model.ReviewAnswerModel;
import com.example.getkracking.API.model.ReviewModel;
import com.example.getkracking.API.model.RoutineModel;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiRoutineService {
    @POST("routines/{routineId}/ratings")
    LiveData<ApiResponse<ReviewAnswerModel>> postReview(@Path("routineId") int routineId , @Body ReviewModel reviewModel);

    @GET("routines")
    LiveData<ApiResponse<PagedListModel<RoutineModel>>> getRoutines();

    @GET("user/current/routines/favourites")
    LiveData<ApiResponse<PagedListModel<RoutineModel>>> getFavouriteRoutines();

    @GET("routines/{routineId}")
    LiveData<ApiResponse<RoutineModel>> getRoutineById(@Path("routineId") int routineId);

    @POST("user/current/routines/{routineId}/favourites")
    LiveData<ApiResponse<Void>> addToFavourites(@Path("routineId") int routineId);

    @DELETE("user/current/routines/{routineId}/favourites")
    LiveData<ApiResponse<Void>> removeFromFavourites(@Path("routineId") int routineId);
}
