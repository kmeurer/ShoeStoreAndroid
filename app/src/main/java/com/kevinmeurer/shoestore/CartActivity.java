package com.kevinmeurer.shoestore;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class CartActivity extends ActionBarActivity {
    // stores the items from our cart
    ArrayList<String> cartItems = new ArrayList<String>();

    /*
        onCreate: Creates our activity by pulling any extras passed in, fills the text view with cart info,
        and sets whether the place order is clickable(not clickable if no items in cart).
         */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Get the items from the cart
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            cartItems = (ArrayList<String>) extras.getStringArrayList("cart_items");
        }

        // Get our place order button so we can later make it not clickable if we have nothing in the cart.
        Button orderButton = (Button) findViewById(R.id.checkoutButton);

        // display information about the cart
        TextView cartText = (TextView) findViewById(R.id.cartItems);
        if (cartItems.size() == 0){
            cartText.setText("Your cart is currently empty.\n\nContinue shopping and come back when you are ready to check out!");
            if (orderButton != null){
                orderButton.setClickable(false);
            }
        }
        else {
            // Limit our result to display 2 decimals
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);
            df.setMinimumFractionDigits(2);

            // Create a string to be added to the text view
            String text = "";
            double totalCost = 0;
            for(int i = 0; i < cartItems.size(); i ++){
                String currentItem = cartItems.get(i);
                String[] nameAndPrice = currentItem.split("\n");
                text += nameAndPrice[0];
                text += "\n\t\t\t\t\t\t\t" + nameAndPrice[1] + "\n";
                totalCost += Double.parseDouble((String) nameAndPrice[1]);
            }

            // print our total
            text += "__________________________\nTOTAL:  $" + df.format(totalCost);
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
            Toast.makeText(this, "pressed",
                    Toast.LENGTH_LONG).show();
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // move to the pick up view, called when we place our order
    public void moveToPickUp(View v){
        Intent moveToPickUp = new Intent(v.getContext(), PickUpActivity.class);
        moveToPickUp.putExtra("cart_items", cartItems);
        startActivity(moveToPickUp);
    }

    // move to the browse view.  called if we want to go back
    public void moveToBrowse(View v){
        Intent moveToBrowseView = new Intent(v.getContext(), BrowseActivity.class);
        moveToBrowseView.putExtra("cart_items", cartItems);
        startActivity(moveToBrowseView);
    }
}
