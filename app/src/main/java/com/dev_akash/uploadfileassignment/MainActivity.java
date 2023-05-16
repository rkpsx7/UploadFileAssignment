package com.dev_akash.uploadfileassignment;

import static com.dev_akash.uploadfileassignment.utils.Constants.*;
import static com.dev_akash.uploadfileassignment.utils.FileUtils.createFileFromUri;
import static com.dev_akash.uploadfileassignment.utils.FileUtils.getFileName;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.dev_akash.uploadfileassignment.databinding.ActivityMainBinding;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.WriteMode;
import com.dropbox.core.v2.sharing.CreateSharedLinkWithSettingsErrorException;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;
import com.dropbox.core.v2.sharing.SharedLinkSettings;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private DbxClientV2 dropboxClient;

    private static final int REQUEST_FILE_CODE = 707;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        initDbxClient();

        binding.btnChoose.setOnClickListener(view -> launchFilePicker());
    }

    private void initDbxClient() {
        DbxRequestConfig config = DbxRequestConfig.newBuilder(DBx_APP_NAME).build();
        dropboxClient = new DbxClientV2(config, getString(R.string.key_for_access));
    }

    private void launchFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_MIME_TYPES, ACCEPTED_MIME_TYPES);
        startActivityForResult(intent, REQUEST_FILE_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FILE_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                if (uri != null) {
                    uploadFile(uri);
                }
            }
        }
    }

    private void uploadFile(Uri uri) {
        new Thread(() -> {
            showToast(getString(R.string.starting_file_upload));
            String fileName = getFileName(MainActivity.this, uri);
            String destinationPath = DBx_FOLDER_PATH + fileName;

            File file = createFileFromUri(MainActivity.this, uri);

            try (InputStream inputStream = new FileInputStream(file)) {
                dropboxClient.files().uploadBuilder(destinationPath).withMode(WriteMode.OVERWRITE).uploadAndFinish(inputStream);

                showToast(getString(R.string.upload_success_msg));
                createSharedLink(destinationPath);
            } catch (Exception e) {
                showToast(getString(R.string.something_went_wrong_msg));
                Log.d("rkpsx7@FileUpload", e.toString());
                e.printStackTrace();
            }
        }).start();
    }

    private void createSharedLink(String destinationPath) {
        try {
            SharedLinkMetadata sharedLinkMetadata = dropboxClient.sharing().createSharedLinkWithSettings(destinationPath, SharedLinkSettings.newBuilder().build());

            String sharedLinkUrl = sharedLinkMetadata.getUrl();
            showToast(getString(R.string.link_generated));
            runOnUiThread(() -> setDownloadLink(sharedLinkUrl));

        } catch (CreateSharedLinkWithSettingsErrorException e) {
            if (e.errorValue.isSharedLinkAlreadyExists()) {
                SharedLinkMetadata existingSharedLink = e.errorValue.getSharedLinkAlreadyExistsValue().getMetadataValue();
                String existingSharedLinkUrl = existingSharedLink.getUrl();
                showToast(getString(R.string.link_retrieved_successfully));
                runOnUiThread(() -> setDownloadLink(existingSharedLinkUrl));
            }
        } catch (Exception e) {
        }


    }

    private void setDownloadLink(String sharedLinkUrl) {
        binding.tvLink.setText(sharedLinkUrl);
        binding.tvLink.setTextColor(getColor(R.color.blue));
        binding.tvLink.setOnClickListener(view -> {
            shareLink(sharedLinkUrl);
        });
    }

    private void shareLink(String link) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, link);
        startActivity(Intent.createChooser(shareIntent, "Share Link"));
    }


    private void showToast(String msg) {
        runOnUiThread(() -> Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show());
    }
}