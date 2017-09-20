package adipslr.androidcameranotify;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ACN_BootReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent )
    {
        if( intent.getAction( ).equals( Intent.ACTION_BOOT_COMPLETED ) )
        {
            ACN_WakefulReceiver.setAlarmEx( context );
        }
    }
}