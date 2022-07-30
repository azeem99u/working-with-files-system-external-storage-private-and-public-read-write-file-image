package com.example.android.filesystem;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class WPImageWorker extends Worker {
    public WPImageWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            MainActivity.writePublicImage(getApplicationContext());
            return Result.success();
        }catch (Exception e){
            e.fillInStackTrace();
            return Result.failure();
        }
    }
}
