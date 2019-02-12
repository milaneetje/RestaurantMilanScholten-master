package com.example.milan.my_restaurant;

import java.io.Serializable;

/**
 * Created by Milan on 17-1-2018.
 */
 //A class Item that has a description, category, price, image, id and a name
public class Item implements Serializable {
    String category;
    String description;
    int price;
    String image_url;
    int id;
    String name;

    //constructor
    public Item(String category, String description, int price, String image_url, int id, String name)
    {
        this.category = category;
        this.description = description;
        this.price = price;
        this.image_url = image_url;
        this.id = id;
        this.name = name;
    }
}
