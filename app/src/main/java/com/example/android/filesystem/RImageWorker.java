package com.example.android.filesystem;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.greenrobot.eventbus.EventBus;

public class RImageWorker extends Worker {
    private static final String TAG = "azeem";
    public RImageWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {

            SystemClock.sleep(2000);
            Bitmap image = MainActivity.readImage(getApplicationContext());
            EventBus.getDefault().post(new EventMessage(image));
            Log.d(TAG, "doWork: ");
            return Result.success();
        }catch (Exception e){
            e.fillInStackTrace();
            return Result.failure();
        }
    }

    @Override
    public void onStopped() {
        super.onStopped();
        Log.d(TAG, "onStopped: ");
    }

}
