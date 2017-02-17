package com.example.goods4u.goods4u;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class ScrollingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Intent i=getIntent();
        int number=i.getIntExtra("number",-1);
        Item item=HomepageActivity.instance.items.get(number);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(item.title);
        setSupportActionBar(toolbar);

        ((TextView)findViewById(R.id.textView_category)).setText(item.category);
        ((TextView)findViewById(R.id.textView_price)).setText(item.price);
        ((TextView)findViewById(R.id.textView_description)).setText(item.description);
    }
}
