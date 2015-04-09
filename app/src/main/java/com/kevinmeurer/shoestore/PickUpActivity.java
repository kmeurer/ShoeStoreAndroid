package com.kevinmeurer.shoestore;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;


public class PickUpActivity extends ActionBarActivity {
    // stores our cart data
    ArrayList<String> cartItems;
    // stores the reference to our google map
    MapFragment map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_up);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            cartItems = extras.getStringArrayList("cart_items");
        }

        // get our itemized list
        TextView itemList = (TextView) findViewById(R.id.itemizedResult);
        String text = "Your items:\n";
        for (String str:cartItems){
            String[] newStr = str.split("\n");
            text += "\t\t\t\t\t" + newStr[0] + "\n";
        }
        itemList.setText(text);

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());
        // if it's available, we get the map fragment
        if( status== ConnectionResult.SUCCESS){
            // get our map fragment
            MapFragment mapFrag = (MapFragment) getFragmentManager().findFragmentById(R.id.storeMap);
            GoogleMap map = mapFrag.getMap();
            // if our map exists, we add the marker to the map
            if (map != null){
                // add a marker to the map
                MarkerOptions markerOptions = new MarkerOptions();
                LatLng place = new LatLng(38.908864, -77.072298);
                markerOptions.position(place);
                markerOptions.title("Outfooted Shoes");

                // add the marker to our map
                map.addMarker(markerOptions);
                System.gc();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_pick_up, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }
}
