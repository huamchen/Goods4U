package com.example.goods4u.goods4u;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;


public class HomepageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private List<Item> items;//要显示的数据集合
    private GridView gvItems;//ListView对象
    private BaseAdapter itemAdapt;//适配器
    private TransferUtility transferUtility;
    private View contentView;
    private View mProgressView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        items = new ArrayList<Item>();
        setContentView(R.layout.activity_homepage);
        transferUtility = Util.getTransferUtility(this);
        contentView=findViewById(R.id.content_homepage);
        mProgressView=findViewById(R.id.homepage_progress);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        ImageButton refresh=(ImageButton)findViewById(R.id.Button_refresh);
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            setImage();
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        setImage();
    }
    public void setImage(){
        showProgress(true);
        new DownloadDataTask().execute();
        GridView gv = (GridView)findViewById(R.id.GridView_picutre);
        gv.setAdapter(new PicAdapter(this));
        gv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                Toast.makeText(HomepageActivity.this, "pic" + position, Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(HomepageActivity.this,picViewActivity.class);
                intent.putExtra("image",items.get(position).image);
                intent.putExtra("title",items.get(position).title);
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
    public class DownloadDataTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {

            items = new ArrayList<Item>();
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
    class PicAdapter extends BaseAdapter {
        private Context context;

        PicAdapter(Context context){
            this.context = context;
        }
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

            View layout = View.inflate(HomepageActivity.this, R.layout.item_image, null);
            ImageView imageView = (ImageView) layout.findViewById(R.id.imageView);
            Item item =  items.get(position);
            Bitmap bitmap= BitmapFactory.decodeFile(Environment.getExternalStorageDirectory()+"/pic/"+item.image);
            imageView.setImageBitmap(bitmap);
            return imageView;
        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_setting) {

        } else if (id == R.id.nav_search) {
            Intent intent=new Intent(HomepageActivity.this,ItemSearchActivity.class);
            startActivity(intent);
        } else if (id== R.id.nav_sell){
            Intent intent=new Intent(HomepageActivity.this,ItemManageActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void enlargeImage(View v) {
   //     ImageView image = (ImageView) findViewById(R.id.imageLarge);
   //     image.setImageDrawable(((ImageView) v).getDrawable());

        //  setImageViewEnable(false);

        Intent intent=new Intent(HomepageActivity.this,picViewActivity.class);
        intent.putExtra("color",(String)v.getTag());
        startActivity(intent);

    }
    public Bitmap getPicture(String path){
        Bitmap bm=null;
        try{
            URL url=new URL(path);
            URLConnection connection=url.openConnection();
            connection.connect();
            InputStream inputStream=connection.getInputStream();
            bm= BitmapFactory.decodeStream(inputStream);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return  bm;
    }
}
