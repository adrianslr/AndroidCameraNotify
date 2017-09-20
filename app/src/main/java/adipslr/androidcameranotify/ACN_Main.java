package adipslr.androidcameranotify;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ACN_Main extends AppCompatActivity
{
    private static boolean mIsInForegroundMode;

    private Camera mCamera = null;
    private ACN_surfaceView mCameraView = null;

    String upLoadServerUri = "";

    Camera.PictureCallback jpegCallback;

    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );

        upLoadServerUri += getString( R.string.uploadUrl );

        String[ ] permissionsRequired = new String[ ]
                {
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECEIVE_BOOT_COMPLETED
                };

        if( !hasPermissions( this, permissionsRequired ) )
        {
            ActivityCompat.requestPermissions( this, permissionsRequired, 1 );
        }

        if( Build.VERSION.SDK_INT >= 23 )
        {
            if( !Settings.canDrawOverlays( ACN_Main.this ) )
            {
                Intent intent = new Intent( Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse( "package:" + getPackageName( ) ) );

                startActivityForResult( intent, 2048 );
            }
        }

        requestWindowFeature( Window.FEATURE_NO_TITLE );
        getWindow( ).setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN );

        setContentView( R.layout.activity_main__go );

        try
        {
            mCamera = Camera.open( findFrontFacingCamera( ) );
        }
        catch( Exception e ) { }

        if( mCamera != null )
        {
            mCameraView = new ACN_surfaceView( this, mCamera );
            FrameLayout camera_view = ( FrameLayout )findViewById( R.id.camera_view );
            camera_view.addView( mCameraView );
        }

        jpegCallback = new Camera.PictureCallback( )
        {
            public void onPictureTaken( byte[ ] data, Camera camera )
            {
                FileOutputStream outStream = null;

                try
                {
                    File imagesFolder = new File( Environment.getExternalStorageDirectory( ), "MyImages" );
                    imagesFolder.mkdirs( );

                    final String loc = String.format( "%s/%d.jpg", imagesFolder, System.currentTimeMillis( ) );

                    outStream = new FileOutputStream( loc );
                    outStream.write( data );
                    outStream.close( );

                    new Thread( new Runnable( )
                    {
                        public void run( )
                        {
                            uploadFile( loc );
                        }
                    }).start( );
                }
                catch( FileNotFoundException e )
                {
                    e.printStackTrace( );
                }
                catch( IOException e )
                {
                    e.printStackTrace( );
                }
                finally { }

                Toast.makeText( getApplicationContext(), "Picture sent to the server !", 2000 ).show( );

                refreshCamera( );
            }
        };
    }

    public void refreshCamera( )
    {
        if( mCameraView.getHolder().getSurface( ) == null )
        {
            return;
        }

        try
        {
            mCamera.stopPreview( );
        }
        catch( Exception e ) { }

        try
        {
            mCamera.setPreviewDisplay( mCameraView.getHolder() );
            mCamera.startPreview( );
        }
        catch( Exception e ) { }
    }

    private int findFrontFacingCamera( )
    {
        int cameraId = -1, numberOfCameras = Camera.getNumberOfCameras( );

        for( int i = 0; i < numberOfCameras; i++ )
        {
            Camera.CameraInfo info = new Camera.CameraInfo( );
            Camera.getCameraInfo( i, info );

            if( info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT )
            {
                cameraId = i;
                break;
            }
        }

        return cameraId;
    }

    public int uploadFile( String sourceFileUri )
    {
        int serverResponseCode = 0;

        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File sourceFile = new File( sourceFileUri );

        if( !sourceFile.isFile( ) )
        {
            return 0;
        }
        else
        {
            try
            {
                FileInputStream fileInputStream = new FileInputStream( sourceFile );
                URL url = new URL( upLoadServerUri );

                conn = ( HttpURLConnection ) url.openConnection( );
                conn.setDoInput( true );
                conn.setDoOutput( true );
                conn.setUseCaches( false );
                conn.setRequestMethod( "POST" );
                conn.setRequestProperty( "Connection", "Keep-Alive" );
                conn.setRequestProperty( "ENCTYPE", "multipart/form-data" );
                conn.setRequestProperty( "Content-Type", "multipart/form-data;boundary=" + boundary );
                conn.setRequestProperty( "uploaded_file", fileName );

                dos = new DataOutputStream( conn.getOutputStream( ) );

                dos.writeBytes( twoHyphens + boundary + lineEnd );
                dos.writeBytes( "Content-Disposition: form-data; name=\"uploaded_file\";filename="+ fileName + "" + lineEnd );
                dos.writeBytes( lineEnd );

                bytesAvailable = fileInputStream.available( );

                bufferSize = Math.min( bytesAvailable, maxBufferSize );
                buffer = new byte[ bufferSize ];

                bytesRead = fileInputStream.read( buffer, 0, bufferSize );

                while( bytesRead > 0 )
                {
                    dos.write( buffer, 0, bufferSize );
                    bytesAvailable = fileInputStream.available( );
                    bufferSize = Math.min( bytesAvailable, maxBufferSize );
                    bytesRead = fileInputStream.read( buffer, 0, bufferSize );
                }

                dos.writeBytes( lineEnd );
                dos.writeBytes( twoHyphens + boundary + twoHyphens + lineEnd );

                serverResponseCode = conn.getResponseCode( );

                fileInputStream.close( );
                dos.flush( );
                dos.close( );
            }
            catch( Exception e )
            {
                e.printStackTrace( );
            }

            return serverResponseCode;
        }
    }

    public void captureImage( View v ) throws IOException
    {
        mCamera.takePicture( null, null, jpegCallback );
    }

    public static boolean isInForeground( )
    {
        return mIsInForegroundMode;
    }

    public static boolean hasPermissions(Context context, String... permissions )
    {
        if( android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null )
        {
            for( String permission : permissions )
            {
                if( ActivityCompat.checkSelfPermission( context, permission ) != PackageManager.PERMISSION_GRANTED )
                {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onPause( )
    {
        super.onPause( );

        mCameraView.setVisibility( View.GONE );

        mIsInForegroundMode = false;
    }

    @Override
    public void onResume( )
    {
        super.onResume( );

        mCameraView.setVisibility( View.VISIBLE );

        mIsInForegroundMode = true;
    }

    @Override
    public void onDestroy( )
    {
        super.onDestroy( );

        mIsInForegroundMode = false;
    }
}

