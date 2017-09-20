package adipslr.androidcameranotify;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.WakefulBroadcastReceiver;

import java.util.Calendar;
import java.util.Date;

public class ACN_WakefulReceiver extends WakefulBroadcastReceiver
{
    public void onReceive(Context context, Intent intent )
    {
        if( !ACN_Main.isInForeground( ) )
        {
            context.startService( new Intent( context, ACN_NotifyService.class ) );
        }

        setAlarm( context );

        ACN_WakefulReceiver.completeWakefulIntent(intent);
    }

    public static void setAlarm( Context context )
    {
        setAlarmEx( context );

        setBoot( context );
    }

    public static void setAlarmEx( Context context )
    {
        AlarmManager mAlarmManager = ( AlarmManager ) context.getSystemService( Context.ALARM_SERVICE );
        Intent intent = new Intent( context, ACN_WakefulReceiver.class );

        PendingIntent alarmIntent = PendingIntent.getBroadcast( context, 0, intent, 0 );

        Calendar calendar = Calendar.getInstance( );
        calendar.setTimeInMillis( System.currentTimeMillis( ) );

        calendar.add( Calendar.SECOND, context.getResources( ).getInteger( R.integer.notifyInterval ) );

        Date date = calendar.getTime( );

        mAlarmManager.setExact( AlarmManager.RTC_WAKEUP, date.getTime( ), alarmIntent );
    }

    private static void setBoot( Context context )
    {
        ComponentName receiver = new ComponentName( context, ACN_BootReceiver.class );
        PackageManager pm = context.getPackageManager( );
        pm.setComponentEnabledSetting( receiver, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP );
    }
}