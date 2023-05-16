package com.dev_akash.uploadfileassignment.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
    public static File createFileFromUri(Context context, Uri uri) {
        String displayName = getFileName(context,uri);

        try {
            // Creating a new file in the app's cache directory and prepare output stream
            File file = new File(context.getCacheDir(), displayName);
            OutputStream outputStream = new FileOutputStream(file);

            // Opening an input stream using the URI
            ContentResolver contentResolver = context.getContentResolver();
            InputStream inputStream = contentResolver.openInputStream(uri);

            //Reading and copying the data of the input stream to the output stream
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            //closing the streams to free resources
            outputStream.close();
            inputStream.close();

            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String getFileName(Context context,Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);

        int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        cursor.moveToFirst();

        String fileName = cursor.getString(nameIndex);
        cursor.close();
        return fileName;
    }

}
