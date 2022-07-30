package com.example.android.filesystem;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class WImageWorker extends Worker {
    public WImageWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            MainActivity.writeImage(getApplicationContext());
            return Result.success();
        }catch (Exception e){
            e.fillInStackTrace();
            return Result.failure();
        }
    }
}
