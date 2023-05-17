package com.dev_akash.uploadfileassignment.network;

import static com.dev_akash.uploadfileassignment.utils.Constants.BEARER_TOKEN;

import java.io.IOException;

import javax.inject.Inject;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    @Inject
    AuthInterceptor() {
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request.Builder req = chain.request().newBuilder();
        req.addHeader("Authorization", BEARER_TOKEN);
        return chain.proceed(req.build());
    }
}