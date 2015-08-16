package com.adamnickle.lockaway;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import javax.crypto.CipherInputStream;

/**
 * Created by Adam on 8/15/2015.
 */
public final class Helper
{
    private Helper() { }

    public static void transition( FragmentManager fragmentManager, @IdRes int container, Fragment newFragment )
    {
        fragmentManager
                .beginTransaction()
                .addToBackStack( newFragment.getClass().getName() )
                .setCustomAnimations(
                        R.anim.fragment_slide_enter,
                        R.anim.fragment_slide_exit,
                        R.anim.fragment_slide_enter,
                        R.anim.fragment_slide_exit )
                .replace( container, newFragment )
                .commit();
    }

    private static final Comparator<File> sFileSorter = new Comparator<File>()
    {
        @Override
        public int compare( File lhs, File rhs )
        {
            if( lhs == null )
            {
                return rhs == null ? 0 : 1;
            }
            else if( rhs == null )
            {
                return -1;
            }
            else if( lhs.isDirectory() )
            {
                if( !rhs.isDirectory() )
                {
                    return -1;
                }
            }
            else if( rhs.isDirectory() )
            {
                return 1;
            }
            return lhs.getName().compareTo( rhs.getName() );
        }
    };

    public static void sort( File[] array )
    {
        Arrays.sort( array, sFileSorter );
    }

    public static Drawable getThumbnail( Resources res, String filename )
    {
        final int size = res.getDimensionPixelSize( R.dimen.thumbnail_size );
        Bitmap thumbnail = Helper.createScaledBitmap( filename, size, size );
        if( thumbnail != null )
        {
            return new BitmapDrawable( res, thumbnail );
        }
        thumbnail = ThumbnailUtils.createVideoThumbnail( filename, MediaStore.Images.Thumbnails.MICRO_KIND );
        if( thumbnail != null )
        {
            return new BitmapDrawable( res, thumbnail );
        }
        return res.getDrawable( R.drawable.ic_file );
    }

    public static String getExtension( String filename )
    {
        int index = filename.lastIndexOf( "." );
        if( index < 0 )
        {
            return "";
        }
        else
        {
            return filename.substring( index + 1 );
        }
    }

    public static String getExtension( File file )
    {
        return Helper.getExtension( file.getName() );
    }

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

    public static Bitmap createScaledBitmapFromLocked( Key key, String filename, int reqWidth, int reqHeight )
    {
        CipherInputStream input1 = null;
        CipherInputStream input2 = null;
        try
        {
            final BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            input1 = new CipherInputStream( new FileInputStream( filename ), key.getUnlocker() );
            BitmapFactory.decodeStream( input1, null, opts );
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
            input2 = new CipherInputStream( new FileInputStream( filename ), key.getUnlocker() );
            return BitmapFactory.decodeStream( input2, null, opts );
        }
        catch( IOException ex )
        {
            LockAway.log( ex );
        }
        finally
        {
            Helper.close( input1 );
            Helper.close( input2 );
        }
        return null;
    }

    public static void close( Closeable closeable )
    {
        if( closeable != null )
        {
            try
            {
                closeable.close();
            }
            catch( IOException ex )
            {
                LockAway.log( ex );
            }
        }
    }

    public static void checkOffsetAndCount( int arrayLength, int offset, int count )
    {
        if( ( offset | count ) < 0 || offset > arrayLength || arrayLength - offset < count )
        {
            throw new ArrayIndexOutOfBoundsException();
        }
    }
}
