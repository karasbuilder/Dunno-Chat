package ChatApp.android;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

public class GlobalStuff extends Application {

    public void onCreate() {
        super.onCreate();
    }

    private static Activity mCurrentActivity = null;

    //get current stored activity name
    public static Activity getCurrentActivity(){

        return mCurrentActivity;
    }

    //set current activity name to storage
    public static void setCurrentActivity(Activity CurrentActivity){
        mCurrentActivity = CurrentActivity;
    }

    private static boolean background;

    //check if app is in background
    public static boolean getIsBackground(){

        return background;
    }

    //set background to true
    public static void setIsBackground(boolean IsBackground){
        background = IsBackground;
    }
}
