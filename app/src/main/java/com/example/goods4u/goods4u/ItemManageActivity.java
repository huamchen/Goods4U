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
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
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
    public Item selectItem=null;
    public static ItemManageActivity instance = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instance=this;
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
                showPopupMenu(view);

                selectItem=items.get(position);
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
    private void showPopupMenu(View view) {

        PopupMenu popupMenu = new PopupMenu(this, view);

        popupMenu.getMenuInflater().inflate(R.menu.item_manage_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
                if(item.getTitle().equals("Delete")) {
                    deleteItem();
                }else if(item.getTitle().equals("Edit")) {
                    editItem();
                }
                return false;
            }
        });
        // PopupMenu关闭事件
        popupMenu.setOnDismissListener(new PopupMenu.OnDismissListener() {
            @Override
            public void onDismiss(PopupMenu menu) {
               // Toast.makeText(getApplicationContext(), "关闭PopupMenu", Toast.LENGTH_SHORT).show();
            }
        });

        popupMenu.show();
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

    public void init() {
        items = new ArrayList<Item>();
        showProgress(true);
        new DownloadDataTask().execute();
        lvItems = (ListView) findViewById(R.id.lvItems);
        itemAdapt = new ItemAdapter();
        lvItems.setAdapter(itemAdapt);
    }
    public void editItem(){
        Intent intent=new Intent(ItemManageActivity.this,ItemEditActivity.class);
        startActivity(intent);
    }

    public void deleteItem(){

        showProgress(true);
        new DeleteDataTask().execute();
    }
    public class DownloadDataTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                System.out.println("http://52.24.19.99/item.php?ownerid="+ Constants.ownerId+ "end");
                String jsonObject =  HttpUtil.get("http://52.24.19.99/item.php?ownerid="+ Constants.ownerId);
                if(jsonObject!=null){
                    JSONArray myJsonArray = new JSONArray(jsonObject);
                    for(int i=0 ; i < myJsonArray.length() ;i++)
                    {
                        JSONObject myjObject = myJsonArray.getJSONObject(i);
                        String Image = myjObject.getString("image");
                        Item item=new Item(Image,null);
                        item.title=myjObject.getString("title");
                        item.description=myjObject.getString("description");
                        item.price=myjObject.getString("price");
                        item.item_id=myjObject.getString("item_id");
                        item.category=myjObject.getString("category");
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
    public class DeleteDataTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                String jsonObject =  HttpUtil.delete("http://52.24.19.99/item.php/"+selectItem.item_id);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            showProgress(false);
            Toast.makeText(getApplicationContext(), "delete success", Toast.LENGTH_SHORT).show();
            init();
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
