package adipslr.androidcameranotify;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

public class ACN_surfaceView extends SurfaceView implements SurfaceHolder.Callback
{
    private SurfaceHolder mHolder;
    private Camera mCamera;

    private List<Camera.Size> mSupportedPreviewSizes;
    private Camera.Size mPreviewSize;

    public ACN_surfaceView(Context context, Camera camera )
    {
        super( context );

        mCamera = camera;
        mCamera.setDisplayOrientation( 90 );

        mHolder = getHolder( );
        mHolder.addCallback( this );
        mHolder.setType( SurfaceHolder.SURFACE_TYPE_NORMAL );

        mSupportedPreviewSizes = mCamera.getParameters( ).getSupportedPreviewSizes( );
    }

    public void refreshCamera( )
    {
        if( mHolder.getSurface( ) == null )
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
            mCamera.setPreviewDisplay( mHolder );

            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
            mCamera.setParameters(parameters);
            mCamera.startPreview();

            mCamera.startPreview( );
        }
        catch( Exception e ) { }
    }

    @Override
    public void surfaceCreated( SurfaceHolder surfaceHolder )
    {
        try
        {
            mCamera.setPreviewDisplay( surfaceHolder );

            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
            mCamera.setParameters(parameters);
            mCamera.startPreview();

            mCamera.startPreview( );
        }
        catch( IOException e ) { }
    }

    @Override
    public void surfaceChanged( SurfaceHolder surfaceHolder, int i, int i2, int i3 )
    {
        refreshCamera( );
    }

    @Override
    public void surfaceDestroyed( SurfaceHolder surfaceHolder )
    {
        mCamera.stopPreview( );
        mCamera.release( );
    }

    @Override
    protected void onMeasure( int widthMeasureSpec, int heightMeasureSpec )
    {
        final int width = resolveSize( getSuggestedMinimumWidth( ), widthMeasureSpec );
        final int height = resolveSize( getSuggestedMinimumHeight( ), heightMeasureSpec );

        if( mSupportedPreviewSizes != null )
        {
            mPreviewSize = getOptimalPreviewSize( mSupportedPreviewSizes, width, height );
        }

        if( mPreviewSize != null )
        {
            float ratio;

            if( mPreviewSize.height >= mPreviewSize.width )
                ratio = ( float ) mPreviewSize.height / ( float ) mPreviewSize.width;
            else
                ratio = ( float ) mPreviewSize.width / ( float ) mPreviewSize.height;

            float camHeight = ( int ) ( width * ratio );
            float newCamHeight;
            float newHeightRatio;

            if( camHeight < height )
            {
                newHeightRatio = ( float ) height / ( float ) mPreviewSize.height;
                newCamHeight = ( newHeightRatio * camHeight );
                setMeasuredDimension( ( int ) ( width * newHeightRatio ), ( int ) newCamHeight );
            }
            else
            {
                newCamHeight = camHeight;
                setMeasuredDimension( width, ( int ) newCamHeight );
            }
        }
    }

    public Camera.Size getOptimalPreviewSize( List<Camera.Size> sizes, int w, int h )
    {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = ( double ) h / w;

        if( sizes == null )
            return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for( Camera.Size size : sizes )
        {
            double ratio = ( double ) size.width / size.height;

            if( Math.abs( ratio - targetRatio ) > ASPECT_TOLERANCE )
                continue;

            if( Math.abs( size.height - targetHeight ) < minDiff )
            {
                optimalSize = size;
                minDiff = Math.abs( size.height - targetHeight );
            }
        }

        if( optimalSize == null )
        {
            minDiff = Double.MAX_VALUE;

            for( Camera.Size size : sizes )
            {
                if( Math.abs( size.height - targetHeight ) < minDiff )
                {
                    optimalSize = size;
                    minDiff = Math.abs( size.height - targetHeight );
                }
            }
        }

        return optimalSize;
    }
}
