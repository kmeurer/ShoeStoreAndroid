package com.kevinmeurer.shoestore;

import android.test.ActivityInstrumentationTestCase2;
import android.view.ViewGroup;
import android.widget.Button;

public class WelcomeActivityTest extends ActivityInstrumentationTestCase2<WelcomeActivity> {

    private WelcomeActivity activity;
    private Button welcomeButton;

    public WelcomeActivityTest() {
        super(WelcomeActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        activity = getActivity();
        welcomeButton = (Button) activity.findViewById(R.id.getStartedBtn);
    }

    public void testPreconditions() {
        assertNotNull("activity is null", activity);
        assertNotNull("welcomeButton is null", welcomeButton);
    }

    public void testWelcomeButton(){
        String msg = "Get Started";
        assertEquals(msg, welcomeButton.getText());
    }
}

