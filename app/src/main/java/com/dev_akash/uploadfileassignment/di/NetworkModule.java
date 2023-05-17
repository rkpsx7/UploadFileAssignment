package com.dev_akash.uploadfileassignment.di;

import static com.dev_akash.uploadfileassignment.utils.Constants.BASE_URL;

import com.dev_akash.uploadfileassignment.network.UploadApiService;
import com.dev_akash.uploadfileassignment.network.AuthInterceptor;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {

    @Singleton
    @Provides
    UploadApiService providesUploadServiceApi(Retrofit retrofit) {
        return retrofit.create(UploadApiService.class);
    }

    @Singleton
    @Provides
    Retrofit providesRetrofit(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    @Singleton
    @Provides
    OkHttpClient providesOkhttp(AuthInterceptor authInterceptor){
        return new OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .build();
    }
}
