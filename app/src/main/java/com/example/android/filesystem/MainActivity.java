package com.example.android.filesystem;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final String FILE_NAME = "abc.txt";
    private static final String IMAGE_NAME = "azeem.jpg";
    public static String MY_FOLDER = "myfolder";
    private static final String TAG = "Azeem";
    private final int REQUEST_PERMISSION_WRITE = 1001;

    private EditText etData;
    private TextView showData,showPublicData;
    private ImageView imageView,imageView2;
    private ProgressBar progressBar,progressBar2;
    private boolean permissionsGranted = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        etData = findViewById(R.id.et1);
        showData = findViewById(R.id.textView1);
        showPublicData = findViewById(R.id.textView2);
        imageView = findViewById(R.id.imageView);
        progressBar = findViewById(R.id.progressBar);
        imageView2 = findViewById(R.id.imageView2);
        progressBar2 = findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.INVISIBLE);
        progressBar2.setVisibility(View.INVISIBLE);


        // get main file directory
        File file = getExternalFilesDir(null);
        Log.d(TAG, "onCreate: " + file.getAbsolutePath());

        //create my file directory
        File file2 = getExternalFilesDir(MY_FOLDER);
        Log.d(TAG, "onCreate: " + file2.getAbsolutePath());

    }

    /*write external public file*/
    public void createExternalPublicFile(View view) {
        if(!permissionsGranted){
            checkPermission();
        }
        File file = new File(Environment.getExternalStoragePublicDirectory(MY_FOLDER),FILE_NAME);
        writeFile(file);
    }

    /*read external public file*/
    public void readExternalPublicFile(View view) {
        if(!permissionsGranted){
            checkPermission();
        }

        File file = new File(Environment.getExternalStoragePublicDirectory(MY_FOLDER),FILE_NAME);
        String s = readFile(file);
        showPublicData.setText(s);
    }


    /*write external private file*/
    public void createExternalPrivateFile(View view) {
        File file = new File(getExternalFilesDir(null), FILE_NAME);
        writeFile(file);
    }

    /*read external private file*/
    public void readExternalPrivateFile(View view) {
        File file = new File(getExternalFilesDir(null), FILE_NAME);
        String s = readFile(file);
        showData.setText(s);
    }


    /*write external public image*/
    public void createExternalPublicImage(View view) {
        if(!permissionsGranted){
            checkPermission();
        }
        OneTimeWorkRequest writeRequest = new OneTimeWorkRequest.Builder(WPImageWorker.class).build();
        WorkManager.getInstance(getApplicationContext()).enqueue(writeRequest);
    }
    /*read external public image*/
    public void readExternalPublicImage(View view) {
        if(!permissionsGranted){
            checkPermission();
        }
        OneTimeWorkRequest readRequest = new OneTimeWorkRequest.Builder(RPImageWorker.class).build();
        WorkManager.getInstance(getApplicationContext()).enqueue(readRequest);
        progressBar2.setVisibility(View.VISIBLE);
        imageView2.setVisibility(View.INVISIBLE);
    }


    /*write external private file*/
    public void createExternalPrivateImage(View view) {
        OneTimeWorkRequest writeRequest = new OneTimeWorkRequest.Builder(WImageWorker.class).build();
        WorkManager.getInstance(getApplicationContext()).enqueue(writeRequest);
    }

   /* read external private file*/
    public void readExternalPrivateImage(View view) {
        OneTimeWorkRequest readRequest = new OneTimeWorkRequest.Builder(RImageWorker.class).build();
        WorkManager.getInstance(getApplicationContext()).enqueue(readRequest);
        progressBar.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.INVISIBLE);
    }


    private String readFile(File file) {
        FileInputStream fileInputStream = null;
        StringBuilder stringBuilder = null;
        try {
            stringBuilder = new StringBuilder();
            fileInputStream = new FileInputStream(file);
            int read;
            while ((read = fileInputStream.read()) != -1) {
                stringBuilder.append((char) read);
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        } finally {

            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void writeFile(File file) {
        FileOutputStream outputStream = null;
        try {

            outputStream = new FileOutputStream(file);
            outputStream.write(etData.getText().toString().getBytes());
            outputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static void writeImage(Context context) {

        File file = new File(context.getExternalFilesDir(MY_FOLDER), IMAGE_NAME);
        FileOutputStream outputStream = null;
        Bitmap bitmap = getImage(context);
        try {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Bitmap readImage(Context context) {
        File file = new File(context.getExternalFilesDir(MY_FOLDER), IMAGE_NAME);
        Bitmap bitmap = null;
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(file);
            bitmap = BitmapFactory.decodeStream(fileInputStream);
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static void writePublicImage(Context context) {

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), IMAGE_NAME);
        FileOutputStream outputStream = null;
        Bitmap bitmap = getImage(context);
        try {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Bitmap readPublicImage(Context context) {
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), IMAGE_NAME);
        Bitmap bitmap = null;
        FileInputStream fileInputStream = null;

        try {
            fileInputStream = new FileInputStream(file);
            bitmap = BitmapFactory.decodeStream(fileInputStream);
            return bitmap;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(EventMessage message) {
        Bitmap image = message.getBitmap();
        progressBar.setVisibility(View.INVISIBLE);
        imageView.setImageBitmap(image);
        imageView.setVisibility(View.VISIBLE);
        Log.d(TAG, "getEventMessage: ");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getEventMessage(PEventMessage message) {
        Bitmap image = message.getBitmap();
        progressBar2.setVisibility(View.INVISIBLE);
        imageView2.setImageBitmap(image);
        imageView2.setVisibility(View.VISIBLE);
        Log.d(TAG, "getEventMessage: ");
    }

    private static Bitmap getImage(Context context) {
        Bitmap bitmap = null;
        InputStream inputStream = null;
        try {
            inputStream = context.getAssets().open("azeemunar.jpg");
            bitmap = BitmapFactory.decodeStream(inputStream);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


   /* check if external storage is available for read and write*/
    public boolean isExternalStorageWriteAble(){
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state));
    }
    /* check if external storage is available to at least read */
    public boolean isExternalStorageReadAble(){
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state)|| Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
    }

    /* Initiate request for permission */
    private boolean checkPermission(){
        if (!isExternalStorageWriteAble() || !isExternalStorageReadAble()){
            Toast.makeText(this, "This App is only works on devices with usable external storage", Toast.LENGTH_SHORT).show();
            return false;
        }
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_WRITE);
            return false;
        }else {
            return true;
        }
    }

    /* Handle permission result*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_PERMISSION_WRITE:
                if (grantResults.length>0 && grantResults[0] ==PackageManager.PERMISSION_GRANTED){
                    permissionsGranted = true;
                    Toast.makeText(this, "External storage permission granted", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "You must grant permission!", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }



}