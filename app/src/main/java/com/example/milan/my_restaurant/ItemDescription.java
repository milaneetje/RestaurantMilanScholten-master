package com.example.milan.my_restaurant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ItemDescription extends AppCompatActivity {

    public Button buyButton;
    public TextView itemDesc;
    public TextView itemPrice;
    public TextView itemName;
    public ImageView itemImage;
    public Item selectedItem;
    public Basket shoppinglist = Basket.getInstance();
    public TextView mTextMessage;
    public TextView mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_description);

        //Gets selected item from previous activity
        selectedItem = (Item) getIntent().getSerializableExtra("SELECTED_ITEM");

        //Gets objects needed from the activity
        itemImage = findViewById(R.id.itemImage);
        itemPrice = findViewById(R.id.price);
        itemDesc = findViewById(R.id.itemDesc);
        itemName = findViewById(R.id.itemName);
        mTextMessage = findViewById(R.id.message);

        //finds buy button and sets a listener
        buyButton = findViewById(R.id.buyButton);
        buyButton.setOnClickListener(buyAction);

        //Finds floating action button and lets it show the amount of products in the order
        mFab= findViewById(R.id.fab);
        mFab.setText(Integer.toString(shoppinglist.getLength()));

        //finds bottom navigation buttons
        Button home = findViewById(R.id.Home);
        Button order = findViewById(R.id.Order);

        populateData();
    }
    //adds a bought item to the order
    View.OnClickListener buyAction = new View.OnClickListener() {
        public void onClick (View v) {
            Context context = getApplicationContext();

            //Sets a shown text to let user know an item has been added
            CharSequence text = selectedItem.name + " has been added";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();

            //adds item to the shoppinglist
            shoppinglist.addItem(selectedItem);

            //updates the floating action button
            mFab.setText(Integer.toString(shoppinglist.getLength()));

            //hands data to the next activity
            SharedPreferences mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = mPrefs.edit();
            Gson gson = new Gson();
            String json = gson.toJson(shoppinglist);
            editor.putString("shoppingList", json);
            editor.apply();
        }
    };

    public void homeClick(View v) {
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        startActivity(intent);
    }

    public void orderClick(View v) {
        Intent intent_order = new Intent(getBaseContext(), Order.class);
        startActivity(intent_order);
    }

    public void populateData() {
        //Sets description, price and name of the product on the screen
        new ImageLoadTask(selectedItem.image_url, itemImage).execute();
        itemDesc.setText(selectedItem.description);
        itemPrice.setText( "\u20ac" + Integer.toString(selectedItem.price));
        itemName.setText(selectedItem.name);
    }

    //https://stackoverflow.com/questions/18953632/how-to-set-image-from-url-for-imageview
    public class ImageLoadTask extends AsyncTask<Void, Void, Bitmap> {

        private String url;
        private ImageView imageView;

        public ImageLoadTask(String url, ImageView imageView) {
            this.url = url;
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(Void... params) {
            try {
                URL urlConnection = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            imageView.setImageBitmap(result);
        }

    }
}
