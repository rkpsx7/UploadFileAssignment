package com.dev_akash.uploadfileassignment.repository;

import com.dev_akash.uploadfileassignment.network.UploadApiService;
import com.dev_akash.uploadfileassignment.model.BaseResponse;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import okhttp3.MultipartBody;
import retrofit2.Response;

@Singleton
public class MainRepo {

    private final UploadApiService apiService;

    @Inject
    public MainRepo(UploadApiService apiService) {
        this.apiService = apiService;
    }


    public BaseResponse uploadFile(MultipartBody.Part file) throws IOException {
        Response<BaseResponse> res = apiService.uploadImage(file).execute();

        if (res.isSuccessful()){
            return res.body();
        }else return null;
    }
}
