package com.example.goods4u.homework3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class SettingActivity extends AppCompatActivity {
    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = this.getSharedPreferences("userSetting",Context.MODE_PRIVATE);
        setContentView(R.layout.activity_setting);
        final EditText name = (EditText) findViewById(R.id.editText_name);
        final EditText path = (EditText) findViewById(R.id.editText_path);
        name.setText(sp.getString("name","test.jpg"));
        path.setText(sp.getString("path", Environment.getExternalStorageDirectory().getAbsolutePath() + "/pic/"));
        Button buttonSave = (Button) findViewById(R.id.Save);

        buttonSave.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("name", name.getText().toString());
                editor.putString("path", path.getText().toString());
                editor.commit();
                SettingActivity.this.finish();
            }
        });
    }
}
