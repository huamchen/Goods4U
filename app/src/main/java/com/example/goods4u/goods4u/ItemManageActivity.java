package com.example.goods4u.goods4u;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ItemManageActivity extends AppCompatActivity {
    private List<Item> items;//要显示的数据集合
    private ListView lvItems;//ListView对象
    private BaseAdapter itemAdapt;//适配器
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_manage);
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


    private void init() {
        items = new ArrayList<Item>();
       // for (int i =0;i<resImags.length;i++){
      //     Item item = new Item(resImags[i],names[i]);
       //    items.add(item);
       // }
        lvItems = (ListView) findViewById(R.id.lvItems);
        itemAdapt = new ItemAdapter();
        lvItems.setAdapter(itemAdapt);
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
        //    ivThumb.setImageResource(general.getImageSrc());
            tvName.setText(item.title);
            return layout;
        }

    }
}
