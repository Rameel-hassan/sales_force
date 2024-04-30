package com.app.salesforce.utilities;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;

public class ImageSaveUtility {
    public ImageSaveUtility() {
    }

    public void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {

        File direct = new File(Environment.getExternalStorageDirectory() + "/Attendance");

        if (!direct.exists()) {
            File wallpaperDirectory = new File("/sdcard/Attendance/");
            wallpaperDirectory.mkdirs();
        }

        File file = new File("/sdcard/Attendance/", fileName);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
