package com.kevinmeurer.shoestore;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

public class CartActivityTest extends ActivityInstrumentationTestCase2<CartActivity> {

    private CartActivity activity;
    private TextView cartMsg;

    public CartActivityTest() {
        super(CartActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        activity = getActivity();
        cartMsg = (TextView) activity.findViewById(R.id.cartItems);
    }

    public void testPreconditions() {
        assertNotNull("activity is null", activity);
        assertNotNull("cartItems is null", cartMsg);
    }

    public void testWelcomeButton(){
        String msg = "Your cart is currently empty.\n\nContinue shopping and come back when you are ready to check out!";
        assertEquals(msg, cartMsg.getText());
    }

    public void testCartItemStore(){
        // make sure that it is instantiated automatically
        if (activity.cartItems == null){
            fail();
        }
        // size of cartItems should be zero
        assertEquals(activity.cartItems.size(), 0);
    }

}

