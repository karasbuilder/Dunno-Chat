package ChatApp.android;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

public class GlobalStuff extends Application {

    public void onCreate() {
        super.onCreate();
    }

    private static Activity mCurrentActivity = null;
    public static Activity getCurrentActivity(){

        return mCurrentActivity;
    }
    public static void setCurrentActivity(Activity CurrentActivity){
        mCurrentActivity = CurrentActivity;
    }

    private static boolean background;
    public static boolean getIsBackground(){

        return background;
    }
    public static void setIsBackground(boolean IsBackground){
        background = IsBackground;
    }
}
