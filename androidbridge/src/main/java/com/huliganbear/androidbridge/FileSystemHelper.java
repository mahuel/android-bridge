package com.huliganbear.androidbridge;

import android.app.Activity;

public class FileSystemHelper {

    public String getSaveFolder(Activity activity){
        return activity.getFilesDir().getAbsolutePath();
    }
}
