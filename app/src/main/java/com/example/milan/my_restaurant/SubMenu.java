package com.example.milan.my_restaurant;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SubMenu extends AppCompatActivity {

    public List<String> submenuItems = new ArrayList<>();
    public ListView resultsListView;
    private String selectedMenu;
    private List<Item> parsedItems = new ArrayList<>();
    public TextView mFab;
    public Basket shoppingList = Basket.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_menu);

        //Finds the Floating actionbutton and sets it to the amount of products in the order
        mFab = findViewById(R.id.fab);
        mFab.setText(Integer.toString(shoppingList.getLength()));

        //finds the bottomnavigation buttons home and order
        Button home = findViewById(R.id.Home);
        Button order = findViewById(R.id.Order);

        //finds the listview for the items
        resultsListView = findViewById(R.id.results_listview);

        //gets the selected category form the previous activity
        selectedMenu = getIntent().getStringExtra("SELECTED_MENU");
        init();
    }

    private void init() {
        RequestQueue queue = Volley.newRequestQueue(this);

        final String url = "https://resto.mprog.nl/menu?category=" +selectedMenu;

        // Request a string response from the provided URL.
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response){
                        try {
                            JSONArray categories = response.getJSONArray("items");
                            Log.d("jsonarray 50", response.getJSONArray("items").toString());
                            for (int i = 0; i < categories.length(); i ++) {
                                JSONObject subitem = categories.getJSONObject(i);
                                String category = subitem.getString("category");
                                String description = subitem.getString("description");
                                int price = subitem.getInt("price");
                                String image_url = subitem.getString("image_url");
                                int id = subitem.getInt("id");
                                String name = subitem.getString("name");

                                Item tempItem = new Item(category, description, price, image_url, id, name);
                                parsedItems.add(tempItem);
                                submenuItems.add(subitem.getString("name"));
                            }
                            updateListView();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.Response", error.toString());
                    }
                }
        );

        // add it to the requestqueue
        queue.add(getRequest);
    }

    public void homeClick(View v) {
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
    }

    public void orderClick(View v) {
        Intent intent_order = new Intent(getBaseContext(), Order.class);
        startActivity(intent_order);
    }

    private void updateListView() {
        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.list_item, R.id.text1, submenuItems);
        resultsListView.setAdapter(adapter);
        resultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Get the selected item text from ListView
                String value = submenuItems.get(position);
                Log.d("Error.Response", value);

                Intent intent = new Intent(getBaseContext(), ItemDescription.class);
                for(Item item: parsedItems) {
                    if(item.name.equalsIgnoreCase(value)) {
                        intent.putExtra("SELECTED_ITEM", item);
                    }
                }

                startActivity(intent);
            }
        });
    }
}
