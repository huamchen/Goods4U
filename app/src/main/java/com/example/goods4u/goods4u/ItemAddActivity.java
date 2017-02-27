package com.example.goods4u.goods4u;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;


import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;


public class ItemAddActivity extends AppCompatActivity {

    //TextView textTargetUri;
    ImageView targetImage;
    Bitmap bitmap=null;
    private static final int CAMERA_REQUEST = 1888;
    private static final int ALBUMS_REQUEST = 1889;
    private TransferUtility transferUtility;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_add);
        transferUtility = Util.getTransferUtility(this);
        Button buttonLoadImage = (Button) findViewById(R.id.loadimage);
        Button buttonTakePhoto = (Button) findViewById(R.id.takephoto);
        Button buttonUpload = (Button) findViewById(R.id.upload);
        //textTargetUri = (TextView) findViewById(R.id.targeturi);
        targetImage = (ImageView) findViewById(R.id.targetimage);

        buttonLoadImage.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, ALBUMS_REQUEST);
            }
        });

        buttonTakePhoto.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
        });
        buttonUpload.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View arg0) {
                if(bitmap==null){
                    Toast.makeText(ItemAddActivity.this,"No Picture",Toast.LENGTH_SHORT).show();
                }
                else{
                    String path=Environment.getExternalStorageDirectory()+"/pic/";
                    final String name=Constants.username+ Calendar.getInstance().getTimeInMillis()+".jpg";
                    saveBitmapFile(bitmap, path,name);
                    File file = new File(path+name);
                    System.out.println("fileLength="+file.length());
                    if(file.length()>1024*1024){
                        Toast.makeText(ItemAddActivity.this,"Picture should smaller than 1MB",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    TransferObserver observer = transferUtility.upload(Constants.BUCKET_NAME, file.getName(), file);
                    Item item=new Item(name,null);
                    item.title=((EditText)findViewById(R.id.editText_title)).getText().toString();
                    item.category=((Spinner)findViewById(R.id.spinner_category)).getSelectedItem().toString();
                    item.description=((EditText)findViewById(R.id.editText_description)).getText().toString();
                    item.price=((EditText)findViewById(R.id.editText_price)).getText().toString();
                    new UploadDataTask(item).execute();
                }
            }
        });
    }
    public class UploadDataTask extends AsyncTask<Void, Void, Boolean> {

        public Item mItem;
        UploadDataTask(Item item){
            mItem=item;
        }
        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("owner_id",Constants.ownerId);
                jsonObject.put("image",mItem.image);
                jsonObject.put("category",mItem.category);
                if(!mItem.price.isEmpty()) jsonObject.put("price",mItem.price);
                if(!mItem.title.isEmpty()) jsonObject.put("title",mItem.title);
                if(!mItem.description.isEmpty()) jsonObject.put("description",mItem.description);
                JSONObject call_json = HttpUtil.post("http://52.24.19.99/item.php", jsonObject.toString());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            Toast.makeText(ItemAddActivity.this,"Upload success!",Toast.LENGTH_SHORT).show();
            ItemManageActivity.instance.init();
            ItemAddActivity.this.finish();
        }

    }
    public void saveBitmapFile(Bitmap bitmap,String path,String name){
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
        if (requestCode == ALBUMS_REQUEST && resultCode == RESULT_OK){
            int MY_PERMISSIONS_REQUEST_READ_CONTACTS=0;
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            }
            Uri targetUri = data.getData();
            //textTargetUri.setText(targetUri.toString());

            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
                System.out.println("picSize="+bitmap.getByteCount()+"   PicScale="+bitmap.getWidth()*bitmap.getWidth());

                if(bitmap.getByteCount()>20000000) {
                    bitmap = null;
                    Toast.makeText(ItemAddActivity.this,"Picture should smaller than 1MB",Toast.LENGTH_SHORT).show();
                    targetImage.setImageBitmap(null);
                }
                else targetImage.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            bitmap = (Bitmap) data.getExtras().get("data");
            targetImage.setImageBitmap(bitmap);
        }
    }
}
