package com.example.milan.my_restaurant;

import java.util.ArrayList;
import java.util.List;


//A class basket that has a list of items.
public class Basket {

    private List<Item> currentOrders = new ArrayList<Item>();

    public void deletItem (com.example.milan.my_restaurant.Item item)
    {
        //removes item from the list
        this.currentOrders.remove(item);
    }

    public void addItem (com.example.milan.my_restaurant.Item item)
    {
        //adds item to the list
        this.currentOrders.add(item);
    }

    public void clearOrder () {
        //clears the entire order
        this.currentOrders = new ArrayList<Item>();
    }

    //gets items
    public List<Item> getItems () {return this.currentOrders;}

    //sets items
    public void setItems (List<Item> orderList) {this.currentOrders = orderList;}

    //gets length of the list
    public int getLength () {return this.currentOrders.size();}

    private static final Basket basketlist = new Basket();

    public static Basket getInstance() {return basketlist;}

}
