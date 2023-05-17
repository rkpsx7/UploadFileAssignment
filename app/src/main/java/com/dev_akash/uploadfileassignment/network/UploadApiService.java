package com.dev_akash.uploadfileassignment.network;

import com.dev_akash.uploadfileassignment.model.BaseResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UploadApiService {

    @Multipart
    @POST("/api/v1/s3/uploadimages")
    Call<BaseResponse> uploadImage(@Part MultipartBody.Part file);
}
