package com.dev_akash.uploadfileassignment.viewmodel;

import static com.dev_akash.uploadfileassignment.utils.Constants.GENERIC_MIME_TYPE;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.dev_akash.uploadfileassignment.repository.MainRepo;
import com.dev_akash.uploadfileassignment.model.BaseResponse;
import com.dev_akash.uploadfileassignment.model.ResponseObj;
import com.dev_akash.uploadfileassignment.utils.FileUtils;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

@HiltViewModel
public class MainViewModel extends ViewModel {

    public static String TAG = "MainViewModelTagDebug";

    private final ExecutorService executorService = Executors.newFixedThreadPool(3);
    private final MainRepo repo;

    @Inject
    MainViewModel(MainRepo repo) {
        this.repo = repo;
    }

    private final MutableLiveData<ResponseObj> _linkDetailLiveData = new MutableLiveData<>();
    public LiveData<ResponseObj> linkDetailLiveData = _linkDetailLiveData;

    public void uploadFile(Uri uri) {
        executorService.submit(() -> {
            try {
                File file = FileUtils.createFileFromUri(uri);
                if (file != null) {
                    RequestBody requestFile = RequestBody.create(MediaType.parse(GENERIC_MIME_TYPE), file);
                    MultipartBody.Part body = MultipartBody.Part.createFormData("files[0]", file.getName(), requestFile);

                    BaseResponse res = repo.uploadFile(body);
                    if (res != null) {
                        _linkDetailLiveData.postValue(res.response.data.get(0));
                    } else {
                        _linkDetailLiveData.postValue(null);
                    }
                }
            } catch (Exception e) {
                Log.d(TAG, e.toString());
                _linkDetailLiveData.postValue(null);
            }
        });
    }

    @Override
    protected void onCleared() {
        executorService.shutdownNow();
        super.onCleared();
    }
}
