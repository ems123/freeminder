package com.freeminder.saralam;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.PushService;

/**
 * Created by Sasikumar Reddy on 31-08-2015.
 */
public class ParseInitialization extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(this, "AYrq9CYZ78PEE49h8UdtJhOdHvtQwR5dXPD2nCpY", "8o0eftEaOfX0X1aNgw0GeHhqGwHfC41dydO6jEpP");
        ParseInstallation.getCurrentInstallation().saveInBackground();


    }
}
