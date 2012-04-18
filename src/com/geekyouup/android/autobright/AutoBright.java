package com.geekyouup.android.autobright;

import android.app.Activity;
import android.content.ContentResolver;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

public class AutoBright extends Activity {
        
        private static final String SCREEN_BRIGHTNESS_MODE = "screen_brightness_mode";
        private static final int SCREEN_MODE_MANUAL = 0;
        private static final int SCREEN_MODE_AUTO = 1;
        
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContentResolver cr = getContentResolver();
        Settings.System.putInt(cr, Settings.System.SCREEN_BRIGHTNESS, 10);
        
        toggleBrightness();
    }
    
    private void toggleBrightness() {

        try {
            ContentResolver cr = getContentResolver();
            Settings.System.putInt(cr, Settings.System.SCREEN_BRIGHTNESS, 10);
            Toast.makeText(this, "Disabling 'Automatic Brightness'", Toast.LENGTH_SHORT).show();
            
                boolean autoBrightOn = (Settings.System.getInt(cr,SCREEN_BRIGHTNESS_MODE,-1)==SCREEN_MODE_AUTO);
                if(autoBrightOn)
                {
                        Settings.System.putInt(cr, SCREEN_BRIGHTNESS_MODE, SCREEN_MODE_MANUAL);
                        Toast.makeText(this, "Disabling 'Automatic Brightness'", Toast.LENGTH_SHORT).show();
                }else
                {
                        Settings.System.putInt(cr, SCREEN_BRIGHTNESS_MODE, SCREEN_MODE_AUTO);
                        Toast.makeText(this, "Enabling 'Automatic Brightness'", Toast.LENGTH_SHORT).show();
                }

                int brightness = Settings.System.getInt(cr,Settings.System.SCREEN_BRIGHTNESS);            
            Settings.System.putInt(cr, Settings.System.SCREEN_BRIGHTNESS, brightness);
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.screenBrightness = brightness / 255.0f;
            getWindow().setAttributes(lp);
        } catch (Exception e) {
            Log.d("Bright", "toggleBrightness: " + e);
        }
        
        final Activity activity = this;
        Thread t = new Thread(){
                public void run()
                {
                        try {
                                        sleep(500);
                                } catch (InterruptedException e) {}
                        activity.finish();
                }
        };
        t.start();
    }
}