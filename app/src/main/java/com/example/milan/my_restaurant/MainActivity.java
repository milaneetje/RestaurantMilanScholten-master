package com.example.milan.my_restaurant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
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
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // creates all variables needed in this activity
    public RequestQueue queue;
    public List<String> menuItems = new ArrayList<>();
    public ListView resultsListView;
    public TextView mFab;
    public Basket shoppingList = Basket.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Sets up new request
        queue = Volley.newRequestQueue(this);

        //finds needed objects in the activity
        resultsListView = findViewById(R.id.results_listview);
        mFab = findViewById(R.id.fab);
        mFab.setText(Integer.toString(shoppingList.getLength()));

        //Finds bottomnavigation buttons
        Button home = findViewById(R.id.Home);
        Button order = findViewById(R.id.Order);

        //finds previous shoppinlist if one exists
        retrievePrefs();

        //Sets up a request to the API to get JSONObjects
        init();
    }

    private void retrievePrefs() {
        //gets previous shoppinglist if one exists
        SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Gson gson = new Gson();
        String json = mPrefs.getString("shoppingList", "");

        //if the String json is not empty, a new Basket object is created
        if (json != "") {
            Basket restoredJson = gson.fromJson(json, Basket.class);
            List<Item> templist = restoredJson.getItems();
            shoppingList.setItems(templist);
            mFab.setText(Integer.toString(shoppingList.getLength()));
        }
    }

    private void init() {

        RequestQueue queue = Volley.newRequestQueue(this);

        final String url = "https://resto.mprog.nl/categories";

        // Request a string response from the provided URL.
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray categories = response.getJSONArray("categories");

                            for (int i = 0; i < categories.length(); i++) {
                                menuItems.add(categories.get(i).toString());
                            }
                            updateListView();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error.response", error.toString());
                    }
                }
        );

        // add it to RequestQueue
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
        ArrayAdapter adapter = new ArrayAdapter<>(this, R.layout.list_item, R.id.text1, menuItems);
        resultsListView.setAdapter(adapter);
        resultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item text from ListView
                String value = menuItems.get(position);
                Log.d("Error.Response", value);

                Intent intent = new Intent(getBaseContext(), SubMenu.class);
                intent.putExtra("SELECTED_MENU", value);
                startActivity(intent);
            }
        });
    }
    }

