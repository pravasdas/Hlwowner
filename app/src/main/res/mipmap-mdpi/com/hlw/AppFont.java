package oditek.com.hlw;

import android.app.Application;

public class AppFont extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/Poppins-Regular.ttf");
        TypefaceUtil.overrideFont(getApplicationContext(), "MONOSPACE", "fonts/Poppins-SemiBold.ttf");
    }

}
