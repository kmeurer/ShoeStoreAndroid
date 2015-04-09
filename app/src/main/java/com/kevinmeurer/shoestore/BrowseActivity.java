package com.kevinmeurer.shoestore;

import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.*;
import android.widget.*;
import android.content.ClipData;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import android.support.v7.app.ActionBar;

public class BrowseActivity extends ActionBarActivity {
    // Create an array to store the items in our cart.  For simplicity, these are stored as "item_name\nprice"(e.g. "New Balance Super Shoe\n50".  This allows us to retain the information when it is passed between activities
    ArrayList<String> cartItems = new ArrayList<String>();

    /*
        onCreate:  This override creates our browse activity.  It does the following:
        1. sets up a grid view
        2. gets any cart data that has been passed to it
        3. Sets a long click listener on items in the gridView to initiate dragging
        4. Initializes our cart at the bottom of the screen
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // call the super method
        super.onCreate(savedInstanceState);

        // get any cart items that have been passed to it from activity transitions
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            cartItems = (ArrayList<String>) extras.getStringArrayList("cart_items");
        }

        // set the content view with the layout
        setContentView(R.layout.activity_browse);

        // GRID VIEW SETUP
        GridView gridview = (GridView) findViewById(R.id.gridview);
        ShoeAdapter shoeAdapter = new ShoeAdapter(this);
        gridview.setAdapter(shoeAdapter);

        // set long click listener to start the drag
        gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            // Override on item long click
            public boolean onItemLongClick(AdapterView<?> parent, View v, int position, long id) {
            // Create a new ClipData.Item from the ImageView object's tag
            TextView textToSend = (TextView) v.findViewWithTag("shoeInfo");
            ClipData.Item item = new ClipData.Item((CharSequence) textToSend.getText());

            // list the mimeTypes for the ClipData
            String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN, ClipDescription.MIMETYPE_TEXT_PLAIN};

            // instantiate a new ClipData object
            ClipData dragData = new ClipData((CharSequence) v.getTag(), mimeTypes, item);

            // Instantiates the drag shadow builder.
            View.DragShadowBuilder imageShadow = new View.DragShadowBuilder(v);

            // Start a drag
            v.startDrag(dragData,  // the data to be dragged
                    imageShadow,  // the drag shadow builder
                    null,      // no need to use local data
                    0          // flags (not currently used, set to 0)
            );

            return true;
            }
        });

        // CART VIEW SETUP
        RelativeLayout cartView = (RelativeLayout) findViewById(R.id.cartView);
        // set up listener for drag events to track the cart
        cartView.setOnDragListener(new cartDragEventListener());

        // set a back button on the action bar for user convenience
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
    }

    /*
        onCreateOptionsMenu: inflates the options menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_browse, menu);
        return true;
    }

    /*
        Called when an options item is selected.  A settings page has not been implemented here.
     */
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

    /*
        moveToCartView: Move to separate cart activity.
     */
    public void moveToCartView(View v){
        Intent moveToCart = new Intent(v.getContext(), CartActivity.class);
        moveToCart.putExtra("cart_items", cartItems);
        startActivity(moveToCart);
    }

    /*
        cartDragEventListener: Class applied to our cart view that listens for movement in our gridView
        dynamically updates the color of the cart depending on where the dragged object is on the screen.
     */
    protected class cartDragEventListener implements View.OnDragListener {
        // Resolve our Colors for reference. Base color is the color it is stored as.  Lighter is while the drag is happening, lightest if the dragged object enters the cart area
        int baseColor = getResources().getColor(R.color.indigo6);
        int lighterColor = getResources().getColor(R.color.indigo8);
        int lightestColor = getResources().getColor(R.color.indigo10);

        // override the onDrag function
        public boolean onDrag(View v, DragEvent event) {
            // Create a variable to store our action
            final int action = event.getAction();

            // Handle each of our expected events
            switch (action) {
                // When the drag begins, we update the color of the cart to indicate that the item can be dragged there
                case DragEvent.ACTION_DRAG_STARTED:

                    // Determines if this View can accept the dragged data
                    if (event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {

                        // As an example of what your application might do,
                        // applies a blue color tint to the View to indicate that it can accept
                        // data.
                        v.setBackgroundColor(lighterColor);
                        // Invalidate the view to force a redraw in the new tint
                        v.invalidate();

                        // returns true to indicate that the View can accept the dragged data.
                        return true;

                    }

                    // Returns false. During the current drag and drop operation, this View will
                    // not receive events again until ACTION_DRAG_ENDED is sent.
                    return false;

                // When the object enters the cart region
                case DragEvent.ACTION_DRAG_ENTERED:
                    // Set the color as the lightest color
                    v.setBackgroundColor(lightestColor);
                    // Invalidate the view to force a redraw in the new tint
                    v.invalidate();
                    return true;
                // Called repeatedly.  Ignored here
                case DragEvent.ACTION_DRAG_LOCATION:
                    // Ignore the event
                    return true;

                // when the object leaves the cart area...
                case DragEvent.ACTION_DRAG_EXITED:
                    // Sets the color back to a lighter color to indicate that the item is no longer in the cart.
                    v.setBackgroundColor(lighterColor);

                    // Invalidate the view to force a redraw in the new tint
                    v.invalidate();

                    return true;

                // when the item is dropped in the cart area, we add it to the cart.
                case DragEvent.ACTION_DROP:
                    // Gets the item containing the dragged data
                    ClipData.Item item = event.getClipData().getItemAt(0);
                    // get the text from our item and prune it for our data, the name of the product and its price
                    String itemText = (String) item.getText();
                    // get rid of indentations and dollar signs
                    itemText = itemText.replace("\t", "");
                    itemText = itemText.replace("$", "");

                    // split into an array of 2 items, the name and the price [name, price]
                    String[] nameAndPrice = itemText.split("\n");

                    // Add the item to our cart
                    cartItems.add(itemText);

                    // indicate to the user through a toast that their item has been added to the cart
                    Toast.makeText(BrowseActivity.this, "Added one " + nameAndPrice[0] + " to the cart.",
                            Toast.LENGTH_SHORT).show();

                    // Invalidates the view to force a redraw
                    v.invalidate();

                    // Returns true. DragEvent.getResult() will return true.
                    return true;

                // when we are done dragging, we revert to our base color
                case DragEvent.ACTION_DRAG_ENDED:
                    // Sets the color back to the base color
                    v.setBackgroundColor(baseColor);

                    // Invalidates the view to force a redraw
                    v.invalidate();

                    // returns true; the value is ignored.
                    return true;

                // If we received an unknown action, break
                default:
                    break;
            }

            return false;
        }
    }

    /*
        ShoeAdapter: A class that inherits BaseAdapters methods to fill our grid with image/text.  This pulls data from (in this case) a static
        group of values, stored as an arraylist of HashMaps, to fill the grid.  In a more robust application, these would likely be pulled from a sqlite db
     */
    public class ShoeAdapter extends BaseAdapter {
        private Context context;

        // references to our shoeData
        private ArrayList<HashMap<String, Object>> shoeData;

        // In a real app, this would pull data from a src and then load it into the grid, but here we just specify it directly
        private void getShoeInfo(){
            HashMap<String, Object> shoe1 = new HashMap<String, Object>();
            shoe1.put("price", 129.99);
            shoe1.put("imgsrc", R.drawable.shoe1);
            shoe1.put("name", "Nike Snakeskin 42");

            HashMap<String, Object> shoe2 = new HashMap<String, Object>();
            shoe2.put("price", 50);
            shoe2.put("imgsrc", R.drawable.shoe2);
            shoe2.put("name", "Nike DuraForce");

            HashMap<String, Object> shoe3 = new HashMap<String, Object>();
            shoe3.put("price", 99.99);
            shoe3.put("imgsrc", R.drawable.shoe3);
            shoe3.put("name", "Nike Tony Hawk X");

            HashMap<String, Object> shoe4 = new HashMap<String, Object>();
            shoe4.put("price", 84.99);
            shoe4.put("imgsrc", R.drawable.shoe4);
            shoe4.put("name", "Nike Forward Mesh");

            HashMap<String, Object> shoe5 = new HashMap<String, Object>();
            shoe5.put("price", 100);
            shoe5.put("imgsrc", R.drawable.shoe5);
            shoe5.put("name", "Columbia TechLite");

            HashMap<String, Object> shoe6 = new HashMap<String, Object>();
            shoe6.put("price", 60);
            shoe6.put("imgsrc", R.drawable.shoe6);
            shoe6.put("name", "Vans Off the Wall");

            HashMap<String, Object> shoe7 = new HashMap<String, Object>();
            shoe7.put("price", 65.99);
            shoe7.put("imgsrc", R.drawable.shoe7);
            shoe7.put("name", "Nike Internationalist");

            HashMap<String, Object> shoe8 = new HashMap<String, Object>();
            shoe8.put("price", 74.95);
            shoe8.put("imgsrc", R.drawable.shoe8);
            shoe8.put("name", "New Balance M775V1");

            HashMap<String, Object> shoe9 = new HashMap<String, Object>();
            shoe9.put("price", 49.99);
            shoe9.put("imgsrc", R.drawable.shoe9);
            shoe9.put("name", "New Balance MX623");

            HashMap<String, Object> shoe10 = new HashMap<String, Object>();
            shoe10.put("price", 60.00);
            shoe10.put("imgsrc", R.drawable.shoe10);
            shoe10.put("name", "Asics Gel Unifire");

            shoeData = new ArrayList<HashMap<String, Object>>();
            shoeData.add(shoe1);
            shoeData.add(shoe2);
            shoeData.add(shoe3);
            shoeData.add(shoe4);
            shoeData.add(shoe5);
            shoeData.add(shoe6);
            shoeData.add(shoe7);
            shoeData.add(shoe8);
            shoeData.add(shoe9);
            shoeData.add(shoe10);
        }

        // constructor fills our data storage with shoe information (text description and img src)
        public ShoeAdapter(Context c) {
            context = c;
            getShoeInfo();
        }

        public int getCount() {
            return shoeData.size();
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public LinearLayout getView(int position, View convertView, ViewGroup parent) {
            LinearLayout relativeLayout;
            ImageView shoeView;
            TextView shoeInfo;
            int itemBackgroundColor = getResources().getColor(R.color.indigo1_5);

            if (convertView == null) {

                relativeLayout = new LinearLayout(context);
                relativeLayout.setOrientation(LinearLayout.VERTICAL);
                relativeLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 550));

                relativeLayout.setBackgroundColor(itemBackgroundColor);
                relativeLayout.setPadding(5, 20, 5, 20);

                // if it's not recycled, initialize some attributes
                // initialize our shoeImage
                shoeView = new ImageView(context);
                shoeView.setTag("shoeImage");

                // Initialize our Shoe Caption
                shoeInfo = new TextView(context);
                shoeInfo.setTag("shoeInfo");
                shoeInfo.setPadding(30, 0, 0, 0);
                shoeInfo.setVisibility(View.VISIBLE);

                // add our views to the layout
                relativeLayout.addView(shoeInfo);
                relativeLayout.addView(shoeView);

                shoeView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                shoeView.setPadding(15, 8, 15, 8);
            } else {
                relativeLayout = (LinearLayout) convertView;
            }

            // Limit our prices to display 2 decimals
            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);
            df.setMinimumFractionDigits(2);

            shoeView = (ImageView) relativeLayout.findViewWithTag("shoeImage");
            shoeInfo = (TextView) relativeLayout.findViewWithTag("shoeInfo");
            shoeView.setImageResource((Integer) shoeData.get(position).get("imgsrc"));
            shoeInfo.setText("" + shoeData.get(position).get("name") + "\n$" + df.format(shoeData.get(position).get("price")));
            // clean unused bitmaps
            System.gc();

            return relativeLayout;
        }


    }
}
