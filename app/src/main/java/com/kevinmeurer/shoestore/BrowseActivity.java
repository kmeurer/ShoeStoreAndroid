package com.kevinmeurer.shoestore;

import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.*;
import android.widget.*;
import android.content.ClipData;
import android.graphics.*;
import android.graphics.drawable.*;
import java.util.ArrayList;
import java.util.HashMap;

import android.support.v7.app.ActionBar;

public class BrowseActivity extends ActionBarActivity {

    ArrayList<String> cartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // call the super method
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ArrayList<String> cartItems = (ArrayList<String>) extras.getStringArrayList("cart_items");
        } else {
            cartItems = new ArrayList<String>();
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
        LinearLayout cartView = (LinearLayout) findViewById(R.id.cartView);
        // set up listener for drag events to track the cart
        cartView.setOnDragListener(new cartDragEventListener());

        // set a back button on the action bar for user convenience
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_browse, menu);
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

    // Move to separate cart activity.
    public void moveToCartView(View v){
        Intent moveToCart = new Intent(v.getContext(), CartActivity.class);
        moveToCart.putExtra("cart_items", cartItems);
        startActivity(moveToCart);
    }

    // Event listener for our cart.  Updates the background color
    protected class cartDragEventListener implements View.OnDragListener {
        // Resolve our Colors for reference
        int baseColor = getResources().getColor(R.color.indigo4);
        int lighterColor = getResources().getColor(R.color.indigo6);
        int lightestColor = getResources().getColor(R.color.indigo8);

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

                // when the item is dropped, we add it to the cart.
                case DragEvent.ACTION_DROP:
                    // Gets the item containing the dragged data
                    ClipData.Item item = event.getClipData().getItemAt(0);
                    // get the text from our item and prune it for our data, the name of the product and its price
                    String itemText = (String) item.getText();
                    // get rid of indentations
                    itemText = itemText.replace("\t", "");
                    itemText = itemText.replace("$", "");
                    // split into an array of 2 items, the name and the price
                    String[] nameAndPrice = itemText.split("\n");

                    // Add the item to our cart
                    cartItems.add(itemText);

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

    // shoeadapter class used to fill our grid
    public class ShoeAdapter extends BaseAdapter {
        private Context context;
        // references to our shoeData
        private ArrayList<HashMap<String, Object>> shoeData;


        // In a real app, this would pull data from a src and then load it into the grid, but here we just specify it directly
        private void getShoeInfo(){
            HashMap<String, Object> shoe1 = new HashMap<String, Object>();
            shoe1.put("price", 50);
            shoe1.put("imgsrc", R.drawable.shoe1);
            shoe1.put("name", "New Balance HG7");

            HashMap<String, Object> shoe2 = new HashMap<String, Object>();
            shoe2.put("price", 50);
            shoe2.put("imgsrc", R.drawable.shoe1);
            shoe2.put("name", "New Balance HG7");

            HashMap<String, Object> shoe3 = new HashMap<String, Object>();
            shoe3.put("price", 50);
            shoe3.put("imgsrc", R.drawable.shoe1);
            shoe3.put("name", "New Balance HG7");

            HashMap<String, Object> shoe4 = new HashMap<String, Object>();
            shoe4.put("price", 50);
            shoe4.put("imgsrc", R.drawable.shoe1);
            shoe4.put("name", "New Balance HG7");

            HashMap<String, Object> shoe5 = new HashMap<String, Object>();
            shoe5.put("price", 50);
            shoe5.put("imgsrc", R.drawable.shoe1);
            shoe5.put("name", "New Balance HG7");

            HashMap<String, Object> shoe6 = new HashMap<String, Object>();
            shoe6.put("price", 50);
            shoe6.put("imgsrc", R.drawable.shoe1);
            shoe6.put("name", "New Balance HG7");

            shoeData = new ArrayList<HashMap<String, Object>>();
            shoeData.add(shoe1);
            shoeData.add(shoe2);
            shoeData.add(shoe3);
            shoeData.add(shoe4);
            shoeData.add(shoe5);
            shoeData.add(shoe6);

        }

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
            int itemBackgroundColor = getResources().getColor(R.color.indigo3);

            if (convertView == null) {

                relativeLayout = new LinearLayout(context);
                relativeLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 750));
                relativeLayout.setOrientation(LinearLayout.VERTICAL);

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

            shoeView = (ImageView) relativeLayout.findViewWithTag("shoeImage");
            shoeInfo = (TextView) relativeLayout.findViewWithTag("shoeInfo");
            shoeView.setImageResource((Integer) shoeData.get(position).get("imgsrc"));
            shoeInfo.setText("" + shoeData.get(position).get("name") + "\n$" + shoeData.get(position).get("price"));

            return relativeLayout;
        }


    }
}
