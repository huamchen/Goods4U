package com.example.goods4u.goods4u;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;


public class HomepageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        Intent intent=getIntent();
        username=intent.getStringExtra("username");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        setImage();
    }
    public void setImage(){
        final ImageView image1 = (ImageView) findViewById(R.id.imageView1);
        final ImageView image2 = (ImageView) findViewById(R.id.imageView2);
        final ImageView image3 = (ImageView) findViewById(R.id.imageView3);
        final ImageView image4 = (ImageView) findViewById(R.id.imageView4);

        final Bitmap[] bp = new Bitmap[4];

        new Thread(new Runnable() {
            @Override
            public void run() {
                bp[0] =getPicture("http://www.drodd.com/images15/1-24.png");
                bp[1] =getPicture("http://www.drodd.com/images15/2-24.png");
                bp[2] =getPicture("http://www.drodd.com/images15/3-24.png");
                bp[3] =getPicture("http://www.drodd.com/images15/4-24.png");
            }
        }).start();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        image1.setImageBitmap(bp[0]);
        image1.setTag("http://www.drodd.com/images15/1-24.png");
        image2.setImageBitmap(bp[1]);
        image2.setTag("http://www.drodd.com/images15/2-24.png");
        image3.setImageBitmap(bp[2]);
        image3.setTag("http://www.drodd.com/images15/3-24.png");
        image4.setImageBitmap(bp[3]);
        image4.setTag("http://www.drodd.com/images15/4-24.png");
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
