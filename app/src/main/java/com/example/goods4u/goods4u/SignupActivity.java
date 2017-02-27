package com.example.goods4u.goods4u;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;


public class SignupActivity extends AppCompatActivity {
    String username,password,email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Button buttonSignup = (Button) findViewById(R.id.button_signup);


        buttonSignup.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                username=((EditText)findViewById(R.id.editText_username)).getText().toString();
                password=((EditText)findViewById(R.id.editText_password)).getText().toString();
                password=encryptPBKDF2(password);
                System.out.println("password: "+password+" end");
                email=((EditText)findViewById(R.id.editText_email)).getText().toString();
                if(username.isEmpty()||password.isEmpty()||email.isEmpty())
                    Toast.makeText(SignupActivity.this,"Imcomplete Infomation",Toast.LENGTH_SHORT).show();
                else {
                    new SignUpTask().execute();
                }
            }
        });
    }
    public static String encryptPBKDF2(String msg) {
        try {
            int iterations = 500;
            char[] chars = msg.toCharArray();
            byte[] salt = "_OR:LM?DVI&#$LN:KDURLKNJDLK".getBytes();

            PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 32 * 8);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = skf.generateSecret(spec).getEncoded();
            String result=new String(Base64.encode(hash,  Base64.DEFAULT),"UTF-8");
            result=result.substring(0,result.length()-1);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public class SignUpTask extends AsyncTask<Void, Void, Boolean> {

        String info=null;
        SignUpTask(){

        }
        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username",username);
                jsonObject.put("password",password);
                jsonObject.put("email",email);
                JSONObject call_json = HttpUtil.post("http://52.24.19.99/user.php", jsonObject.toString());
                info=call_json.get("data").toString();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            Toast.makeText(SignupActivity.this,info,Toast.LENGTH_SHORT).show();
            if(info.equals("create successful\n"))
                SignupActivity.this.finish();
        }

    }
}
