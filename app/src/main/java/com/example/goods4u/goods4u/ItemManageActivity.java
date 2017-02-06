package com.example.goods4u.goods4u;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ItemManageActivity extends AppCompatActivity {
    private List<Item> items;//要显示的数据集合
    private ListView lvItems;//ListView对象
    private BaseAdapter itemAdapt;//适配器
    private TransferUtility transferUtility;
    private View contentView;
    private View mProgressView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_manage);
        transferUtility = Util.getTransferUtility(this);
        contentView=findViewById(R.id.content_item_manage);
        mProgressView=findViewById(R.id.item_manage_progress);
        init();//初始化要显示的数据集合、ListView对象、以及适配器
        setListener();//设置按item事件
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }
    private void setListener() {
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
            //    Toast.makeText(MainActivity.this, generals.get(position).getName()+":被短按 ", 50000).show();
            }
        });
        ImageButton add=(ImageButton)findViewById(R.id.Button_Add);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ItemManageActivity.this,ItemAddActivity.class);
                startActivity(intent);
            }
        });
    }
    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            contentView.setVisibility(show ? View.GONE : View.VISIBLE);
            contentView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    contentView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            contentView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void init() {
        items = new ArrayList<Item>();
        showProgress(true);
        new DownloadDataTask().execute();
        lvItems = (ListView) findViewById(R.id.lvItems);
        itemAdapt = new ItemAdapter();
        lvItems.setAdapter(itemAdapt);
    }
    public class DownloadDataTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                String jsonObject =  HttpUtil.get("http://52.24.19.99/goods4u.php/items");
                if(jsonObject!=null){
                    JSONArray myJsonArray = new JSONArray(jsonObject);
                    for(int i=0 ; i < myJsonArray.length() ;i++)
                    {
                        JSONObject myjObject = myJsonArray.getJSONObject(i);
                        String Image = myjObject.getString("image");
                        Item item=new Item(Image,null);
                        item.title=myjObject.getString("title");
                        items.add(item);
                        String path=Environment.getExternalStorageDirectory()+"/pic/";
                        System.out.println(path+Image);
                        File file=new File(path);
                        if(!file.exists()){
                            file.mkdir();
                        }
                        file=new File(path+Image);
                        if(!file.exists()){
                            TransferObserver observer = transferUtility.download(Constants.BUCKET_NAME, Image, file);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            showProgress(false);
        }

        @Override
        protected void onCancelled() {
            showProgress(false);
        }
    }
    class ItemAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Item getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View layout = View.inflate(ItemManageActivity.this, R.layout.item_items, null);
            ImageView ivThumb = (ImageView) layout.findViewById(R.id.ivThumb);
            TextView tvName = (TextView) layout.findViewById(R.id.tvName);
            Item item =  items.get(position);
            Bitmap bitmap= BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/pic/"+item.image);
            ivThumb.setImageBitmap(bitmap);
            tvName.setText(item.title);
            return layout;
        }

    }
}
