package com.adamnickle.lockaway;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Adam on 8/8/2015.
 */
public class LockAway extends Application
{
    private static final String TAG = "LockAway";

    private static Context sContext;

    @Override
    public void onCreate()
    {
        super.onCreate();

        sContext = this;
    }

    public static void log( String log )
    {
        if( BuildConfig.DEBUG && sContext != null )
        {
            Log.d( TAG, log );
            LockAway.toast( log );
        }
    }

    public static void log( Exception ex )
    {
        String message = ex.getMessage();
        if( BuildConfig.DEBUG && sContext != null )
        {
            Log.e( TAG, message );
            LockAway.toast( message );
        }
        ex.printStackTrace();
    }

    public static void toast( final String message )
    {
        final Looper mainLooper = Looper.getMainLooper();
        if( Looper.getMainLooper().getThread() == Thread.currentThread() )
        {
            Toast.makeText( sContext, message, Toast.LENGTH_LONG ).show();
        }
        else
        {
            new Handler( mainLooper ).post( new Runnable()
            {
                @Override
                public void run()
                {
                    Toast.makeText( sContext, message, Toast.LENGTH_LONG ).show();
                }
            } );
        }
    }
}
