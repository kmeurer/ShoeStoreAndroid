package com.kevinmeurer.shoestore;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;


public class CartActivity extends ActionBarActivity {
    ArrayList<String> cartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            cartItems = (ArrayList<String>) extras.getStringArrayList("cart_items");
        }

        TextView cartText = (TextView) findViewById(R.id.cartItems);
        if (cartItems.size() == 0){
            cartText.setText("You have not added any items to your cart.\n\nContinue shopping and come back when you are ready to check out!");
        }
        else {
            // Create a string to be added to the text view
            String text = "";
            double totalCost = 0;
            for(int i = 0; i < cartItems.size(); i ++){
                String currentItem = cartItems.get(i);
                String[] nameAndPrice = currentItem.split(" ");
                text += nameAndPrice[0] + "\n";
                text += "\t\t\t\t\t\t\t\t" + nameAndPrice[1] + "\n\n";
                totalCost += Double.parseDouble((String) nameAndPrice[1]);
            }
            text += "____________\nTOTAL:\n\t\t\t\t\t\t\t\t" + totalCost;
            cartText.setText(text);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.home) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
