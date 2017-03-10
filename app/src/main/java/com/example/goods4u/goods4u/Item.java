package com.example.goods4u.goods4u;

/**
 * Created by hasee on 2017/2/5.
 */

public class Item {
    public String owner_id;
    public String item_id;
    public String title;
    public String description=null;
    public String price=null;
    public String image;
    public String category;
    public Item(String mImage,String mCategory){
        this.image=mImage;
        this.category=mCategory;
    }
}
