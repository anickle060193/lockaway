package com.adamnickle.lockaway;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Adam on 8/12/2015.
 */
public abstract class BitmapHelper
{
    private BitmapHelper() { }

    public static Bitmap createScaledBitmap( String filename, int reqWidth, int reqHeight )
    {
        final BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile( filename, opts );
        if( opts.outHeight == -1 && opts.outWidth == -1 )
        {
            return null;
        }
        final int width = opts.outWidth;
        final int height = opts.outHeight;
        int sampleSize = 1;
        if( width > reqWidth || height > reqHeight )
        {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while( ( halfHeight / sampleSize ) > reqHeight
                && ( halfWidth / sampleSize ) > reqWidth )
            {
                sampleSize *= 2;
            }
        }
        opts.inJustDecodeBounds = false;
        opts.inSampleSize = sampleSize;
        return BitmapFactory.decodeFile( filename, opts );
    }
}
