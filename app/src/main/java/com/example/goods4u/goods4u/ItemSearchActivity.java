package com.example.goods4u.goods4u;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ItemSearchActivity extends Activity {
    private TransferUtility transferUtility;
    private String keyword;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_search);
        transferUtility = Util.getTransferUtility(this);
        setTitle("Search Items");
        Button buttonSerach = (Button) findViewById(R.id.button_search);


        buttonSerach.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                keyword=((EditText) findViewById(R.id.Search_name)).getText().toString();
                HomepageActivity.instance.keyword=keyword;
                HomepageActivity.instance.type="title";
                HomepageActivity.instance.setImage();
                ItemSearchActivity.this.finish();
            }
        });

    }
    public void searchByCategory(View v) {

        keyword=((Button) v).getText().toString();
        HomepageActivity.instance.keyword=keyword;
        HomepageActivity.instance.type="category";
        HomepageActivity.instance.setImage();
        ItemSearchActivity.this.finish();

    }
}
