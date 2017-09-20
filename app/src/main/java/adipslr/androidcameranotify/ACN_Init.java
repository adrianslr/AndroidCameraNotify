package adipslr.androidcameranotify;

import android.app.Application;

public class ACN_Init extends Application
{
    @Override
    public void onCreate( )
    {
        super.onCreate( );

        ACN_WakefulReceiver.setAlarm( this );
    }
}