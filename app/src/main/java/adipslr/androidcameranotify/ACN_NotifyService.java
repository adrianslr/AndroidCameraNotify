package adipslr.androidcameranotify;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;


public class ACN_NotifyService extends Service
{
    private WindowManager mWindowManager;
    private View mFloatingView;

    public ACN_NotifyService( ) { }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate( )
    {
        super.onCreate( );

        mFloatingView = LayoutInflater.from( this ).inflate( R.layout.layout_floating_widget, null );

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams
                (
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_PHONE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT
                );

        params.gravity = Gravity.TOP | Gravity.RIGHT;
        params.x = 0;
        params.y = 100;

        mWindowManager = ( WindowManager ) getSystemService( WINDOW_SERVICE );
        mWindowManager.addView( mFloatingView, params );

        mFloatingView.findViewById( R.id.root_container ).setOnTouchListener( new View.OnTouchListener( )
        {
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch( View v, MotionEvent event )
            {
                switch( event.getAction( ) )
                {
                    case MotionEvent.ACTION_DOWN:
                    {
                        initialTouchX = event.getRawX( );
                        initialTouchY = event.getRawY( );

                        return true;
                    }

                    case MotionEvent.ACTION_UP:
                    {
                        int Xdiff = ( int ) ( event.getRawX( ) - initialTouchX );
                        int Ydiff = ( int ) ( event.getRawY( ) - initialTouchY );

                        if( Xdiff < 10 && Ydiff < 10 )
                        {
                            if( isViewCollapsed( ) )
                            {
                                Intent intent = new Intent( ACN_NotifyService.this, ACN_Main.class );
                                intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
                                startActivity( intent );

                                stopSelf( );
                            }
                        }
                        return true;
                    }
                }
                return false;
            }
        } );

        Handler handler = new Handler( );
        handler.postDelayed(
                new Runnable( )
                {
                    @Override
                    public void run( )
                    {
                        stopSelf( );
                    }
                }, getResources( ).getInteger( R.integer.notifyTimeOnScreen )
        );
    }

    private boolean isViewCollapsed( )
    {
        return mFloatingView == null || mFloatingView.findViewById(R.id.collapse_view).getVisibility( ) == View.VISIBLE;
    }

    @Override
    public void onDestroy( )
    {
        super.onDestroy( );
        if( mFloatingView != null )
            mWindowManager.removeView( mFloatingView );
    }
}
