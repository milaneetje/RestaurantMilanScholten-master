package com.example.milan.my_restaurant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Milan on 19-1-2018.
 */

public class Order extends AppCompatActivity{

    //All variables needed by the activity
    public Basket shoppingList = Basket.getInstance();
    public ListView ordersListView;
    public List<String> orders = new ArrayList<>();
    public List<Item> orderList = new ArrayList<Item>();
    public ArrayAdapter adapter;
    public RequestQueue queue;
    public TextView mFab;
    public Integer totalMoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        //finds components of the activity
        ordersListView = findViewById(R.id.order_listview);
        mFab = findViewById(R.id.fabtotal);


        //fills the array list with the items in the shoppinglist
        orderList = shoppingList.getItems();

        queue = Volley.newRequestQueue(this);

        //sets up a listener for the bottom buttons
        Button home= findViewById(R.id.Home);
        Button order= findViewById(R.id.Order);

        updateListView();
    }

    public void homeClick(View v) {
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
    }

    public void orderClick(View v) {
        postOrderList();
    }

    private void postOrderList() {
        RequestQueue queue = Volley.newRequestQueue(this);

        final String url = "https://resto.mprog.nl/order";

        // Request a string response from the provided URL.
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Context context = getApplicationContext();
                            JSONObject parsedObject = new JSONObject(response);

                            //Creates a string hown to the user with a preparation time and a total
                            String time = parsedObject.getString("preparation_time");
                            CharSequence text = "Order will be served in " + time + "minutes! "
                                    + "Your bill is: " + Integer.toString(totalMoney) + "\u20ac";
                            int duration = Toast.LENGTH_SHORT;
                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();

                            //clears the shoppinglist and the adapter.
                            shoppingList.clearOrder();
                            adapter.clear();

                            SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
                            SharedPreferences.Editor editor = mPrefs.edit();
                            Gson gson = new Gson();
                            String json = gson.toJson(shoppingList);

                            editor.putString("shoppingList", json);
                            editor.apply();
                            updateListView();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                    }
                }
        ) {
            @Override
            //creates a hashmap to post the order
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                for (Item order: orderList) {
                    params.put(Integer.toString(order.id), "");
                }

                return params;
            }
        };
        queue.add(postRequest);

    }

    private void refreshList(int position) {
        //deletes one item from shoppinglist and adapter
        shoppingList.deletItem(orderList.get(position));
        adapter.remove(adapter.getItem(position));
        orderList = shoppingList.getItems();

        //updates shoppinglist
        Context context = getApplicationContext();
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(shoppingList);
        editor.putString("shoppingList", json);
        editor.apply();

        updateListView();
    }

    private void updateListView() {
        //gets updated shoppinglist
        orderList = shoppingList.getItems();
        orders = new ArrayList<>();
        totalMoney = 0;

        //updates listview
        for(Item order: orderList) {
            orders.add(order.name + "    " + Integer.toString(order.price) + "\u20ac");
            totalMoney += order.price;
        }
        mFab.setText("your total is: " + "\u20ac" + Integer.toString(totalMoney));
        adapter = new ArrayAdapter<>(this, R.layout.list_item, R.id.text1, orders);
        ordersListView.setAdapter(adapter);

        //sets a listener for items in the list.
        ordersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                refreshList(position);
            }
        });
    }
}
