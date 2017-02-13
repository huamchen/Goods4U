package com.example.goods4u.homework3;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    //TextView textTargetUri;
    ImageView targetImage;
    private SharedPreferences sp;
    private static final int CAMERA_REQUEST = 1888;
    private static final int ALBUMS_REQUEST = 1889;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = this.getSharedPreferences("userSetting", Context.MODE_PRIVATE);
        setContentView(R.layout.activity_main);
        Button buttonSavePhoto = (Button) findViewById(R.id.savePhoto);
        Button buttonLoadPhoto = (Button) findViewById(R.id.loadPhoto);
        Button buttonTakePhoto = (Button) findViewById(R.id.takephoto);
        Button buttonSetting = (Button) findViewById(R.id.setting);
        //textTargetUri = (TextView) findViewById(R.id.targeturi);
        targetImage = (ImageView) findViewById(R.id.targetimage);

        buttonLoadPhoto.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, ALBUMS_REQUEST);
            }
        });

        buttonTakePhoto.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });

        buttonSavePhoto.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (targetImage.getDrawable() == null)
                    return;

                Bitmap bmp = ((BitmapDrawable) targetImage.getDrawable()).getBitmap();
                String path = sp.getString("path", Environment.getExternalStorageDirectory().getAbsolutePath() + "/pic/");
                String name = sp.getString("name","test.jpg");
                saveBitmapFile(bmp, path, name);
            }
        });

        buttonSetting.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {
                Intent intent = new Intent(MainActivity.this,SettingActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
    }
    public void saveBitmapFile(Bitmap bitmap,String path,String name){
        int MY_PERMISSIONS_REQUEST_READ_CONTACTS=0;
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

        }
        File file=new File(path);
        if(!file.exists()){
            file.mkdir();
        }
        try {
            file=new File(path+name);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap=null;
        if (requestCode == ALBUMS_REQUEST && resultCode == RESULT_OK){
            int MY_PERMISSIONS_REQUEST_READ_CONTACTS=0;
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
            Uri targetUri = data.getData();
            //textTargetUri.setText(targetUri.toString());

            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                targetImage.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            bitmap = (Bitmap) data.getExtras().get("data");
            targetImage.setImageBitmap(bitmap);
        }
    }

}
