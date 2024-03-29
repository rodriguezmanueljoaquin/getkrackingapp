package com.example.getkracking.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import com.example.getkracking.API.ApiResponse;
import com.example.getkracking.API.ApiUserService;
import com.example.getkracking.API.model.CredentialsModel;
import com.example.getkracking.API.model.EmailModel;
import com.example.getkracking.API.model.RegisterCredentialsModel;
import com.example.getkracking.API.model.TokenModel;
import com.example.getkracking.API.model.UpdateUserModel;
import com.example.getkracking.API.model.UserModel;
import com.example.getkracking.API.model.VerificationModel;
import com.example.getkracking.entities.UserVO;
import com.example.getkracking.room.AppDatabase;
import com.example.getkracking.room.entities.UserTable;
import com.example.getkracking.vo.AbsentLiveData;
import com.example.getkracking.vo.Resource;


public class UserRepository {

    private AppExecutors executors;
    private ApiUserService service;
    private AppDatabase database;

    public UserRepository(AppExecutors executors, ApiUserService service, AppDatabase database) {
        this.executors = executors;
        this.service = service;
        this.database = database;
    }

    public LiveData<Resource<UserVO>> register(String username, String email, String password) {
        return new NetworkBoundResource<UserVO, UserTable, UserModel>
                (executors, //Convierte UserTable a UserVO - llenar los campos que no vamos a usar
                        table -> {
                            return new UserVO(table.id, false, false, table.username, table.fullName, "other", table.email, table.image, "69420", 0, 1, 2);
                        },
                        //Convierte UserModel a UserTable
                        model -> {
                            return new UserTable(model.getId(), model.getUsername(), model.getAvatarUrl(), model.getFullName(), model.getEmail());
                        },
                        //Convierte UserModel a UserVO
                        model -> {
                            return new UserVO(model.getId(), false, false, model.getUsername(), model.getFullName(), "other", model.getEmail(), model.getAvatarUrl(), "69420", 0, 1, 2);
                        }) {

            @Override
            protected void saveCallResult(@NonNull UserTable entity) {
                database.userDao().insert(entity);
            }

            @Override
            protected boolean shouldFetch(@Nullable UserTable entity) {
                return true;
            }

            @Override
            protected boolean shouldPersist(@Nullable UserModel model) {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<UserTable> loadFromDb() {
                return database.userDao().getUser();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<UserModel>> createCall() {
                return service.register(new RegisterCredentialsModel
                        (username, password, username, "other", 2L, email, "0303456", "http://icons.iconseeker.com/png/fullsize/live-messenger-icon/live-messenger-green.png"));
            }
        }.asLiveData();
    }

    public LiveData<Resource<String>> login(String username, String password) {

        return new NetworkBoundResource<String, Void, TokenModel>(executors, null, null, model -> model.getToken()) {

            @Override
            protected void saveCallResult(@NonNull Void entity) {
            }

            @Override
            protected boolean shouldFetch(@Nullable Void entity) {
                return true;
            }

            @Override
            protected boolean shouldPersist(@Nullable TokenModel model) {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<Void> loadFromDb() {
                return AbsentLiveData.create();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<TokenModel>> createCall() {
                return service.login(new CredentialsModel(username, password));
            }
        }.asLiveData();
    }
    public LiveData<Resource<Void>> verifyEmail(String email , String code){

        return new NetworkBoundResource<Void, Void, Void>
                (executors, null, null, model -> model) {

            @Override
            protected void saveCallResult(@NonNull Void entity) {
            }

            @Override
            protected boolean shouldFetch(@Nullable Void entity) {
                return true;
            }

            @Override
            protected boolean shouldPersist(@Nullable Void model) {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<Void> loadFromDb() {
                return AbsentLiveData.create();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Void>> createCall() {
                return service.verifyEmail(new VerificationModel(email , code));
            }
        }.asLiveData();
    }

    public LiveData<Resource<Void>> resendVerification(String email){

        return new NetworkBoundResource<Void, Void, Void>
                (executors, null, null, model -> model) {

            @Override
            protected void saveCallResult(@NonNull Void entity) {
            }

            @Override
            protected boolean shouldFetch(@Nullable Void entity) {
                return true;
            }

            @Override
            protected boolean shouldPersist(@Nullable Void model) {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<Void> loadFromDb() {
                return AbsentLiveData.create();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Void>> createCall() {
                return service.resendEmail(new EmailModel(email));
            }
        }.asLiveData();
    }
    public LiveData<Resource<Void>> logout() {

        return new NetworkBoundResource<Void, Void, Void>(executors, null, null, model -> model) {

            @Override
            protected void saveCallResult(@NonNull Void entity) {
            }

            @Override
            protected boolean shouldFetch(@Nullable Void entity) {
                return true;
            }

            @Override
            protected boolean shouldPersist(@Nullable Void model) {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<Void> loadFromDb() {
                return AbsentLiveData.create();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<Void>> createCall() {
                return service.logout();
            }
        }.asLiveData();
    }

    public LiveData<Resource<UserVO>> getCurrent() {
        return new NetworkBoundResource<UserVO, UserTable, UserModel>(executors,
                //Convierte UserTable a UserVO - llenar los campos que no vamos a usar
                table -> {
                    return new UserVO(table.id, false, true, table.username, table.fullName, "other", table.email, table.image, "69420", 0, 1, 2);
                },
                //Convierte UserModel a UserTable
                model -> {
                    return new UserTable(model.getId(), model.getUsername(), model.getAvatarUrl(), model.getFullName(), model.getEmail());
                },
                //Convierte UserModel a UserVO
                model -> {
                    return new UserVO(model.getId(), false, true, model.getUsername(), model.getFullName(), "other", model.getEmail(), model.getAvatarUrl(), "69420", 0, 1, 2);
                }) {
            @Override
            protected void saveCallResult(@NonNull UserTable table) {
            }

            @Override
            protected boolean shouldFetch(@Nullable UserTable table) {
                return true;
            }

            @Override
            protected boolean shouldPersist(@Nullable UserModel model) {
                return true;
            }

            @NonNull
            @Override
            protected LiveData<UserTable> loadFromDb() {
                return database.userDao().getUser();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<UserModel>> createCall() {
                return service.getCurrent();
            }
        }.asLiveData();
    }

    public LiveData<Resource<UserVO>> updateUser(String fullName, String imageUrl, String username, String email, int idUser) {
        return new NetworkBoundResource<UserVO, Void, UserModel>(executors, null, null,
                model -> new UserVO(idUser, false, true, username, fullName, "other", email, imageUrl, "69420", 0, 1, 2)) {
            @Override
            protected void saveCallResult(@NonNull Void entity) {
            }

            @Override
            protected boolean shouldFetch(@Nullable Void entity) {
                return true;
            }

            @Override
            protected boolean shouldPersist(@Nullable UserModel model) {
                return false;
            }

            @NonNull
            @Override
            protected LiveData<Void> loadFromDb() {
                return AbsentLiveData.create();
            }

            @NonNull
            @Override
            protected LiveData<ApiResponse<UserModel>> createCall() {
                executors.diskIO().execute(() -> database.userDao().deleteAll());
                return service.updateCurrent(new UpdateUserModel(username, fullName, "other", 2L, email, "69420", imageUrl));
            }
        }.asLiveData();
    }
}
