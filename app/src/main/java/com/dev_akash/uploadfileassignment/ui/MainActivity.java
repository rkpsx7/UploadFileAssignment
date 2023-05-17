package com.dev_akash.uploadfileassignment.ui;

import static com.dev_akash.uploadfileassignment.utils.Constants.ACCEPTED_MIME_TYPES;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.dev_akash.uploadfileassignment.viewmodel.MainViewModel;
import com.dev_akash.uploadfileassignment.R;
import com.dev_akash.uploadfileassignment.databinding.ActivityMainBinding;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_FILE_CODE = 707;
    private ActivityMainBinding binding;
    MainViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        binding.btnChoose.setOnClickListener(view -> launchFilePicker());

        addObserver();

    }

    private void addObserver() {
        viewModel.linkDetailLiveData.observe(this, responseObj -> {
            if (responseObj != null) {
                showToast(getString(R.string.upload_success_msg));
                setDownloadLink(responseObj.url);
            } else showToast(getString(R.string.something_went_wrong_msg));
        });
    }

    private void launchFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, ACCEPTED_MIME_TYPES);// declaring required file types like .pdf, .docx or .xlsx
        startActivityForResult(intent, REQUEST_FILE_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FILE_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    showToast(getString(R.string.starting_file_upload));
                    viewModel.uploadFile(uri);
                }
            }
        }
    }

    private void setDownloadLink(String sharedLinkUrl) {
        binding.tvLink.setText(sharedLinkUrl);
        binding.tvLink.setTextColor(getColor(R.color.blue));
        binding.tvLink.setOnClickListener(view -> {
            shareLink(sharedLinkUrl);
        });
    }

    /**
     * firing an Intent to get supported apps to share link
     * For Ex. WhatsApp, Email Client etc.
     */
    private void shareLink(String link) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, link);
        startActivity(Intent.createChooser(shareIntent, "Share Link"));
    }


    private void showToast(String msg) {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
    }
}